package com.example.Integrated.RecycleHistory.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class CreateClaimRequestDto {

    private Long intendedUserId;
    private Long collectionPointId;
    private Long itemId;
    private int quantity;
    private int earnedAmount;


}
