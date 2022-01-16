package com.sparta.showmethecode.ranking.dto.response;

import com.sparta.showmethecode.ranking.domain.Ranking;
import com.sparta.showmethecode.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
public class RankingUserResponseDto {

    private Long id;
    private String username;
    private String nickname;
    private List<String> languages;

    private int answerCount;
    private double point;

    public RankingUserResponseDto(Ranking ranking) {
        User user = ranking.getUser();

        this.id = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.languages = user.getLanguages().stream().map(l -> new String(l.getName())).collect(Collectors.toList());
        this.answerCount = ranking.getAnswerCount();
        this.point = ranking.getAverage();
    }
}
