package com.example.Integrated.login.Entity.User;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "local_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LocalAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "localAccount", fetch = FetchType.LAZY)
    private User user;

    private String email;           // 로컬 로그인 이메일
    private String pwd;             // 비밀번호 (암호화 저장)



}
