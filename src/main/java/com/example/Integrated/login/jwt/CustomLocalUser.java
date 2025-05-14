package com.example.Integrated.login.jwt;

import com.example.Integrated.login.Entity.User.User;
import com.example.Integrated.login.Entity.User.UserDetail;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomLocalUser extends CustomUser implements UserDetails {

    public CustomLocalUser(User user, String jwt, UserDetail userDetail) {
        super(user, jwt, userDetail);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return user.getLocalAccount().getPwd();
    }

    @Override
    public String getUsername() {
        return user.getLocalAccount().getEmail();
    }
}
