package com.example.Integrated.point.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PointDto {

    private String name;

    private String region;

    private double latitude;

    private double longitude;

    private String description;

    private String tel;

    private String homepage;
}
