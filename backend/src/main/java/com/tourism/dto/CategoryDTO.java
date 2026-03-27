package com.tourism.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 分类新增/编辑 DTO
 */
@Data
public class CategoryDTO {

    private Long id;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private String icon;

    private Integer sortOrder;
}
