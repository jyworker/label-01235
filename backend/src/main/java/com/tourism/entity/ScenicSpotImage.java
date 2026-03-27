package com.tourism.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 景点图片实体
 */
@Data
@TableName("scenic_spot_image")
public class ScenicSpotImage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long scenicSpotId;

    private String imageUrl;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
