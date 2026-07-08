package com.huuhv.foodsndrinks.dto.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/** Stable JSON shape for paged API responses (serializing PageImpl directly is unsupported/unstable). */
@Getter
public class PageResDto<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean last;

    private PageResDto(Page<T> p) {
        this.content       = p.getContent();
        this.page          = p.getNumber();
        this.size          = p.getSize();
        this.totalElements = p.getTotalElements();
        this.totalPages    = p.getTotalPages();
        this.last          = p.isLast();
    }

    public static <T> PageResDto<T> from(Page<T> page) {
        return new PageResDto<>(page);
    }
}
