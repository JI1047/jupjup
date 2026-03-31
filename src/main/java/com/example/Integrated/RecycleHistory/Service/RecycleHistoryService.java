package com.example.Integrated.RecycleHistory.Service;

import com.example.Integrated.Item.Entity.RecycleItem;
import com.example.Integrated.Item.Repository.RecycleItemRepository;
import com.example.Integrated.RecycleHistory.Dto.RecycleHistoryRequestDto;
import com.example.Integrated.RecycleHistory.Dto.RecycleHistoryResponseDto;
import com.example.Integrated.RecycleHistory.Entity.RecycleHistory;
import com.example.Integrated.RecycleHistory.Mapper.RecycleHistoryMapper;
import com.example.Integrated.RecycleHistory.Repository.RecycleHistoryRepository;
import com.example.Integrated.login.Entity.User.User;
import com.example.Integrated.login.Repository.User.UserRepository;
import com.example.Integrated.point.Entity.Point;
import com.example.Integrated.point.Repository.PointRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecycleHistoryService {

    private final RecycleHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final RecycleItemRepository itemRepository;

    @Transactional
    public RecycleHistory saveHistory(RecycleHistoryRequestDto request) {
        // 1. 엔티티 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Point point = pointRepository.findById(request.getPointId())
                .orElseThrow(() -> new IllegalArgumentException("거점을 찾을 수 없습니다."));

        RecycleItem item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("재활용 품목을 찾을 수 없습니다."));



        // 3. 엔티티 생성
        RecycleHistory history = RecycleHistoryMapper.toEntity(request, user, point, item);

        // 4. DB 저장
        return historyRepository.save(history);
    }
    public List<RecycleHistoryResponseDto> findByUserId(Long userId) {
        List<RecycleHistory> historyList = historyRepository.findByUserId(userId);
        return historyList.stream()
                .map(RecycleHistoryResponseDto::fromEntity)
                .toList();
    }

}

