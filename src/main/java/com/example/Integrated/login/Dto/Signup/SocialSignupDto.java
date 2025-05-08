package com.example.Integrated.login.Dto.Signup;

import com.example.Integrated.login.Entity.User.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SocialSignupDto {

    private String nickname;
    private String name;
    private String phone;
    private Gender gender;
    private String birth;
    private String address;

}
