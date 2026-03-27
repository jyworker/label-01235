package com.tourism.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tourism.dto.CategoryDTO;
import com.tourism.entity.Category;

import java.util.List;

/**
 * 景点分类服务接口
 */
public interface CategoryService extends IService<Category> {

    /**
     * 获取分类列表（按排序字段排序）
     */
    List<Category> listCategories();

    /**
     * 新增分类
     */
    void addCategory(CategoryDTO dto);

    /**
     * 编辑分类
     */
    void updateCategory(CategoryDTO dto);

    /**
     * 删除分类（检查关联景点）
     */
    void deleteCategory(Long id);
}
