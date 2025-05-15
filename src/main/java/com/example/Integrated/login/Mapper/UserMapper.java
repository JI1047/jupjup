package com.example.Integrated.login.Mapper;

import com.example.Integrated.login.Dto.Login.LocalResponseDto;
import com.example.Integrated.login.Dto.Login.LoginSuccessDto;
import com.example.Integrated.login.Dto.Login.SocialResponseDto;
import com.example.Integrated.login.Dto.Signup.LocalSignupDto;
import com.example.Integrated.login.Dto.Signup.SocialSignupDto;
import com.example.Integrated.login.Dto.User.UserDetailDto;
import com.example.Integrated.login.Dto.User.UserDto;
import com.example.Integrated.login.Entity.User.*;
import com.example.Integrated.login.jwt.CustomLocalUser;
import com.example.Integrated.login.jwt.CustomUser;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
public class UserMapper {






    public static User toLocalUser(UserDetail userDetail,LocalAccount localAccount) {
        return User.builder()
                .userDetail(userDetail)
                .localAccount(localAccount)
                .socialAccount(null)
                .loginType(LoginType.LOCAL)
                .createdAt(LocalDateTime.now())
                .build();
    }


    public static LocalAccount toLocalAccount(LocalSignupDto dto, String encodedPassword) {

        return LocalAccount.builder()
                .email(dto.getEmail())
                .pwd(encodedPassword)
                .build();
    }

    public static UserDetail toUserDetail(LocalSignupDto dto) {
        return UserDetail.builder()
                .name(dto.getName())
                .phone(dto.getPhone())
                .gender(dto.getGender())
                .birth(dto.getBirth())
                .address(dto.getAddress())
                .build();
    }
    public static UserDetail toUserDetail(SocialSignupDto dto) {
        return UserDetail.builder()
                .name(dto.getName())
                .phone(dto.getPhone())
                .gender(dto.getGender())
                .birth(dto.getBirth())
                .address(dto.getAddress())
                .build();
    }



    public static User toSocialUser(UserDetail userDetail, SocialAccount socialAccount) {
        return User.builder()
                .userDetail(userDetail)
                .localAccount(null)
                .socialAccount(socialAccount)
                .loginType(LoginType.SOCIAL)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static SocialAccount toSocialAccount(String snsId, SocialProvider provider) {

        return SocialAccount.builder()

                .snsId(snsId)
                .provider(provider)
                .build();

    }


    public static UserDto touserDto(User user) {
        return UserDto.builder()
                .profileImg(user.getProfileImg())
                .loginType(user.getLoginType())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static UserDetailDto touserDetailDto(UserDetail userDetail) {
        return UserDetailDto.builder()
                .name(userDetail.getName())
                .phone(userDetail.getPhone())
                .gender(userDetail.getGender())
                .birth(userDetail.getBirth())
                .address(userDetail.getAddress())
                .build();
    }

    public static LocalResponseDto toResponseDto(CustomLocalUser customLocalUser) {
        User user = customLocalUser.getUser();
        UserDetail userDetail= customLocalUser.getUserDetail();

        UserDto userDto = touserDto(user);
        UserDetailDto userDetailDto = touserDetailDto(userDetail);
        return LocalResponseDto.builder()
                .user(userDto)
                .jwt(customLocalUser.getJwt())
                .userDetail(userDetailDto)
                .build();
    }

    public static LoginSuccessDto toSuccessDto(CustomUser customUser) {
        UserDetail userDetail = customUser.getUserDetail();

        return LoginSuccessDto.builder()
                .name(userDetail.getName())
                .phone(userDetail.getPhone())
                .gender(userDetail.getGender())
                .birth(userDetail.getBirth())
                .address(userDetail.getAddress())
                .build();
    }

    public static SocialResponseDto tosocialResponseDto(CustomUser customUser) {
        User user = customUser.getUser();
        UserDetail userDetail = customUser.getUserDetail();

        return SocialResponseDto.builder()
                .jwt(customUser.getJwt())
                .name(userDetail.getName())
                .phone(userDetail.getPhone())
                .gender(userDetail.getGender())
                .birth(userDetail.getBirth())
                .address(userDetail.getAddress())
                .build();
    }

}
