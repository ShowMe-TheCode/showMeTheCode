package com.sparta.showmethecode.question.domain;

import lombok.Getter;

@Getter
public enum QuestionStatus {

    UNSOLVE("미해결"),
    CHECKED("확인됨"),
    REJECTED("거절됨"),
    SOLVE("해결됨"),

    EVALUATED("평가됨"),
    ALL("ALL");

    private final String description;

    QuestionStatus(String description) {
        this.description = description;
    }
}
