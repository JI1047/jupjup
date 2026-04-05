package com.example.Integrated.point.Service;

import com.example.Integrated.Config.CacheNames;
import com.example.Integrated.Config.CacheMetricsService;
import com.example.Integrated.Config.VersionedCacheService;
import com.example.Integrated.Item.Entity.PointRecycleItem;
import com.example.Integrated.Item.Entity.RecycleItem;
import com.example.Integrated.Item.Repository.PointRecycleItemRepository;
import com.example.Integrated.Item.Repository.RecycleItemRepository;
import com.example.Integrated.point.Dto.CmpnPositnItem;
import com.example.Integrated.point.Dto.PointAddressDto;
import com.example.Integrated.point.Dto.PointDto;
import com.example.Integrated.point.Dto.PointFacilityDto;
import com.example.Integrated.point.Dto.PointHourDto;
import com.example.Integrated.point.Dto.PositionDto;
import com.example.Integrated.point.Dto.SearchItemPointDto;
import com.example.Integrated.point.Entity.Point;
import com.example.Integrated.point.Entity.PointAddress;
import com.example.Integrated.point.Entity.PointFacility;
import com.example.Integrated.point.Entity.PointHour;
import com.example.Integrated.point.Mapper.PointMapper;
import com.example.Integrated.point.Repository.PointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionApiService {

    private final PointRepository pointRepository;
    private final PointRecycleItemRepository pointRecycleItemRepository;
    private final RecycleItemRepository recycleItemRepository;
    private final CacheMetricsService cacheMetricsService;
    private final VersionedCacheService versionedCacheService;
    private final CacheWarmupService cacheWarmupService;

    private static final String BASE_URL = "https://apis.data.go.kr/B552584/kecoapi/rtrvlCmpnPositnService/getCmpnPositnInfo";
    private static final String SERVICE_KEY = "4DwueaIvHf5cKHAz%2FbT8HT1LecGpnNYrKJmTfDOZ4QOGIBw%2F73UJQJj5ND%2BGhovcV7%2BEzv5299wODKmVmgtoZw%3D%3D";

    public void importAllPoints() {
        System.out.println("=== importAllPoints ===");

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

                    CmpnPositnItem item = new CmpnPositnItem(
                            getStr(itemJson, "positnNm"),
                            getStr(itemJson, "positnRgnNm"),
                            getStr(itemJson, "positnPstnLat"),
                            getStr(itemJson, "positnPstnLot"),
                            getStr(itemJson, "positnIntdcCn"),
                            getStr(itemJson, "rprsTelnoCn"),
                            getStr(itemJson, "lnkgHmpgUrlAddr"),
                            getStr(itemJson, "monSalsHrExplnCn"),
                            getStr(itemJson, "tuesSalsHrExplnCn"),
                            getStr(itemJson, "wedSalsHrExplnCn"),
                            getStr(itemJson, "thurSalsHrExplnCn"),
                            getStr(itemJson, "friSalsHrExplnCn"),
                            getStr(itemJson, "satSalsHrExplnCn"),
                            getStr(itemJson, "sunSalsHrExplnCn"),
                            getStr(itemJson, "positnPstnAddExpln"),
                            getStr(itemJson, "prkMthdExpln"),
                            getStr(itemJson, "positnLotnoAddr"),
                            getStr(itemJson, "positnRdnmAddr")
                    );
                    PointDto pointDto = PointMapper.toPointDto(item);
                    List<PointHourDto> hourDtos = PointMapper.toPointHourDtoList(item);
                    PointFacilityDto facilityDto = PointMapper.toPointFacilityDto(item);
                    PointAddressDto addressDto = PointMapper.topointAddressDto(item);

                    PointAddress address = PointMapper.toPointAddress(addressDto);
                    PointFacility facility = PointMapper.toPointFacility(facilityDto);
                    List<PointHour> hours = PointMapper.toPointHour(hourDtos);

                    Point point = PointMapper.toPoint(pointDto, facility, address, hours);
                    pointRepository.save(point);
                }

                pageNo++;
            } catch (Exception e) {
                log.error("Failed to import points page {}", pageNo, e);
                refreshSucceeded = false;
                break;
            }
        }

        if (refreshSucceeded) {
            String newVersion = versionedCacheService.createNextVersion();
            cacheWarmupService.warmPointsMain(newVersion);
            versionedCacheService.switchToVersion(CacheNames.POINTS_MAIN, newVersion);
            cacheMetricsService.recordVersionSwitch(CacheNames.POINTS_MAIN);
            log.info("Switched pointsMain cache to version {}", newVersion);
        }
    }

    @Scheduled(cron = "${app.scheduler.points-refresh-cron}")
    public void scheduledImportAllPoints() {
        importAllPoints();
    }

    private String getStr(JSONObject obj, String key) {
        return obj.containsKey(key) ? (String) obj.get(key) : "";
    }

    @Transactional(readOnly = true)
    public List<PositionDto> loadPositions() {
        List<Point> points = pointRepository.findTop165WithAllFetch();
        List<PositionDto> dtos = new ArrayList<>();
        for (Point point : points) {
            List<RecycleItem> items = point.getRecycleItems()
                    .stream()
                    .map(PointRecycleItem::getRecycleItem)
                    .toList();
            PositionDto dto = PointMapper.toPositionDto(point, items);
            dtos.add(dto);
        }
        return dtos;
    }

    @Cacheable(cacheNames = CacheNames.POINTS_BY_ITEM_IDS, keyGenerator = "keyGenerator")
    public List<SearchItemPointDto> findPointsByItemIds(List<Long> itemIds) {
        List<Long> pointsId = pointRepository.findPointIdsThatCollectAllItems(itemIds, itemIds.size());
        List<SearchItemPointDto> dtos = new ArrayList<>();
        for (Long pointId : pointsId) {
            Point point = pointRepository.findById(pointId)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown pointId: " + pointId));
            dtos.add(PointMapper.toSearchItemPointDtoList(point));
        }
        return dtos;
    }
}
