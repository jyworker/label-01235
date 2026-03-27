package com.tourism.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

/**
 * 景点新增/编辑 DTO
 */
@Data
public class ScenicSpotDTO {

    private Long id;

    @NotBlank(message = "景点名称不能为空")
    private String name;

    private Long categoryId;

    private String coverImage;

    private List<String> images;

    private String description;

    private String detailContent;

    private String videoUrl;

    private String province;

    private String city;

    private String address;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private BigDecimal ticketPrice;

    private String openTime;

    private String closeTime;

    private String tips;

    private Integer isHot;
}
