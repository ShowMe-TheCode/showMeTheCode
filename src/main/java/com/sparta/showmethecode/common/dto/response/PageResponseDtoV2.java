package com.sparta.showmethecode.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class PageResponseDtoV2<T> {

    private List<T> data = new ArrayList<>();

    private Long lastId;
}
