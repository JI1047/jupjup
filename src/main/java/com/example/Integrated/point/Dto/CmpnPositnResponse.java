package com.example.Integrated.point.Dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class CmpnPositnResponse {
    private Map<String, Object> header;


    private CmpnPositnBody body;
}
