package com.sparta.showmethecode.answer.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Builder
@Data
public class AnswerResponseDto {

    private Long answerId;
    private Long questionId;

    private String username;
    private String nickname;

    private String content;

    private double point;

    private LocalDateTime createdAt;

    @QueryProjection
    public AnswerResponseDto(
            Long answerId, Long questionId, String username, String nickname, String content, double point, LocalDateTime createdAt
    ) {
        this.answerId = answerId;
        this.questionId = questionId;
        this.username = username;
        this.nickname = nickname;
        this.content = content;
        this.point = point;
        this.createdAt = createdAt;
    }
}
