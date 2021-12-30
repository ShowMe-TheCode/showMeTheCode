package com.sparta.showmethecode.user.domain;

import com.sparta.showmethecode.language.domain.Language;
import com.sparta.showmethecode.comment.domain.Comment;
import com.sparta.showmethecode.language.domain.Timestamped;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User extends Timestamped {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private UserRole role; // ROLE_USER: 일반사용자, ROLE_REVIEWER: 리뷰어

    private int answerCount = 0; // 몇 개의 코드리뷰를 완료했는지
    private int evalCount = 0; // 몇 명의 평가를 받았는지
    private double evalTotal = 0; // 평가점수 총점

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Language> languages = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    // 연관관계 편의 메서드
    public void addLanguage(Language language) {
        this.getLanguages().add(language);
        language.setUser(this);
    }

    public User(String username, String password, String nickname,UserRole role, int answerCount, int evalCount,double evalTotal, List<Language> languages) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.answerCount = answerCount;
        this.evalCount = evalCount;
        this.evalTotal = evalTotal;
        this.languages = languages;

    }

    public User(String username, String password, String nickname, UserRole role, int answerCount, int evalCount, double evalTotal) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.answerCount = answerCount;
        this.evalCount = evalCount;
        this.evalTotal = evalTotal;
    }

    public void evaluate(double point) {
        this.evalCount++;
        this.evalTotal += point;
    }

    public User update(String name){
        this.username = name;

        return this;
    }
    public String getRoleKey(){
        return this.role.toString();
    }


    public void increaseAnswerCount() {
        this.answerCount++;
    }
}