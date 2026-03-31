package com.example.Integrated.point.Entity;

import jakarta.persistence.*;
import lombok.*;



@Entity
@Table(name = "point_hour")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PointHour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @Enumerated(EnumType.STRING)
    private DayOfWeek day;

    private String salsHr;

}
