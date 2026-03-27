package com.tourism.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结果类
 */
@Data
public class PageResult<T> implements Serializable {

    private List<T> records;
    private long total;
    private int pageNum;
    private int pageSize;

    public PageResult() {}

    public PageResult(List<T> records, long total, int pageNum, int pageSize) {
        this.records = records;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public static <T> PageResult<T> of(List<T> records, long total, int pageNum, int pageSize) {
        return new PageResult<>(records, total, pageNum, pageSize);
    }
}
