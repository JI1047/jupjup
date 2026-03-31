package com.example.Integrated.point.Dto;

import com.example.Integrated.point.Entity.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PointHourDto {

    private DayOfWeek day;

    private String SalsHr;


}
