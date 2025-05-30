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



    private String roadAddress;

    private String lotAddress;



}
