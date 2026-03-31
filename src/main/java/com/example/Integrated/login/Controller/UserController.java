package com.example.Integrated.login.Controller;

import com.example.Integrated.login.Dto.Login.LocalRequestDto;
import com.example.Integrated.login.Dto.Login.LocalResponseDto;
import com.example.Integrated.login.Dto.Login.LoginSuccessDto;
import com.example.Integrated.login.Dto.Signup.LocalSignupDto;
import com.example.Integrated.login.Dto.Signup.SocialSignupDto;
import com.example.Integrated.login.Dto.Update.UpdateRequest;
import com.example.Integrated.login.Entity.User.SocialProvider;
import com.example.Integrated.login.Entity.User.User;
import com.example.Integrated.login.Entity.User.UserDetail;
import com.example.Integrated.login.Mapper.UserMapper;
import com.example.Integrated.login.Service.UserService;
import com.example.Integrated.login.jwt.CustomLocalUser;
import com.example.Integrated.login.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/me")
    public ResponseEntity<LoginSuccessDto> me(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();
        User user = userDetails.getUser();
        UserDetail detail = user.getUserDetail();
        CustomLocalUser custom = new CustomLocalUser(user, null, detail);
        return ResponseEntity.ok(UserMapper.toSuccessDto(custom));
    }

    @GetMapping("/check-email")
    public ResponseEntity<String> checkEmail(@RequestParam("email") String email) {

        return userService.checkEmail(email);
    }
    @PutMapping("/edit")
    public ResponseEntity<String> Edit(@AuthenticationPrincipal CustomUserDetails principal,
                                            @RequestBody UpdateRequest req) {

        userService.updateLocalUser(principal.getUser(), req);
        return ResponseEntity.noContent().build();
    }



}
