package com.tourism.controller.admin;

import com.tourism.common.Result;
import com.tourism.dto.CategoryDTO;
import com.tourism.entity.Category;
import com.tourism.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 管理端分类控制器
 */
@Api(tags = "管理端-分类管理")
@RestController
@RequestMapping("/api/admin/category")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation("分类列表")
    @GetMapping("/list")
    public Result<List<Category>> list() {
        List<Category> categories = categoryService.listCategories();
        return Result.success(categories);
    }

    @ApiOperation("新增分类")
    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody CategoryDTO dto) {
        categoryService.addCategory(dto);
        return Result.success("新增成功", null);
    }

    @ApiOperation("编辑分类")
    @PutMapping("/update")
    public Result<?> update(@Valid @RequestBody CategoryDTO dto) {
        categoryService.updateCategory(dto);
        return Result.success("更新成功", null);
    }

    @ApiOperation("删除分类")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success("删除成功", null);
    }
}
