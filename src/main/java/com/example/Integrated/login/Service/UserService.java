package com.example.Integrated.login.Service;


import com.example.Integrated.login.Dto.Login.LocalRequestDto;
import com.example.Integrated.login.Dto.Login.LocalResponseDto;
import com.example.Integrated.login.Dto.Signup.LocalSignupDto;
import com.example.Integrated.login.Dto.Signup.SocialSignupDto;
import com.example.Integrated.login.Dto.Signup.UpdatedDto;
import com.example.Integrated.login.Entity.User.*;
import com.example.Integrated.login.Mapper.UserMapper;
import com.example.Integrated.login.Repository.User.LocalAccountRepository;
import com.example.Integrated.login.Repository.User.SocialAccountRepository;
import com.example.Integrated.login.Repository.User.UserDetailRepository;
import com.example.Integrated.login.Repository.User.UserRepository;
import com.example.Integrated.login.jwt.CustomLocalUser;
import com.example.Integrated.login.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
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
        // User 생성
        User user = UserMapper.toLocalUser(dto);

        userRepository.save(user);


        // LocalAccount 생성
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        LocalAccount localAccount = UserMapper.toLocalAccount(dto, encodedPassword, user);

        localAccountRepository.save(localAccount);

        // UserDetail 생성
        UserDetail userDetail = UserMapper.toSocialUserDetail(dto, user);

        userDetailRepository.save(userDetail);

        return user.getId();
    }


    public Long registerSocialUser(SocialSignupDto dto, String snsId, SocialProvider provider) {

        User user = UserMapper.toSocialUser(dto);
        userRepository.save(user);



        UpdatedDto updatedDto = UpdatedDto.builder()
                .snsId(snsId)
                .provider(provider)
                .build();

        SocialAccount socialAccount = UserMapper.toSocialAccount(user,updatedDto);
        socialAccountRepository.save(socialAccount);

        UserDetail userDetail = UserMapper.toSocialUserDetail(dto, user);
        userDetailRepository.save(userDetail);

        return user.getId();


    }

        public LocalResponseDto getLocalUser(LocalRequestDto dto) {
            LocalAccount localAccount = localAccountRepository.findByEmail(dto.getEmail());

            if (localAccount == null) {
                throw new UsernameNotFoundException("존재하지 않는 사용자입니다.");
            }

            // 2. 비밀번호 검증
            if (!passwordEncoder.matches(dto.getPassword(), localAccount.getPwd())) {
                throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
            }
            User user = localAccount.getUser();

            String jwt = jwtProvider.generateToken(user);
            UserDetail userDetail = user.getUserDetail();

            CustomLocalUser customLocalUser = new CustomLocalUser(user, jwt, userDetail);

            LocalResponseDto responseDto = UserMapper.toResponseDto(customLocalUser);

            return responseDto;



        }

}
