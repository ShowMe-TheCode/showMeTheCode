package com.sparta.showmethecode.comment.domain;

import com.sparta.showmethecode.language.domain.Timestamped;
import com.sparta.showmethecode.comment.dto.request.UpdateCommentDto;
import com.sparta.showmethecode.answer.domain.Answer;
import com.sparta.showmethecode.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 리뷰 답변서에 달리는 댓글
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class ReviewAnswerComment extends Timestamped {

    @Id @GeneratedValue
    private Long id;

    private String content;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "review_answer_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Answer answer;

    public ReviewAnswerComment(String content, User user, Answer answer) {
        this.content = content;
        this.user = user;
        this.answer = answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public void update(UpdateCommentDto dto) {
        this.content = dto.getContent();
    }
}
