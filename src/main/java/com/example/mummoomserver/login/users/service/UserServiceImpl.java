package com.example.mummoomserver.login.users.service;

import com.example.mummoomserver.config.resTemplate.ResponeException;
import com.example.mummoomserver.login.service.UserDetailsImpl;
import com.example.mummoomserver.login.users.*;
import com.example.mummoomserver.login.users.dto.UserDto;
import com.example.mummoomserver.login.users.requestResponse.SignUpRequest;
import com.example.mummoomserver.login.users.requestResponse.UpdateProfileRequest;
import com.example.mummoomserver.login.validation.SimpleFieldError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.mummoomserver.config.resTemplate.ResponseTemplateStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    /**
     *
     * 현석
     * 시큐리티객체로부터 유저정보 뽑아옴
     */

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void saveUser(SignUpRequest signUpRequest){
        checkDuplicateEmail(signUpRequest.getEmail());
        User user = User.builder()
                .nickName(signUpRequest.getNickName())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .type(UserType.DEFAULT)
                .role(Role.GUEST)
                .build();

        userRepository.save(user);
    }

    @Override
    public UserDto getUserProfile(String email) throws ResponeException {
        //이메일을 입력받으면 정보를 내어주는 로직을 짜야함
        User user = userRepository.findByEmail(email).get();
        try {
            return new UserDto(user.getEmail(), user.getNickName(), user.getImgUrl(), user.getPassword());
        } catch (Exception e) {
            throw new ResponeException(DATABASE_ERROR);
        }
    }
    public void updateProfile(String email, UpdateProfileRequest updateProfileRequest) throws ResponeException {

        User user = userRepository.findByEmail(email).get();
        // 이미지가 변경되었는지 체크
        if (!user.getImgUrl().equals(updateProfileRequest.getImgUrl()))
            user.updateImgUrl(updateProfileRequest.getImgUrl());

        //이름이 변경되었는지 체크
        if (!user.getNickName().equals(updateProfileRequest.getNickName()))
            checkDuplicateNickname(updateProfileRequest.getNickName());
            user.updateName(updateProfileRequest.getNickName());
        userRepository.save(user);

    }

    private void checkDuplicateEmail(String email) {
        if(userRepository.existsByEmail(email))
            throw new DuplicateUserException("사용중인 이메일 입니다.", new SimpleFieldError("email", "사용중인 이메일 입니다."));
    }

    private void checkDuplicateNickname(String nickName) {
        if(userRepository.existsByEmail(nickName))
            throw new DuplicateUserException("사용중인 닉네임 입니다.", new SimpleFieldError("nickname", "사용중인 닉네임 입니다."));
    }

//    private User checkRegisteredUser(String nickName) {
//        Optional<User> optUser = userRepository.findByNickName(nickName);
//        Assert.state(optUser.isPresent(), "가입되지 않은 회원입니다.");
//        return optUser.get();
//    }


    // 현석- 추가됨
    @Override
    public String getAuthUserNickname() {

        UserDetailsImpl userDetails= (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userDetails instanceof UserDetails){ //인증된 유저여야함
            return userDetails.getNickName();
        }else{
            log.info("인증되지 않은 유저의 정보이므로 유저 닉네임을 불러올 수 없습니다.");
            return userDetails.toString();
        }

    }

    @Override

    public String getAuthUserEmail() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails instanceof UserDetails) { //인증된 유저여야함
            return userDetails.getEmail();
        } else {
            log.info("인증되지 않은 유저의 정보이므로 유저 이메일을 불러올 수 없습니다.");
            return userDetails.toString();
        }
    }
}
