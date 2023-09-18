package com.rmsoft.library.controller;

import com.rmsoft.library.dto.LoginDto;
import com.rmsoft.library.dto.ResponseDto;
import com.rmsoft.library.dto.JoinDto;
import com.rmsoft.library.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseDto<?> login(@RequestBody LoginDto loginDto) {
        return authService.login(loginDto);
    }

    @PostMapping("/join")
    public ResponseDto<?> join(@RequestBody JoinDto joinDto) {
        return authService.join(joinDto);
    }


    @GetMapping("/deployTest")
    public String deploy() {
        return "deploySuccess!!!!!!";
    }
}
