package com.server.domain.user.service;


import com.server.domain.oauth.dto.GithubDto;
import com.server.domain.user.entity.User;
import com.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User loginOrRegister(GithubDto githubDto, String githubToken) {
        return userRepository.findByUsername(githubDto.getUsername())
                .orElseGet(() -> registerNewUser(githubDto, githubToken));
    }

    private User registerNewUser(GithubDto githubDto, String githubToken) {
        User newUser = User.builder()
                .email(githubDto.getEmail())
                .username(githubDto.getUsername())
                .thumbnail(githubDto.getThumbnail())
                .oauth("github")
                .githubToken(githubToken)
                .build();


        return userRepository.save(newUser);
    }

    @Transactional
    public void saveRefreshToken(String userName, String refreshToken) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);
    }

    public User findByUserName(String userName) {
        return userRepository.findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}