package com.example.Integrated.login.Dto.Signup;


import com.example.Integrated.login.Entity.User.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class LocalSignupDto {

    private String nickname;
    private String email;
    private String password;
    private LocalDate createdAt;

    private String name;
    private String phone;
    private Gender gender;
    private String birth;
    private String address;


}
