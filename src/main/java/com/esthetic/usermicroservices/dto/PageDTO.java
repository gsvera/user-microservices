package com.esthetic.usermicroservices.dto;

import org.springframework.data.domain.Page;

public class PageDTO {
    public int pageNumber;
    public int pageSize;
    public Long totalElements;
    public int totalPages;
    public Boolean isLast;
    public Object items;
    public PageDTO(Page page) {
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.isLast = page.isLast();
    }
}
