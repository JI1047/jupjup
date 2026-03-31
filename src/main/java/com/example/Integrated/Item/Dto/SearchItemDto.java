package com.example.Integrated.Item.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SearchItemDto {

    private Long id;
    private String name;

    private int unitPrice;
}
