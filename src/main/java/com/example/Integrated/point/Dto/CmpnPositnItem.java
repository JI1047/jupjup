package com.example.Integrated.point.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor

public class CmpnPositnItem {

    //거점명
    private final String positnNm;

    //지역명
    private final String positnRgnNm;

    //경도
    private final String positnPstnLat;

    //위도
    private final String positnPstnLot;

    //거점 한 줄 소개
    private final String positnIntdcCn;

    //전화번호
    private final String rprsTelnoCn;

    //연결 홈페이지
    private final String lnkgHmpgUrlAddr;

    //월요일 운영시간
    private final String monSalsHrExplnCn;

    //화요일 운영시간
    private final String tuesSalsHrExplnCn;

    //수요일 운영시간
    private final String wedSalsHrExplnCn;

    //목요일 운영시간
    private final String thurSalsHrExplnCn;

    //금요일 운영시간
    private final String friSalsHrExplnCn;

    //토요일 운영시간
    private final String satSalsHrExplnCn;

    //일요일 운영시간
    private final String sunSalsHrExplnCn;

    //찾아오는길 설명
    private final String positnPstnAddExpln;

    //주차장 설명
    private final String prkMthdExpln;

    //지번주소

    private final String positnLotnoAddr;

    //도로명주소
    private final String positnRdnmAddr;

}
