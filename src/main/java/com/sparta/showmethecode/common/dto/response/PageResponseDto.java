package com.sparta.showmethecode.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class PageResponseDto<T> {

    private List<T> data = new ArrayList<>();

    private int totalPage;
    private long totalElements;
    private int page;
    private int size;

    public PageResponseDto(List<T> data, int totalPage, int totalElements, int page, int size ) {
        this.data = data;

        this.totalPage = totalPage;
        this.totalElements = totalElements;
        this.page = page++;
        this.size = size;
    }
}
