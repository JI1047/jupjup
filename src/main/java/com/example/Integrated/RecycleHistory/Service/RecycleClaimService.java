package com.example.Integrated.RecycleHistory.Service;

import com.example.Integrated.Item.Entity.RecycleItem;
import com.example.Integrated.Item.Repository.RecycleItemRepository;
import com.example.Integrated.RecycleHistory.Dto.CreateClaimRequestDto;
import com.example.Integrated.RecycleHistory.Dto.CreateClaimResponseDto;
import com.example.Integrated.RecycleHistory.Dto.CreateClaimVerifyResponseDto;
import com.example.Integrated.RecycleHistory.Entity.RecycleClaim;
import com.example.Integrated.RecycleHistory.Entity.RecycleHistory;
import com.example.Integrated.RecycleHistory.Mapper.RecycleHistoryMapper;
import com.example.Integrated.RecycleHistory.Repository.RecycleClaimRepository;
import com.example.Integrated.RecycleHistory.Repository.RecycleHistoryRepository;
import com.example.Integrated.RecycleHistory.util.HmacSigner;
import com.example.Integrated.login.Entity.User.User;
import com.example.Integrated.login.Entity.User.UserDetail;
import com.example.Integrated.login.Repository.User.UserDetailRepository;
import com.example.Integrated.login.Repository.User.UserRepository;
import com.example.Integrated.point.Entity.Point;
import com.example.Integrated.point.Repository.PointRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RecycleClaimService {

    private final RecycleClaimRepository claimRepo;
    private final RecycleItemRepository itemRepo;
    private final PointRepository pointRepo;
    private final UserRepository userRepo;
    private final HmacSigner signer;
    private final RecycleHistoryRepository recycleHistoryRepository;
    private final UserDetailRepository userDetailRepository;

    @Value("${app.qr.base-url:http://192.168.123.104:3000/r/claim}")
    private String qrBaseUrl;

    public ResponseEntity<CreateClaimResponseDto> createClaim(CreateClaimRequestDto req) {
        // 1️⃣ 관련 엔티티 조회
        RecycleItem item = itemRepo.findById(req.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid item ID"));
        Point point = pointRepo.findById(req.getCollectionPointId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid collection point ID"));

        User intendedUser = null;
        if (req.getIntendedUserId() != null) {
            intendedUser = userRepo.findById(req.getIntendedUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        }

        int expected = item.getUnitPrice() * req.getQuantity();

        // 3️⃣ 만료 시간 (5분 후)
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);

        // 4️⃣ 클레임 엔티티 생성
        RecycleClaim claim = RecycleHistoryMapper.toRecycleClaim(point, item, intendedUser, req, expected,expiresAt);

        claim = claimRepo.save(claim);

        // 5️⃣ HMAC 서명 생성
        String message = buildSignaturePayload(claim);
        String signature = signer.sign(message);
        claim.setSignature(signature);
        claimRepo.save(claim);

        // 6️⃣ QR URL 생성
        String qrUrl = qrBaseUrl + "?c=" + claim.getId() + "&s=" + signature;

        CreateClaimResponseDto createClaimResponseDto = new CreateClaimResponseDto(claim.getId().toString(), qrUrl, expiresAt);
        return ResponseEntity.ok().body(createClaimResponseDto);
    }



    private String buildSignaturePayload(RecycleClaim c) {
        long expEpoch = c.getExpiresAt().atZone(java.time.ZoneId.systemDefault()).toEpochSecond();
        return c.getId() + "." + expEpoch + "." +
                c.getItem().getId() + "." + c.getCollectionPoint().getId() + "." +
                c.getQuantity() + "." + c.getExpectedAmount();
    }

    // ✅ QR 확인용 — React가 처음 /verify 호출할 때
    public CreateClaimVerifyResponseDto verifyClaim(String claimId, String signature) {
        RecycleClaim claim = claimRepo.findById(UUID.fromString(claimId))
                .orElseThrow(() -> new IllegalArgumentException("❌ 클레임을 찾을 수 없습니다."));

        // (추가 검증 로직은 생략: signature 유효성 등)
        return CreateClaimVerifyResponseDto.builder()
                .collectionPointName(claim.getCollectionPoint().getName())
                .itemName(claim.getItem().getName())
                .quantity(claim.getQuantity())
                .expectedAmount(claim.getExpectedAmount())
                .build();
    }

    // ✅ "확인" 버튼 클릭 시 실제 저장 처리
    @Transactional
    public void saveClaim(String claimId, String signature) {
        RecycleClaim claim = claimRepo.findById(UUID.fromString(claimId))
                .orElseThrow(() -> new IllegalArgumentException("❌ 클레임을 찾을 수 없습니다."));

        UserDetail userDetail = userDetailRepository
                .findByUserId(claim.getIntendedUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("❌ UserDetail을 찾을 수 없습니다."));                ;
        userDetail.updatePoint(claim.getExpectedAmount());
        // 실제 재활용 내역 저장
        RecycleHistory history = RecycleHistory.builder()
                .user(claim.getIntendedUser())
                .collectionPoint(claim.getCollectionPoint())
                .item(claim.getItem())
                .quantity(claim.getQuantity())
                .earnedAmount(claim.getExpectedAmount())
                .recycledAt(LocalDateTime.now())
                .build();

        recycleHistoryRepository.save(history);

        // (선택) 클레임 상태 변경
        claim.setStatus(RecycleClaim.Status.USED);
        claimRepo.save(claim);
    }
}
