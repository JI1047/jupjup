package com.example.Integrated.login.Service;

import com.example.Integrated.login.Entity.User.SocialAccount;
import com.example.Integrated.login.Entity.User.SocialProvider;
import com.example.Integrated.login.Entity.User.User;
import com.example.Integrated.login.Entity.User.UserDetail;
import com.example.Integrated.login.Exception.OAuth2UserNotRegisteredException;
import com.example.Integrated.login.Repository.User.SocialAccountRepository;
import com.example.Integrated.login.jwt.CustomOAuth2User;
import com.example.Integrated.login.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final SocialAccountRepository socialAccountRepository;
    private final JwtProvider jwtProvider;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        SocialProvider provider = SocialProvider.valueOf(userRequest.getClientRegistration().getRegistrationId());

        String snsId = extractSnsId(oAuth2User, provider); // provider별 ID 추출

        SocialAccount account = socialAccountRepository.findBySnsIdAndProvider(snsId, provider);

        if (account == null) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("user_not_registered"),
                    "User not registered",
                    new OAuth2UserNotRegisteredException(snsId, provider, oAuth2User.getAttributes())
            );
        }

        User user = account.getUser();
        String jwt = jwtProvider.generateToken(user);

        UserDetail userDetails = user.getUserDetail();
        return new CustomOAuth2User(user, jwt, userDetails);

    }

    private String extractSnsId(OAuth2User oAuth2User, SocialProvider provider) {
        if (provider.name().equals("google")) {
            return oAuth2User.getAttribute("sub");
        } else if (provider.name().equals("kakao")) {
            return oAuth2User.getAttribute("id").toString();
        } else if (provider.name().equals("naver")) {
            Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttribute("response");
            return response.get("id").toString();
        }
        return null;
    }
}
