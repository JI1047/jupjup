package com.example.Integrated.point.Dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PointFacilityDto {


    private String convenienceInfo;

    private String parkingInfo;
}
