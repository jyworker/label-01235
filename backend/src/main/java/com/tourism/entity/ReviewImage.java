package com.tourism.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论图片实体
 */
@Data
@TableName("review_image")
public class ReviewImage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long reviewId;

    private String imageUrl;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
