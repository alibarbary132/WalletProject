package com.example.walletproject.auth.service;

import com.example.walletproject.auth.InvalidCredentialsException;
import com.example.walletproject.auth.dto.LoginRequest;
import com.example.walletproject.auth.dto.RegisterRequest;
import com.example.walletproject.auth.entity.User;
import com.example.walletproject.auth.repository.UserRepository;
import com.example.walletproject.exception.UsernameAlreadyExistsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    public String login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(InvalidCredentialsException::new);

        // BCrypt comparison: hashes the raw input password using the same salt/algorithm
        // stored in the hash, and compares — never decrypt the stored hash to compare.
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return jwtService.generateToken(user);
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException(request.username());
        }

        // Never store the raw password — only its BCrypt hash.
        String hashedPassword = passwordEncoder.encode(request.password());

        User user = new User(request.username(), request.email(), hashedPassword, User.Role.USER);
        return userRepository.save(user);
    }
}