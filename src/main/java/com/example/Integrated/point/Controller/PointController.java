package com.example.Integrated.point.Controller;

import com.example.Integrated.point.Service.PositionApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PointController {

    private final PositionApiService positionApiService;

    @GetMapping("/api/test-import")
    public String testImport() {
        positionApiService.importAllPoints();
        return "✅ importAllPoints 수동 호출 완료";
    }
}
