package com.example.Integrated.point.Dto;


import com.example.Integrated.Item.Entity.RecycleItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

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

    private List<String> itemNames;

}
