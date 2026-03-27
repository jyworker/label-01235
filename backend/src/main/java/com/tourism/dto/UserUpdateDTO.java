package com.tourism.dto;

import lombok.Data;

/**
 * 用户信息更新 DTO
 */
@Data
public class UserUpdateDTO {

    private String nickname;

    private String email;

    private String phone;

    private Integer gender;
}
