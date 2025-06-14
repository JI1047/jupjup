package com.example.Integrated.login.Service;

import com.example.Integrated.login.Entity.User.*;
import com.example.Integrated.login.Exception.OAuth2UserNotRegisteredException;
import com.example.Integrated.login.Repository.User.SocialAccountRepository;
import com.example.Integrated.login.jwt.CustomOAuth2User;
import com.example.Integrated.login.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.mysql.cj.conf.PropertyKey.logger;

@Slf4j
@Service
@RequiredArgsConstructor

public class OAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final SocialAccountRepository socialAccountRepository;
    private final JwtProvider jwtProvider;


    //소셜 로그인시 계정이 있으면 그대로 로그인 진행 없으면 회원가입 페이지로 이동
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        //userRequest안에 소셜로부터 받은 Access Token이 들어있다.
        //request 에서 user,제공 소셜 이름,snsID 추출
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        SocialProvider provider = SocialProvider.valueOf(userRequest.getClientRegistration().getRegistrationId());

        String snsId = extractSnsId(oAuth2User, provider); // provider별 ID 추출

        // DB에서 snsId,와 제공자로 계정 조회
        SocialAccount account = socialAccountRepository.findBySnsIdAndProvider(snsId, provider);

        //없으면 소셜로그인 전용 회원가입 페이지로 이동하기 위한 예외처리
        if (account == null) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("user_not_registered"),
                    "User not registered",
                    new OAuth2UserNotRegisteredException(snsId, provider, oAuth2User.getAttributes())
            );
        }

        //있다면 successHandler로 보내기 전에 return에 포함시킬 정보(user,jwt)생성
        User user = account.getUser();

        String jwt = jwtProvider.generateToken(user);



        UserDetail userDetails = user.getUserDetail();


        return new CustomOAuth2User(user, jwt, userDetails);

    }

    //소셜 제공자에서 snsid 도출
    private String extractSnsId(OAuth2User oAuth2User, SocialProvider provider) {
        if (provider.name().equals("kakao")) {
            return oAuth2User.getAttribute("id").toString();
        } else if (provider.name().equals("naver")) {
            Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttribute("response");
            return response.get("id").toString();
        }
        return null;
    }
}
