package com.jobboard.jobboardapplication.config;

import com.jobboard.jobboardapplication.ExceptionHandler.UserNotFoundException;
import com.jobboard.jobboardapplication.auth.model.User;
import com.jobboard.jobboardapplication.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String Email) throws UsernameNotFoundException {
        return userRepository.findByEmail(Email).orElseThrow(()-> new UserNotFoundException("User with email " +Email+" not found"));
    }
}
