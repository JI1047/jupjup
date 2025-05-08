package com.example.Integrated.login.Dto.User;


import com.example.Integrated.login.Entity.User.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserDetailDto {
    private String name;
    private String phone;
    private Enum<Gender> gender;
    private String birth;
    private String address;
}
