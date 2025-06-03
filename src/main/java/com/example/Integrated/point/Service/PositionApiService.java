package com.example.Integrated.point.Service;


import com.example.Integrated.point.Dto.*;
import com.example.Integrated.point.Entity.Point;
import com.example.Integrated.point.Entity.PointAddress;
import com.example.Integrated.point.Entity.PointFacility;
import com.example.Integrated.point.Entity.PointHour;
import com.example.Integrated.point.Mapper.PointMapper;
import com.example.Integrated.point.Repository.PointRepository;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionApiService {

    private final PointRepository pointRepository;

    private static final String BASE_URL = "https://apis.data.go.kr/B552584/kecoapi/rtrvlCmpnPositnService/getCmpnPositnInfo";
    private static final String SERVICE_KEY = "4DwueaIvHf5cKHAz%2FbT8HT1LecGpnNYrKJmTfDOZ4QOGIBw%2F73UJQJj5ND%2BGhovcV7%2BEzv5299wODKmVmgtoZw%3D%3D";


    public void importAllPoints() {
        System.out.println("=== 🔄 importAllPoints 실행됨 ===");

        int pageNo = 1;

        while (true) {

            try {
                String urlStr = BASE_URL +
                        "?serviceKey=" + SERVICE_KEY +
                        "&returnType=json" +
                        "&pageNo=" + pageNo +
                        "&numOfRows=10";
                System.out.println(urlStr);

                URL url = new URL(urlStr);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                String result = reader.readLine();

                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(result);
                JSONObject body = (JSONObject) json.get("body");
                JSONArray items = (JSONArray) body.get("items");

                if (items == null || items.isEmpty()) break;

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
                e.printStackTrace();
                break;
            }
        }
    }
    private String getStr(JSONObject obj, String key) {
        System.out.println((String) obj.get(key));
        return obj.containsKey(key) ? (String) obj.get(key) : "";
    }

    public List<PositionDto> getPosition() {

        List<Point> points = pointRepository.findTop18ByOrderByIdAsc();
        List<PositionDto> dtos=new ArrayList<>();
        for(Point point : points) {
            PositionDto positionDto = PointMapper.toPositionDto(point);
            dtos.add(positionDto);

        }
        return dtos;
    }


}