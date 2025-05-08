package com.example.Integrated.login.Dto.User;

import com.example.Integrated.login.Entity.User.LocalAccount;
import com.example.Integrated.login.Entity.User.LoginType;
import com.example.Integrated.login.Entity.User.SocialAccount;
import com.example.Integrated.login.Entity.User.UserDetail;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class UserDto {


    private String nickname;

    private String profileImg;

    private Enum<LoginType> loginType;

    private LocalDateTime createdAt;




}
