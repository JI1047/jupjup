package com.example.Integrated.RecycleHistory.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateClaimVerifyResponseDto {
    private String collectionPointName;
    private String itemName;
    private int quantity;
    private int expectedAmount;
}
