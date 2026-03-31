package com.example.Integrated.RecycleHistory.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RecycleHistoryRequestDto {
    private Long userId;
    private Long pointId;
    private Long itemId;
    private int quantity;
    private int earnedAmount;
}
