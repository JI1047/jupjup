package com.example.Integrated.login.Entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_detail")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")   // FK
    private User user;

    private String name;
    private String phone;
    private Enum<Gender> gender;
    private String birth;
    private String address;


}
