package com.example.Integrated.RecycleHistory.Dto;

import com.example.Integrated.RecycleHistory.Entity.RecycleHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecycleHistoryResponseDto {
    private Long id;
    private String date;
    private String place;
    private String item;
    private int points;

    public static RecycleHistoryResponseDto fromEntity(RecycleHistory entity) {
        return RecycleHistoryResponseDto.builder()
                .id(entity.getId())
                .date(entity.getRecycledAt().toString()) // LocalDateTime → 문자열
                .place(entity.getCollectionPoint().getName())
                .item(entity.getItem().getName())
                .points(entity.getEarnedAmount())
                .build();
    }
}

