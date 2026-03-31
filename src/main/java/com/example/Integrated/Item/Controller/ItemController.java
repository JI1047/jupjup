package com.example.Integrated.Item.Controller;

import com.example.Integrated.Item.Dto.ItemSearchRequest;
import com.example.Integrated.Item.Dto.SearchItemDto;
import com.example.Integrated.Item.Service.ItemService;
import com.example.Integrated.point.Dto.SearchItemPointDto;
import com.example.Integrated.point.Service.PositionApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/item")
public class ItemController {

    private final ItemService itemService;

    private final PositionApiService positionApiService;

    @GetMapping("/test-import")
    public String testImport() {
        itemService.importAllItems();
        return "✅ importAllPoints 수동 호출 완료";
    }

    @GetMapping("/search")
    public List<SearchItemDto> getSearchItem() {
        return itemService.getItem();
    }

    @PostMapping("/item-search")
    public List<SearchItemPointDto> getPoints(@RequestBody ItemSearchRequest request) {
        return positionApiService.findPointsByItemIds(request.getItemIds());
    }
}
