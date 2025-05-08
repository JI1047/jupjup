package com.example.Integrated.login.jwt;

import com.example.Integrated.login.Entity.User.User;
import com.example.Integrated.login.Entity.User.UserDetail;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomOAuth2User extends CustomUser implements OAuth2User{



    public CustomOAuth2User(User user, String jwt, UserDetail userDetail) {
        super(user,jwt,userDetail);

    }


    @Override
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getName() {
        return user.getId().toString(); // Spring Security 내부에서 사용하는 식별자
    }
}
