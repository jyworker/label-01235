package com.tourism.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tourism.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 景点分类 Mapper
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
