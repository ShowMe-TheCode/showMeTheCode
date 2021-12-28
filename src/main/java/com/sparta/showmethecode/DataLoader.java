package com.sparta.showmethecode;

import com.sparta.showmethecode.language.domain.Language;
import com.sparta.showmethecode.user.domain.User;
import com.sparta.showmethecode.user.domain.UserRole;
import com.sparta.showmethecode.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        createNormalUser("test", "testUser", "1234");
        createReviewer("reviewer", "JavaGod", "1234", "Java");
    }



    private void createNormalUser(String username, String nickname, String password) {
        User user = User.builder()
                .username(username)
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .role(UserRole.ROLE_USER).build();
        userRepository.save(user);
    }

    private void createReviewer(String username, String nickname, String password, String language) {
        User user =  User.builder()
                .username(username)
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .languages(new ArrayList<>())
                .role(UserRole.ROLE_REVIEWER).build();
        user.addLanguage(new Language(language.toUpperCase()));
        userRepository.save(user);
    }
}
