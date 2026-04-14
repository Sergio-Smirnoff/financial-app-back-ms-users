package com.financialapp.users.service;

import com.financialapp.users.exception.DuplicateEmailException;
import com.financialapp.users.exception.ResourceNotFoundException;
import com.financialapp.users.kafka.event.UserRegisteredEvent;
import com.financialapp.users.kafka.producer.UserEventProducer;
import com.financialapp.users.model.dto.request.LoginRequest;
import com.financialapp.users.model.dto.request.RegisterRequest;
import com.financialapp.users.model.entity.User;
import com.financialapp.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEventProducer userEventProducer;

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        User saved = userRepository.save(user);

        try {
            userEventProducer.publishUserRegistered(UserRegisteredEvent.builder()
                    .userId(saved.getId())
                    .payload(UserRegisteredEvent.Payload.builder()
                            .email(saved.getEmail())
                            .firstName(saved.getFirstName())
                            .lastName(saved.getLastName())
                            .build())
                    .build());
        } catch (Exception ex) {
            log.error("Failed to publish user.registered event for userId={}: {}", saved.getId(), ex.getMessage());
        }

        return saved;
    }

    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        return user;
    }

    public User refreshToken(String refreshToken, JwtService jwtService) {
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
