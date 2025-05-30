package com.example.Integrated.point.Entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "point_address")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PointAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "pointAddress", fetch = FetchType.LAZY)
    private Point point;

    private String roadAddress;

    private String lotAddress;



}
