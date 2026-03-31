package com.example.Integrated.login.Dto.Login;


import com.example.Integrated.login.Dto.User.UserDetailDto;
import com.example.Integrated.login.Dto.User.UserDto;
import com.example.Integrated.login.Entity.User.Gender;
import com.example.Integrated.login.Entity.User.LoginType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LoginSuccessDto {



    private String name;

    private String phone;

    private Enum<Gender> gender;

    private String birth;

    private String address;

    private int point;

    private Enum<LoginType> type;

}
