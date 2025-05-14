package com.example.Integrated.login.Dto.Signup;

import com.example.Integrated.login.Entity.User.SocialProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UpdatedDto {


    private String snsId;
    private SocialProvider provider;

}
