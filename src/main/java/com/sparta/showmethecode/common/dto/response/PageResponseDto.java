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

    public PageResponseDto(Page<T> pageData) {
        this.data = pageData.getContent();

        this.totalPage = pageData.getTotalPages();
        this.totalElements = pageData.getTotalElements();
        this.page = pageData.getNumber();
        this.page = pageData.getSize();
    }
}
