package com.example.Integrated.point.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "point")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String region;

    private double latitude;

    private double longitude;

    private String description;

    private String tel;

    private String homepage;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "point_address_id")
    private PointAddress pointAddress;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "point_facility_id")
    private PointFacility pointFacility;

    @OneToMany(mappedBy = "point", cascade = CascadeType.ALL)
    private List<PointHour> operatingHours;

}
