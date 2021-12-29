package com.sparta.showmethecode.question.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AddQuestionDto {

    private String title;
    private String content;

    private String language;
    private Long reviewerId;
}
