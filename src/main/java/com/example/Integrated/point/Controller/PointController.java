package com.example.Integrated.point.Controller;

import com.example.Integrated.point.Dto.PositionDto;
import com.example.Integrated.point.Service.CachedPointQueryService;
import com.example.Integrated.point.Service.PositionApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/map")
public class PointController {

    private final PositionApiService positionApiService;
    private final CachedPointQueryService cachedPointQueryService;

    @GetMapping("/test-import")
    public String testImport() {
        positionApiService.importAllPoints();
        return "importAllPoints completed";
    }

    @GetMapping("/test-clear-cache")
    public String testClearCache() {
        cachedPointQueryService.evictPointsMain();
        return "pointsMain cache cleared";
    }

    @GetMapping("/main")
    public List<PositionDto> getPosition() {
        return cachedPointQueryService.getPosition();
    }
}
