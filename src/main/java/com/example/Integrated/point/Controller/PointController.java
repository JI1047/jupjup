package com.example.Integrated.point.Controller;

import com.example.Integrated.point.Dto.PositionDto;
import com.example.Integrated.point.Service.PositionApiService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/map")
public class PointController {

    private final PositionApiService positionApiService;

    @GetMapping("/api/test-import")
    public String testImport() {
        positionApiService.importAllPoints();
        return "✅ importAllPoints 수동 호출 완료";
    }

    @GetMapping("/main")
    public List<PositionDto> getPosition(){
        return positionApiService.getPosition();
    }
}
