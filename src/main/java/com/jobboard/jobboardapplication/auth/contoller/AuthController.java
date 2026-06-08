package com.jobboard.jobboardapplication.auth.contoller;

import com.jobboard.jobboardapplication.auth.Service.AuthService;
import com.jobboard.jobboardapplication.auth.dto.LoginRequestDto;
import com.jobboard.jobboardapplication.auth.dto.LoginResponseDto;
import com.jobboard.jobboardapplication.auth.dto.RegisterUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDto>registerUser(@RequestBody RegisterUserDto registerUserDto){
        log.info("Register request for email : {}",registerUserDto.getEmail());
        return ResponseEntity.ok(authService.registerUser(registerUserDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto>login(@RequestBody LoginRequestDto loginRequestDto){
        log.info("Logged in for existing user with email : {}",loginRequestDto.getEmail());
        return ResponseEntity.ok(authService.loginUser(loginRequestDto));
    }
}
