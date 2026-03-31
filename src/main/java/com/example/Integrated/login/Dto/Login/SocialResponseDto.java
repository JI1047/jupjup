package com.example.Integrated.login.Dto.Login;


import com.example.Integrated.login.Entity.User.Gender;
import com.example.Integrated.login.Entity.User.User;
import com.example.Integrated.login.Entity.User.UserDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SocialResponseDto {

    private String jwt;

    private String nickname;

    private String name;

    private String phone;

    private Enum<Gender> gender;

    private String birth;

    private String address;
}
