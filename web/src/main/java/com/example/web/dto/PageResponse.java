package com.example.web.dto;

import com.example.core.pagination.Page;

import java.util.List;
import java.util.function.Function;

public class PageResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public PageResponse() {
    }

    public PageResponse(
            List<T> content,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public static <S, T> PageResponse<T> from(
            Page<S> page,
            Function<S, T> mapper
    ) {
        List<T> content = page.getContent()
                .stream()
                .map(mapper)
                .toList();

        return new PageResponse<>(
                content,
                page.getPage(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public List<T> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }
}