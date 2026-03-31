package com.example.Integrated.login.Entity.User;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "social_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "socialAccount", fetch = FetchType.LAZY)
    private User user;

    private String snsId;

    @Enumerated(EnumType.STRING)
    private SocialProvider provider;



}