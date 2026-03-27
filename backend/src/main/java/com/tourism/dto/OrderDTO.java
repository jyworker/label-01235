package com.tourism.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建订单 DTO
 */
@Data
public class OrderDTO {

    @NotNull(message = "景点ID不能为空")
    private Long scenicSpotId;

    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量至少为1")
    private Integer quantity;

    private String visitDate;

    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;

    @NotBlank(message = "联系人电话不能为空")
    private String contactPhone;
}
