package com.example.Integrated.login.jwt;

import com.example.Integrated.login.Entity.User.User;
import com.example.Integrated.login.Entity.User.UserDetail;
import lombok.Getter;

@Getter
public class CustomUser {

    protected final User user;

    protected final String jwt;

    protected final UserDetail userDetail;

    public CustomUser(User user, String jwt, UserDetail userDetail) {
        this.user = user;
        this.jwt = jwt;
        this.userDetail = userDetail;

    }


 }
