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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_id")  // FK
    private Point point;


    @Enumerated(EnumType.STRING)
    private DayOfWeek day;

    private String openHour;

    private String closeHour;
}
