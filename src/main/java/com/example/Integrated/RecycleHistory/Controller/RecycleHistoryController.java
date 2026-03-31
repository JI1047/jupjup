package com.example.Integrated.RecycleHistory.Controller;

import com.example.Integrated.RecycleHistory.Dto.*;
import com.example.Integrated.RecycleHistory.Entity.RecycleHistory;
import com.example.Integrated.RecycleHistory.Service.RecycleClaimService;
import com.example.Integrated.RecycleHistory.Service.RecycleHistoryService;
import com.example.Integrated.login.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recycle-history")
public class RecycleHistoryController {
    private final RecycleHistoryService historyService;
    private final RecycleClaimService recycleClaimService;
    private final JwtProvider jwtProvider;

    @PostMapping("/save")
    public ResponseEntity<RecycleHistory> createHistory(@RequestBody RecycleHistoryRequestDto request) {
        RecycleHistory saved = historyService.saveHistory(request);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/claims")
    public ResponseEntity<CreateClaimResponseDto> create(@RequestBody CreateClaimRequestDto request) {
        return recycleClaimService.createClaim(request);
    }
    @GetMapping("/verify")
    public ResponseEntity<CreateClaimVerifyResponseDto> verifyClaim(
            @RequestParam String claimId,
            @RequestParam String signature
    ) {
        CreateClaimVerifyResponseDto result = recycleClaimService.verifyClaim(claimId, signature);
        return ResponseEntity.ok(result);
    }

    // ✅ 2) 실제 저장 처리
    @PostMapping("/save-claim")
    public ResponseEntity<String> saveClaim(@RequestBody Map<String, String> request) {
        String claimId = request.get("claimId");
        String signature = request.get("signature");
        recycleClaimService.saveClaim(claimId, signature);
        return ResponseEntity.ok("✅ 재활용 내역이 저장되었습니다.");
    }
    @GetMapping("/user")
    public ResponseEntity<?> getUserRecycleHistory(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "JWT 누락"));
        }

        String token = authHeader.substring(7);
        String userIdStr = jwtProvider.getUserIdFromToken(token);
        Long userId = Long.valueOf(userIdStr);

        List<RecycleHistoryResponseDto> historyList = historyService.findByUserId(userId);
        return ResponseEntity.ok(historyList);
    }

}
