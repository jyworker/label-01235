package com.tourism.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 评论发表 DTO
 */
@Data
public class ReviewDTO {

    @NotNull(message = "景点ID不能为空")
    private Long scenicSpotId;

    @NotBlank(message = "评论内容不能为空")
    private String content;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低1分")
    @Max(value = 5, message = "评分最高5分")
    private Integer rating;
}
