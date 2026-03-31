package com.example.Integrated.RecycleHistory.Dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class CreateClaimResponseDto {
    String claimId;
    String qrUrl;
    LocalDateTime expiresAt;
}
