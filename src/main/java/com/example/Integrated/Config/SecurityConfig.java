package com.example.Integrated.Config;

import com.example.Integrated.login.Repository.User.UserRepository;
import com.example.Integrated.login.Service.OAuth2Service;

import com.example.Integrated.login.OAuth.OAuth2AuthFailureHandler;
import com.example.Integrated.login.jwt.CustomOAuth2User;
import com.example.Integrated.login.jwt.JwtAuthenticationFilter;
import com.example.Integrated.login.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseCookie;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2Service oAuth2Service;

    private final OAuth2AuthFailureHandler oAuth2AuthFailureHandler;

    private final JwtProvider jwtProvider;

    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                //인증 없이도 접근 허용가능한 api 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/local-login", "/", "/api/auth/**", "/login/**", "/api/test-import", "/map/**").permitAll()
                        //나머지는 로그인 필요
                        .anyRequest().authenticated()
                )
                //인증되지 않은 사용자가 접근했을 때 JSON으로 응답을 줌
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) -> {
                            //redirect 막고 401로 에러페이지
                            response.setStatus(401); // ✅ 핵심: Redirect 막기
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Unauthorized\"}");
                        })
                )
                //OAuth2 로그인 설정
                .oauth2Login(oauth -> oauth
                        //로그인 후 사용자 정보를 가져올 때 사용할 서비스 지정
                        .userInfoEndpoint(userInfo -> userInfo
                                //로그인 시 내가 만든 oAuth2Service가 실행됨
                                .userService(oAuth2Service)
                        )
                        //로그인 실패 시 처리하는 핸들러
                        .failureHandler(oAuth2AuthFailureHandler)
                        //로그인 성공 시 실행되는 로직
                        .successHandler((request, response, authentication) -> {
                            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
                            System.out.println("✅ 로그인 성공 - JWT: " + oAuth2User.getJwt());
                            String jwt = oAuth2User.getJwt();

                            //로그인 성공시 jwt 추출해서 redirectUrl에 추가
                            String redirectUrl = "http://localhost:3000/oauth/loginInfo?token=" + jwt;
                            //  프론트에서 /api/auth/token 요청을 통해 JWT를 수동으로 받을 수 있도록 리디렉션만 수행
                            response.sendRedirect(redirectUrl);
                        })
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider, userRepository), UsernamePasswordAuthenticationFilter.class
                )
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000", "http://13.209.202.27"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true); // 쿠키 포함할지 여부

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
