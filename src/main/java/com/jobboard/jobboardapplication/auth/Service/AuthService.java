package com.jobboard.jobboardapplication.auth.Service;

import com.jobboard.jobboardapplication.ExceptionHandler.UserNotFoundException;
import com.jobboard.jobboardapplication.auth.dto.LoginRequestDto;
import com.jobboard.jobboardapplication.auth.dto.LoginResponseDto;
import com.jobboard.jobboardapplication.auth.dto.RegisterUserDto;
import com.jobboard.jobboardapplication.auth.model.User;
import com.jobboard.jobboardapplication.auth.repository.UserRepository;
import com.jobboard.jobboardapplication.config.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.security.auth.Login;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Serial;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public LoginResponseDto registerUser(RegisterUserDto registerUserDto) {

        if (userRepository.existsByEmail(registerUserDto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User
                .builder()
                .role(registerUserDto.getRole())
                .name(registerUserDto.getName())
                .password(passwordEncoder.encode(registerUserDto.getPassword()))
                .email(registerUserDto.getEmail()).build();
        log.info("User with user email : {} added to Database ", registerUserDto.getEmail());
        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return new LoginResponseDto(token, user.getEmail(), user.getRole().name());
    }

    public LoginResponseDto loginUser(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found !"));

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));

        log.info("User log in successfully with id : {}", user.getId());
        String token = jwtService.generateToken(user);
        return new LoginResponseDto(token, user.getEmail(), user.getRole().name());
    }
}
