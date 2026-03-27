package com.tourism.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tourism.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员 Mapper
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
}
