package com.sparta.showmethecode.user.domain;

import com.sparta.showmethecode.language.domain.Language;
import com.sparta.showmethecode.comment.domain.Comment;
import com.sparta.showmethecode.language.domain.Timestamped;
import com.sparta.showmethecode.ranking.domain.Ranking;
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Language> languages = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ranking_id")
    private Ranking ranking;

    // 연관관계 편의 메서드
    public void addLanguage(Language language) {
        this.getLanguages().add(language);
        language.setUser(this);
    }

    // 연관관계 편의 메서드
    public void setRanking(Ranking ranking) {
        ranking.setUser(this);
        this.ranking = ranking;
    }

    public User(String username, String password, String nickname, UserRole role, List<Language> languages) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.languages = languages;

    }

    public User(String username, String password, String nickname, UserRole role) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }

    public User update(String name){
        this.username = name;

        return this;
    }

    public String getRoleKey(){
        return this.role.toString();
    }

    public void evaluate(double point) {
        this.ranking.evaluate(point);
    }

    public void increaseAnswerCount() {
        this.ranking.increaseAnswerCount();
    }

}
