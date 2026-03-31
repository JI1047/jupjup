package com.example.Integrated.RecycleHistory.Mapper;


import com.example.Integrated.Item.Entity.RecycleItem;
import com.example.Integrated.RecycleHistory.Dto.CreateClaimRequestDto;
import com.example.Integrated.RecycleHistory.Dto.RecycleHistoryRequestDto;
import com.example.Integrated.RecycleHistory.Entity.RecycleClaim;
import com.example.Integrated.RecycleHistory.Entity.RecycleHistory;
import com.example.Integrated.login.Entity.User.User;
import com.example.Integrated.point.Dto.CmpnPositnItem;
import com.example.Integrated.point.Dto.PointDto;
import com.example.Integrated.point.Entity.Point;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder

public class RecycleHistoryMapper {
    public static RecycleHistory toEntity(RecycleHistoryRequestDto request, User user, Point point, RecycleItem item) {
        return RecycleHistory.builder()
                .user(user)
                .collectionPoint(point)
                .item(item)
                .quantity(request.getQuantity())
                .earnedAmount(request.getEarnedAmount()) // 프론트에서 계산해서 넘긴 값
                .recycledAt(LocalDateTime.now())
                .build();
    }

    public static RecycleClaim toRecycleClaim(Point point, RecycleItem item, User intendedUser, CreateClaimRequestDto req,int expected , LocalDateTime expiresAt) {

        RecycleClaim claim = RecycleClaim.builder()
                .collectionPoint(point)
                .item(item)
                .quantity(req.getQuantity())
                .expectedAmount(expected)
                .intendedUser(intendedUser)
                .status(RecycleClaim.Status.PENDING)
                .expiresAt(expiresAt)
                .build();

        return claim;
    }
}
