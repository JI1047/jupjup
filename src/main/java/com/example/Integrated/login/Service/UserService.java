package com.example.Integrated.login.Service;


import com.example.Integrated.login.Dto.Login.LocalRequestDto;
import com.example.Integrated.login.Dto.Login.LocalResponseDto;
import com.example.Integrated.login.Dto.Signup.LocalSignupDto;
import com.example.Integrated.login.Dto.Signup.SocialSignupDto;
import com.example.Integrated.login.Entity.User.*;
import com.example.Integrated.login.Mapper.UserMapper;
import com.example.Integrated.login.Repository.User.LocalAccountRepository;
import com.example.Integrated.login.Repository.User.SocialAccountRepository;
import com.example.Integrated.login.Repository.User.UserDetailRepository;
import com.example.Integrated.login.Repository.User.UserRepository;
import com.example.Integrated.login.jwt.CustomLocalUser;
import com.example.Integrated.login.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final LocalAccountRepository localAccountRepository;
    private final UserDetailRepository userDetailRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public Long registerLocalUser(LocalSignupDto dto) {

        // LocalAccount 생성
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        LocalAccount localAccount = UserMapper.toLocalAccount(dto, encodedPassword);

        localAccountRepository.save(localAccount);

        // UserDetail 생성
        UserDetail userDetail = UserMapper.toUserDetail(dto);

        userDetailRepository.save(userDetail);

        User user = UserMapper.toLocalUser(userDetail,localAccount);

        userRepository.save(user);

        return user.getId();
    }


    public Long registerSocialUser(SocialSignupDto dto, String snsId, SocialProvider provider) {


        SocialAccount socialAccount = UserMapper.toSocialAccount(snsId,provider);
        socialAccountRepository.save(socialAccount);

        UserDetail userDetail = UserMapper.toUserDetail(dto);
        userDetailRepository.save(userDetail);

        User user = UserMapper.toSocialUser(userDetail,socialAccount);
        userRepository.save(user);

        return user.getId();


    }

    public LocalResponseDto getLocalUser(LocalRequestDto dto) {
        LocalAccount localAccount = localAccountRepository.findByEmailWithLocalAccount(dto.getEmail());

        if (localAccount == null) {
            throw new UsernameNotFoundException("존재하지 않는 사용자입니다.");
        }

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(dto.getPassword(), localAccount.getPwd())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }


        User user = localAccountRepository.findUserByLocalAccountEmail(dto.getEmail());

        String jwt = jwtProvider.generateToken(user);
        UserDetail userDetail = user.getUserDetail();

        CustomLocalUser customLocalUser = new CustomLocalUser(user, jwt, userDetail);

        LocalResponseDto responseDto = UserMapper.toResponseDto(customLocalUser);

        return responseDto;



    }


    public ResponseEntity<String> checkEmail(String email) {
        if (localAccountRepository.existsByEmail(email)) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) // 409 Conflict
                    .body("아이디가 중복됨");
        }
        return ResponseEntity.ok("사용 가능한 이메일입니다");
    }


}
