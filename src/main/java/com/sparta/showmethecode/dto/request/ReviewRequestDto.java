package com.sparta.showmethecode.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ReviewRequestDto {

    private String title;
    private String code;
    private String comment;

    private String language;
    private Long reviewerId;
}
