package com.sparta.showmethecode.question.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sparta.showmethecode.comment.dto.response.CommentResponseDto;
import com.sparta.showmethecode.answer.dto.response.AnswerResponseDto;
import com.sparta.showmethecode.question.domain.QuestionStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionDetailsResponseDto {

    private Long questionId;
    private Long questionUserId;
    private Long answerUserId;
    private String username;
    private String nickname;
    private String title;
    private String content;
    private String languageName;
    private String status;

    private LocalDateTime createdAt;

    private AnswerResponseDto answer;

    private List<CommentResponseDto> comments;


    @QueryProjection
    public QuestionDetailsResponseDto(
            Long questionId, Long questionUserId, Long answerUserId,
            String username, String nickname,
            String title, String content, QuestionStatus status, String languageName, LocalDateTime createdAt,
            List<CommentResponseDto> comments,
            AnswerResponseDto answer
    ) {
        this.questionId = questionId;
        this.answerUserId = answerUserId;
        this.questionUserId = questionUserId;
        this.username = username;
        this.nickname = nickname;
        this.title = title;
        this.content = content;
        this.status = status.getDescription();
        this.languageName = languageName;
        this.createdAt = createdAt;
        this.comments = comments;
        this.answer = answer;
    }

    public QuestionDetailsResponseDto(
            Long questionId, Long questionUserId, Long answerUserId,
            String username, String nickname,
            String title, String content, QuestionStatus status, String languageName, LocalDateTime createdAt,
            List<CommentResponseDto> comments
    ) {
        this.questionId = questionId;
        this.answerUserId = answerUserId;
        this.questionUserId = questionUserId;
        this.username = username;
        this.nickname = nickname;
        this.title = title;
        this.content = content;
        this.languageName = languageName;
        this.status = status.getDescription();
        this.createdAt = createdAt;
        this.comments = comments;
    }
}
