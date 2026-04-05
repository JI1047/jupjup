package com.example.Integrated.Item.Service;

import com.example.Integrated.Config.CacheNames;
import com.example.Integrated.Config.CacheMetricsService;
import com.example.Integrated.Config.VersionedCacheService;
import com.example.Integrated.Item.Dto.PRI;
import com.example.Integrated.Item.Dto.SearchItemDto;
import com.example.Integrated.Item.Entity.PointRecycleItem;
import com.example.Integrated.Item.Entity.RecycleItem;
import com.example.Integrated.Item.Mapper.ItemMapper;
import com.example.Integrated.Item.Repository.PointRecycleItemRepository;
import com.example.Integrated.Item.Repository.RecycleItemRepository;
import com.example.Integrated.point.Entity.Point;
import com.example.Integrated.point.Repository.PointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private static final String BASE_URL = "https://apis.data.go.kr/B552584/kecoapi/rtrvlCmpnPositnService/getCmpnPositnInfo";
    private static final String SERVICE_KEY = "4DwueaIvHf5cKHAz%2FbT8HT1LecGpnNYrKJmTfDOZ4QOGIBw%2F73UJQJj5ND%2BGhovcV7%2BEzv5299wODKmVmgtoZw%3D%3D";

    private final PointRepository pointRepository;
    private final RecycleItemRepository recycleItemRepository;
    private final PointRecycleItemRepository pointRecycleItemRepository;
    private final CacheMetricsService cacheMetricsService;
    private final VersionedCacheService versionedCacheService;
    private final com.example.Integrated.point.Service.CacheWarmupService cacheWarmupService;

    @CacheEvict(
            cacheNames = {
                    CacheNames.ITEMS_SEARCH,
                    CacheNames.POINTS_BY_ITEM_IDS
            },
            allEntries = true
    )
    public void importAllItems() {
        System.out.println("=== importAllItems ===");

        int pageNo = 1;
        boolean refreshSucceeded = true;

        while (true) {
            try {
                String urlStr = BASE_URL +
                        "?serviceKey=" + SERVICE_KEY +
                        "&returnType=json" +
                        "&pageNo=" + pageNo +
                        "&numOfRows=10";

                URL url = new URL(urlStr);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                String result = reader.readLine();

                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(result);
                JSONObject body = (JSONObject) json.get("body");
                JSONArray items = (JSONArray) body.get("items");

                if (items == null || items.isEmpty()) {
                    break;
                }

                for (Object itemObj : items) {
                    JSONObject itemJson = (JSONObject) itemObj;

                    PRI item = new PRI(
                            getStr(itemJson, "positnNm"),
                            getStr(itemJson, "clctItemCn")
                    );
                    Point point = pointRepository.findByName(item.getPositnNm());
                    if (point == null) {
                        continue;
                    }

                    String[] itemNames = item.getClctItemCn().split("\n");
                    for (String itemName : itemNames) {
                        RecycleItem recycleItem = recycleItemRepository.findByName(itemName.trim());
                        if (recycleItem == null) {
                            continue;
                        }

                        PointRecycleItem pri = PointRecycleItem.builder()
                                .point(point)
                                .recycleItem(recycleItem)
                                .build();

                        pointRecycleItemRepository.save(pri);
                    }
                }

                pageNo++;
            } catch (Exception e) {
                log.error("Failed to import items page {}", pageNo, e);
                refreshSucceeded = false;
                break;
            }
        }

        if (refreshSucceeded) {
            String newVersion = versionedCacheService.createNextVersion();
            cacheWarmupService.warmPointsMain(newVersion);
            versionedCacheService.switchToVersion(CacheNames.POINTS_MAIN, newVersion);
            cacheMetricsService.recordVersionSwitch(CacheNames.POINTS_MAIN);
            log.info("Switched pointsMain cache to version {} after item refresh", newVersion);
        }
    }

    @Scheduled(cron = "${app.scheduler.items-refresh-cron}")
    public void scheduledImportAllItems() {
        importAllItems();
    }

    private String getStr(JSONObject obj, String key) {
        return obj.containsKey(key) ? (String) obj.get(key) : "";
    }

    @Cacheable(cacheNames = CacheNames.ITEMS_SEARCH)
    public List<SearchItemDto> getItem() {
        List<RecycleItem> recycleItems = recycleItemRepository.findAll();
        return ItemMapper.toSearchItemDto(recycleItems);
    }
}
