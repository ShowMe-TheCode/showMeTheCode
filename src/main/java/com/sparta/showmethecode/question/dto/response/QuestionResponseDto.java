package com.sparta.showmethecode.question.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sparta.showmethecode.question.domain.QuestionStatus;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
public class QuestionResponseDto {

    private Long questionId;
    private String username;
    private String nickname;
    private String title;
    private String content;

    private String languageName;
    private String status;

    private LocalDateTime createdAt;

    private long commentCount;

    @QueryProjection
    public QuestionResponseDto(Long questionId, String username, String nickname, String title, String content, String languageName, QuestionStatus status, LocalDateTime createdAt, long commentCount) {
        this.questionId = questionId;
        this.username = username;
        this.nickname = nickname;
        this.title = title;
        this.content = content;
        this.languageName = languageName;
        this.status = status.getDescription();
        this.createdAt = createdAt;
        this.commentCount = commentCount;
    }
}
