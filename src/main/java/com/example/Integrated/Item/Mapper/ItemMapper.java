package com.example.Integrated.Item.Mapper;

import com.example.Integrated.Item.Dto.SearchItemDto;
import com.example.Integrated.Item.Entity.RecycleItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.core.annotation.MergedAnnotations;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Builder
public class ItemMapper {

    public static List<SearchItemDto> toSearchItemDto(List<RecycleItem> items) {

        List<SearchItemDto> searchItemDtos = new ArrayList<>();
        for (RecycleItem item : items) {
            SearchItemDto dto = new SearchItemDto(item.getId(),item.getName(),item.getUnitPrice());
            searchItemDtos.add(dto);

        }
        return searchItemDtos;
    }
}
