package com.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tourism.common.BusinessException;
import com.tourism.dto.CategoryDTO;
import com.tourism.entity.Category;
import com.tourism.entity.ScenicSpot;
import com.tourism.mapper.CategoryMapper;
import com.tourism.mapper.ScenicSpotMapper;
import com.tourism.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 景点分类服务实现类
 */
@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private ScenicSpotMapper scenicSpotMapper;

    @Override
    public List<Category> listCategories() {
        return this.list(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSortOrder)
                .orderByAsc(Category::getId));
    }

    @Override
    public void addCategory(CategoryDTO dto) {
        // 检查分类名是否已存在
        long count = this.count(new LambdaQueryWrapper<Category>()
                .eq(Category::getName, dto.getName()));
        if (count > 0) {
            throw new BusinessException("分类名称已存在");
        }

        Category category = new Category();
        category.setName(dto.getName());
        category.setIcon(dto.getIcon());
        category.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        category.setStatus(1);
        this.save(category);

        log.info("新增分类: name={}", dto.getName());
    }

    @Override
    public void updateCategory(CategoryDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("分类ID不能为空");
        }

        Category existing = this.getById(dto.getId());
        if (existing == null) {
            throw new BusinessException("分类不存在");
        }

        // 检查名称是否与其他分类重复
        long count = this.count(new LambdaQueryWrapper<Category>()
                .eq(Category::getName, dto.getName())
                .ne(Category::getId, dto.getId()));
        if (count > 0) {
            throw new BusinessException("分类名称已存在");
        }

        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setIcon(dto.getIcon());
        category.setSortOrder(dto.getSortOrder());
        category.setVersion(existing.getVersion());
        boolean success = this.updateById(category);
        if (!success) {
            throw new BusinessException("分类信息已被其他用户修改，请刷新后重试");
        }

        log.info("编辑分类: id={}, name={}", dto.getId(), dto.getName());
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = this.getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // 检查是否有关联景点
        long spotCount = scenicSpotMapper.selectCount(new LambdaQueryWrapper<ScenicSpot>()
                .eq(ScenicSpot::getCategoryId, id));
        if (spotCount > 0) {
            throw new BusinessException("该分类下有关联景点，无法删除");
        }

        this.removeById(id);
        log.info("删除分类: id={}, name={}", id, category.getName());
    }
}
