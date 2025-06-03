package com.example.Integrated.point.Dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PositionDto {

    private String name;

    private double latitude;

    private double longitude;

    private String lotAddress;

    private String tel;

    private String description;
}
