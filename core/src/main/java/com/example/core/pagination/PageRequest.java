package com.example.core.pagination;

public class PageRequest {

    private final int page;
    private final int size;
    private final String sort;

    public PageRequest(int page, int size, String sort) {
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Size must be between 1 and 100");
        }

        this.page = page;
        this.size = size;
        this.sort = sort;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public String getSort() {
        return sort;
    }
}