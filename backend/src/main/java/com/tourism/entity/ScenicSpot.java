package com.tourism.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 景点信息实体
 */
@Data
@TableName("scenic_spot")
public class ScenicSpot {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Long categoryId;

    private String coverImage;

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

    private BigDecimal avgRating;

    private Integer reviewCount;

    private Integer visitCount;

    private Integer status;

    private Integer isHot;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Version
    @TableField(fill = FieldFill.INSERT)
    private Integer version;
}
