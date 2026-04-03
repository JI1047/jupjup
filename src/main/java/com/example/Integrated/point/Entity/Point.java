package com.example.Integrated.point.Entity;

import com.example.Integrated.Item.Entity.PointRecycleItem;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "point_id")
    private List<PointHour> operatingHours;

    @Builder.Default
    @OneToMany(mappedBy = "point", cascade = CascadeType.ALL)
    private List<PointRecycleItem> recycleItems = new ArrayList<>();
}
