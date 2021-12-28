package com.sparta.showmethecode.answer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.showmethecode.comment.domain.ReviewAnswerComment;
import com.sparta.showmethecode.language.domain.Timestamped;
import com.sparta.showmethecode.answer.dto.request.UpdateAnswerDto;
import com.sparta.showmethecode.question.domain.Question;
import com.sparta.showmethecode.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * 코드리뷰 답변서
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Answer extends Timestamped {

    @Id @GeneratedValue
    private Long id;

    @Lob
    @Column(nullable = false)
    private String content;

    private double point;

    @JoinColumn(name = "answer_user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User answerUser;

    @JsonIgnore
    @JoinColumn(name = "review_request_id")
    @OneToOne(mappedBy = "reviewAnswer", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Question question;

    public void setQuestion(Question question) {
        this.question = question;
    }

    @OneToMany(mappedBy = "answer", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReviewAnswerComment> comments;

    public void evaluate(double point) {
        this.point = point;
    }

    public void update(UpdateAnswerDto dto) {
        this.content = dto.getContent();
    }

    public void addComment(ReviewAnswerComment comment) {
        this.getComments().add(comment);
        comment.setAnswer(this);
    }

    public Answer(String content, double point, User answerUser, Question question) {
        this.content = content;
        this.point = point;
        this.answerUser = answerUser;
        this.question = question;
    }
}
