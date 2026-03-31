package com.example.Integrated.point.Dto;

import com.example.Integrated.Item.Entity.RecycleItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class SearchItemPointDto {

    private String name;

    private String lotAddress;

    private String tel;

}
