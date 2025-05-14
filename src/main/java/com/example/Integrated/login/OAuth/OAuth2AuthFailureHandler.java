package com.example.Integrated.login.OAuth;


import com.example.Integrated.login.Exception.OAuth2UserNotRegisteredException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class OAuth2AuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        if (exception.getCause() instanceof OAuth2UserNotRegisteredException ex) {
            response.sendRedirect("http://localhost:3000/social-signup?snsId=" + ex.getSnsId() + "&provider=" + ex.getProvider());
        } else {
            response.sendRedirect("/login?error");
        }
    }
}
