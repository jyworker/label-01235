package com.tourism.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 景点查询 DTO
 */
@Data
public class ScenicSpotQueryDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 12;

    private Long categoryId;

    private String keyword;

    private String province;

    private String city;

    private String sortBy;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private BigDecimal minRating;

    private Integer status;
}
