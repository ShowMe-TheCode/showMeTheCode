package com.sparta.showmethecode.helper;

import com.sparta.showmethecode.language.domain.Language;
import com.sparta.showmethecode.ranking.domain.Ranking;
import com.sparta.showmethecode.user.domain.User;
import com.sparta.showmethecode.user.domain.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.stream.Collectors;

public class UserHelper {

    public static User createUser(String username, String password, String nickname, UserRole role, String... languageName) {
        Ranking ranking = new Ranking(0, 0.0, 0.0, 0);
        User user = new User(username, password, nickname, role, Arrays.stream(languageName).map(l -> new Language(l)).collect(Collectors.toList()));
        user.setRanking(ranking);

        return user;
    }
}
