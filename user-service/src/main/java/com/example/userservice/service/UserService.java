package com.example.userservice.service;

import com.example.userservice.dto.response.UserResponse;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse getOrCreateByJwt(Jwt token) {
        String sub = token.getClaim("sub");
        User user = userRepository.findById(UUID.fromString(sub))
                .orElseGet(() -> createUser(token));

        return userMapper.toDto(user);
    }

    private User createUser(Jwt jwt) {
        String sub = jwt.getClaim("sub");
        User user = User.builder()
                .id(UUID.fromString(sub))
                .email(jwt.getClaim("email"))
                .username(jwt.getClaim("preferred_username"))
                .firstName(jwt.getClaim("given_name"))
                .lastName(jwt.getClaim("family_name"))
                .build();

        return userRepository.save(user);
    }
}
