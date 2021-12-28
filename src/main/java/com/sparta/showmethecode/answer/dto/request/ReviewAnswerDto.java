package com.sparta.showmethecode.answer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ReviewAnswerDto {
    private String content;
    private Long reviewerId;

}
