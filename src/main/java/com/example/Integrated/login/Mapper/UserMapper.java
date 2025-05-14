package com.example.Integrated.login.Mapper;

import com.example.Integrated.login.Dto.Login.LocalResponseDto;
import com.example.Integrated.login.Dto.Login.LoginSuccessDto;
import com.example.Integrated.login.Dto.Login.SocialResponseDto;
import com.example.Integrated.login.Dto.Signup.LocalSignupDto;
import com.example.Integrated.login.Dto.Signup.SocialSignupDto;
import com.example.Integrated.login.Dto.Signup.UpdatedDto;
import com.example.Integrated.login.Dto.User.UserDetailDto;
import com.example.Integrated.login.Dto.User.UserDto;
import com.example.Integrated.login.Entity.User.*;
import com.example.Integrated.login.jwt.CustomLocalUser;
import com.example.Integrated.login.jwt.CustomUser;
import com.example.Integrated.login.jwt.JwtProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
public class UserMapper {

    private final JwtProvider jwtProvider;





    public static User toLocalUser(LocalSignupDto dto) {
        return User.builder()
                .nickname(dto.getNickname())
                .loginType(LoginType.LOCAL)
                .createdAt(LocalDateTime.now())
                .build();
    }


    public static LocalAccount toLocalAccount(LocalSignupDto dto, String encodedPassword, User user) {

        return LocalAccount.builder()
                .user(user)
                .email(dto.getEmail())
                .pwd(encodedPassword)
                .build();
    }

    public static UserDetail toSocialUserDetail(LocalSignupDto dto, User user) {
        return UserDetail.builder()
                .user(user)

                .name(dto.getName())
                .phone(dto.getPhone())
                .gender(dto.getGender())
                .birth(dto.getBirth())
                .address(dto.getAddress())
                .build();
    }



    public static User toSocialUser(SocialSignupDto dto) {
        return User.builder()
                .nickname(dto.getNickname())
                .loginType(LoginType.SOCIAL)
                .createdAt(LocalDateTime.now())
                .build();
    }
    public static SocialAccount toSocialAccount(User user, UpdatedDto updatedDto) {

        return SocialAccount.builder()
                .user(user)
                .snsId(updatedDto.getSnsId())
                .provider(updatedDto.getProvider())
                .build();

    }
    public static UserDetail toSocialUserDetail(SocialSignupDto dto,User user) {
        return UserDetail.builder()
                .user(user)
                .name(dto.getName())
                .phone(dto.getPhone())
                .gender(dto.getGender())
                .birth(dto.getBirth())
                .address(dto.getAddress())
                .build();
    }

    public static UserDto touserDto(User user) {
        return UserDto.builder()
                .nickname(user.getNickname())
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
        User user = customUser.getUser();
        UserDetail userDetail = customUser.getUserDetail();

        return LoginSuccessDto.builder()
                .nickname(user.getNickname())
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
                .nickname(user.getNickname())
                .name(userDetail.getName())
                .phone(userDetail.getPhone())
                .gender(userDetail.getGender())
                .birth(userDetail.getBirth())
                .address(userDetail.getAddress())
                .build();
    }

}
