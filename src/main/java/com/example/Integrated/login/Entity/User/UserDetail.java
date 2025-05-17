package com.example.Integrated.login.Entity.User;

import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

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

    @OneToOne(mappedBy = "userDetail", fetch = FetchType.LAZY)
    private User user;

    private String name;
    private String phone;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String birth;
    private String address;


}
