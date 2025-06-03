package com.example.Integrated.point.Mapper;


import com.example.Integrated.point.Dto.*;
import com.example.Integrated.point.Entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@AllArgsConstructor
@Builder

public class PointMapper {

    public static PointDto toPointDto(CmpnPositnItem item) {

        System.out.println(item.getPositnPstnLat());
        System.out.println(item.getPositnPstnLot());
        return PointDto.builder()
                .name(item.getPositnNm())
                .region(item.getPositnRgnNm())
                .latitude(parseDouble(item.getPositnPstnLat()))
                .longitude(parseDouble(item.getPositnPstnLot()))
                .description(item.getPositnIntdcCn())
                .tel(item.getRprsTelnoCn())
                .homepage(item.getLnkgHmpgUrlAddr())
                .build();
    }
    public static List<PointHourDto> toPointHourDtoList(CmpnPositnItem item) {
        return List.of(
                new PointHourDto(DayOfWeek.MON, item.getMonSalsHrExplnCn()),
                new PointHourDto(DayOfWeek.THU, item.getTuesSalsHrExplnCn()),
                new PointHourDto(DayOfWeek.WED, item.getWedSalsHrExplnCn()),
                new PointHourDto(DayOfWeek.THU, item.getThurSalsHrExplnCn()),
                new PointHourDto(DayOfWeek.FRI, item.getFriSalsHrExplnCn()),
                new PointHourDto(DayOfWeek.SAT, item.getSatSalsHrExplnCn()),
                new PointHourDto(DayOfWeek.SUN, item.getSunSalsHrExplnCn())
        );
    }

    public static PointFacilityDto toPointFacilityDto(CmpnPositnItem item) {
        return PointFacilityDto.builder()
                .convenienceInfo(item.getPositnPstnAddExpln())
                .parkingInfo(item.getPrkMthdExpln())
                .build();
    }

    public static PointAddressDto topointAddressDto(CmpnPositnItem item) {
        return PointAddressDto.builder()
                .lotAddress(item.getPositnLotnoAddr())
                .roadAddress(item.getPositnRdnmAddr())
                .build();
    }
    public static PointAddress toPointAddress(PointAddressDto addressDto) {

        return PointAddress.builder()
                .roadAddress(addressDto.getRoadAddress())
                .lotAddress(addressDto.getLotAddress())
                .build();
    }
    public static PointFacility toPointFacility(PointFacilityDto facilityDto) {
        return PointFacility.builder()
                .convenienceInfo(facilityDto.getConvenienceInfo())
                .parkingInfo(facilityDto.getParkingInfo())
                .build();
    }
    public static List<PointHour> toPointHour(List<PointHourDto> hourDtoList) {
        return hourDtoList.stream()
                .map(dto -> PointHour.builder()
                        .day(dto.getDay())
                        .salsHr(dto.getSalsHr())
                        .build())
                .toList();
    }

    public static Point toPoint(PointDto pointDto, PointFacility facility, PointAddress address, List<PointHour> hourList) {
        return Point.builder()
                .name(pointDto.getName())
                .region(pointDto.getRegion())
                .latitude(pointDto.getLatitude())
                .longitude(pointDto.getLongitude())
                .description(pointDto.getDescription())
                .tel(pointDto.getTel())
                .homepage(pointDto.getHomepage())
                .pointFacility(facility)
                .pointAddress(address)
                .operatingHours(hourList)
                .build();
    }

    public static Double parseDouble(String value) {
        try {
            if (value == null || value.isBlank()) return 0.0;
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public static PositionDto toPositionDto(Point point) {
        return PositionDto.builder()
                .name(point.getName())
                .latitude(point.getLatitude())
                .longitude(point.getLongitude())
                .lotAddress(point.getPointAddress().getLotAddress())
                .tel(point.getTel())
                .description(point.getDescription())
                .build();
    }

}
