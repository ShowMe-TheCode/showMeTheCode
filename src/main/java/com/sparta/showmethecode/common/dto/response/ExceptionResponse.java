package com.sparta.showmethecode.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ExceptionResponse {

    private String message;
    private HttpStatus httpStatus;
}
