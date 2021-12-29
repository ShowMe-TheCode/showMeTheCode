package com.sparta.showmethecode.question.domain;

import com.sparta.showmethecode.answer.domain.Answer;
import com.sparta.showmethecode.notification.domain.Notification;
import com.sparta.showmethecode.comment.domain.Comment;
import com.sparta.showmethecode.language.domain.Timestamped;
import com.sparta.showmethecode.question.dto.request.UpdateQuestionDto;
import com.sparta.showmethecode.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * 코드리뷰 요청서
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Question extends Timestamped {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private QuestionStatus status;

    @Column(nullable = false)
    private String languageName;

    // 한 명의 사용자는 여러 개 코드리뷰 요청서를 작성할 수 있다.
    @JoinColumn(name = "request_user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User requestUser;

    // 한 명의 리뷰어는 여러 개 코드리뷰 요청서에 리뷰를 할 수 있다.
    @JoinColumn(name = "answer_user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User answerUser;

    @JoinColumn(name = "answer_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Answer answer;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.REMOVE)
    private List<Notification> notifications = new ArrayList<>();

    public void addAnswer(Answer answer) {
        this.answer = answer;
        answer.setQuestion(this);
    }

    public void addComment(Comment comment) {
        this.getComments().add(comment);
        comment.setQuestion(this);
    }

    public Question(User requestUser, String title, String content, QuestionStatus status, String languageName) {
        this.requestUser = requestUser;
        this.title = title;
        this.content = content;
        this.status = status;
        this.languageName = languageName.toUpperCase();
    }

    public Question(User requestUser, User answerUser, String title, String content, QuestionStatus status, String languageName) {
        this.requestUser = requestUser;
        this.answerUser = answerUser;
        this.title = title;
        this.content = content;
        this.status = status;
        this.languageName = languageName.toUpperCase();
    }

    public void update(UpdateQuestionDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
    }

    public void setStatus(QuestionStatus status) {
        this.status = status;
    }

    public boolean hasComments() {
        if (!Objects.isNull(this.comments))
            return this.getComments().size() > 0 ? true : false;
        else
            return false;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
        answer.setQuestion(this);
    }

    public void updateReviewer(User answerUser) {
        this.answerUser = answerUser;
    }
}
