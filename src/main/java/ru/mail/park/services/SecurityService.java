package ru.mail.park.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String encode(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean matches(String password, String hash) {
        return passwordEncoder.matches(password, hash);
    }
}
