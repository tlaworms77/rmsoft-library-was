package com.rmsoft.library.service;

import com.rmsoft.library.dto.JoinDto;
import com.rmsoft.library.dto.LoginDto;
import com.rmsoft.library.dto.LoginResponseDto;
import com.rmsoft.library.dto.ResponseDto;
import com.rmsoft.library.entity.User;
import com.rmsoft.library.repository.UserRepository;
import com.rmsoft.library.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    JwtProvider tokenProvider;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ResponseDto<?> login(@RequestBody LoginDto loginDto) {
        try {
            String userId = loginDto.getUserId();
            String userPassword = loginDto.getUserPassword();

            User userEntity = userRepository.findByUserId(userId);
            if(userEntity == null) return ResponseDto.setFailed("등록된 아이디가 존재하지 않습니다.");

            boolean isPasswordMatched = passwordEncoder.matches(userPassword, userEntity.getUserPassword());
            if(!isPasswordMatched) return ResponseDto.setFailed("비밀번호가 틀렸습니다.");

            userEntity.setUserPassword(""); // 비밀번호 token정보에서 제거

            String token = tokenProvider.create(userId);
            int exprTime = 3600000;

            // 이름,
            LoginResponseDto loginResponseDto = new LoginResponseDto(token, exprTime, userEntity);
            return ResponseDto.setSuccess("로그인 성공", loginResponseDto);
        } catch(Exception e) {
            return ResponseDto.setFailed("DataBase Error!");
        }
    }

    @Transactional
    public ResponseDto<?> join(JoinDto dto) {
        try {
            //아이디 중복 확인
            String userId = dto.getUserId();
            if(userRepository.existsById(userId)) {
                return ResponseDto.setFailed("중복된 아이디가 존재합니다.");
            }
        } catch(Exception e) {
            return ResponseDto.setFailed("DataBase Error!");
        }

        //1. 패스워드 체크 검증
        String userPassword = dto.getUserPassword();
        String userPasswordCheck = dto.getUserPasswordCheck();

        // 비밀번호가 서로 다르면 Fail
        if(!userPassword.equals(userPasswordCheck)) {
            return ResponseDto.setFailed("패스워드가 일치하지 않습니다");
        }

        // USER Entity 생성
        User user = new User(dto);

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userPassword);
        user.setUserPassword(encodedPassword);

        try {
            // UserRepository를 이용해서 DB에 Entity에 저장!
            userRepository.save(user);
        } catch(Exception e) {
            return ResponseDto.setFailed("DataBase Error!");
        }

        // 회원가입 성공
        return ResponseDto.setSuccess("Join Success!", null);
    }
}
