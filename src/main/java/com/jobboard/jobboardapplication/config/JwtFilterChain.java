package com.jobboard.jobboardapplication.config;

import com.jobboard.jobboardapplication.ExceptionHandler.UserNotFoundException;
import com.jobboard.jobboardapplication.auth.model.Role;
import com.jobboard.jobboardapplication.auth.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilterChain extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String token ;
        final String email;

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
           filterChain.doFilter(request,response);
           return;
        }
        token = authHeader.substring(7);
        email = jwtService.extractEmail(token);

        // check if we have a email in db and no authentication exists yet
        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
            var userDetails = userRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("User not found !"));

            //check validation of the token
            if(jwtService.isValidToken(token,userDetails)){
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(email,null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(request,response);
    }
}
