package com.example.Integrated.login.Dto.Login;


import com.example.Integrated.login.Entity.User.User;
import com.example.Integrated.login.Entity.User.UserDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SocialResponseDto {
    private User user;
    private String jwt;
    private UserDetail userDetail;
}
