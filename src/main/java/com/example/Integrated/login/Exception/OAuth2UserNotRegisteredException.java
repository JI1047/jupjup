package com.example.Integrated.login.Exception;

import com.example.Integrated.login.Entity.User.SocialProvider;
import lombok.Getter;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

import java.util.Map;

@Getter
public class OAuth2UserNotRegisteredException extends OAuth2AuthenticationException {

    private final String snsId;
    private final SocialProvider provider;
    private final Map<String, Object> attributes;


    public OAuth2UserNotRegisteredException(String snsId, SocialProvider provider, Map<String, Object> attributes) {

        super(new OAuth2Error("user_not_registered"), "User not registered");
        this.snsId = snsId;
        this.provider = provider;
        this.attributes = attributes;

    }


}
