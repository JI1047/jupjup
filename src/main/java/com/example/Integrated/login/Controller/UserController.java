package com.example.Integrated.login.Controller;

import com.example.Integrated.login.Dto.Login.LocalRequestDto;
import com.example.Integrated.login.Dto.Login.LocalResponseDto;
import com.example.Integrated.login.Dto.Login.LoginSuccessDto;
import com.example.Integrated.login.Dto.Login.SocialResponseDto;
import com.example.Integrated.login.Dto.Signup.LocalSignupDto;
import com.example.Integrated.login.Dto.Signup.SocialSignupDto;
import com.example.Integrated.login.Entity.User.SocialProvider;
import com.example.Integrated.login.Entity.User.User;
import com.example.Integrated.login.Entity.User.UserDetail;
import com.example.Integrated.login.Mapper.UserMapper;
import com.example.Integrated.login.Service.UserService;
import com.example.Integrated.login.jwt.CustomLocalUser;
import com.example.Integrated.login.jwt.CustomOAuth2User;
import com.example.Integrated.login.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Long> signup(@RequestBody LocalSignupDto request) {
        Long userId = userService.registerLocalUser(request);
        return ResponseEntity.ok(userId);
    }

    @PostMapping("/social-signup")
    public ResponseEntity<Long> socialSignup(
            @RequestBody SocialSignupDto dto,
            @RequestParam("snsId") String snsId,
            @RequestParam("provider") SocialProvider provider
    ) {
        Long userId = userService.registerSocialUser(dto, snsId, provider);
        return ResponseEntity.ok(userId);
    }

    @PostMapping("/local-login")
    public LocalResponseDto localLogin(@RequestBody LocalRequestDto request) {

        LocalResponseDto response = userService.getLocalUser(request);


        return response;

    }

    @GetMapping("/token")
    public ResponseEntity<SocialResponseDto> issueToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof CustomOAuth2User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();


        System.out.println("🔥 /token 진입 - JWT: " + oAuth2User.getJwt());
        SocialResponseDto response = UserMapper.tosocialResponseDto(oAuth2User);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/login-success")
    public ResponseEntity<LoginSuccessDto> loginSuccess(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        UserDetail detail = user.getUserDetail();

        // 여기서 jwt를 null로 넣음
        CustomLocalUser custom = new CustomLocalUser(user, null, detail);
        LoginSuccessDto dto = UserMapper.toSuccessDto(custom);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/check-email")
    public ResponseEntity<String> checkEmail(@RequestParam("email") String email) {

        return userService.checkEmail(email);
    }
}
