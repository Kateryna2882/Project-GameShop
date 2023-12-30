package org.example.service;

import org.example.repository.dao.PasswordEncoder;

public class BCryptPasswordEncoder extends PasswordEncoder {

    private final org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder
            = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    String encodedPassword;

    @Override
    public String encode(String password) {
        encodedPassword = passwordEncoder.encode(password);
        return encodedPassword;
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
