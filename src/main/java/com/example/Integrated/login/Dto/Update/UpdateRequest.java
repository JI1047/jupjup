package com.example.Integrated.login.Dto.Update;

import com.example.Integrated.login.Entity.User.Gender;
import com.example.Integrated.login.Entity.User.LoginType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UpdateRequest {


    private String name;

    private String phone;

    private Gender gender;

    private String birth;

    private String address;

    private String password;

    private LoginType loginType;
}
