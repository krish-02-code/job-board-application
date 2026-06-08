package com.jobboard.jobboardapplication.auth.dto;

import com.jobboard.jobboardapplication.auth.model.Role;
import lombok.Data;

@Data
public class RegisterUserDto {
    private String name;
    private String password;
    private String email;
    private Role role;
}
