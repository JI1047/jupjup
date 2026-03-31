package com.example.Integrated.point.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "point_facility")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PointFacility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String convenienceInfo;

    private String parkingInfo;


}
