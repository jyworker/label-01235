package com.tourism.controller.user;

import com.tourism.common.PageResult;
import com.tourism.common.Result;
import com.tourism.dto.ScenicSpotQueryDTO;
import com.tourism.entity.Category;
import com.tourism.entity.ScenicSpot;
import com.tourism.service.CategoryService;
import com.tourism.service.ScenicSpotService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户端景点控制器
 */
@Api(tags = "用户端-景点接口")
@RestController
@RequestMapping("/api/scenic")
public class UserScenicSpotController {

    @Autowired
    private ScenicSpotService scenicSpotService;

    @Autowired
    private CategoryService categoryService;

    @ApiOperation("景点列表（分页）")
    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> list(ScenicSpotQueryDTO queryDTO) {
        PageResult<Map<String, Object>> result = scenicSpotService.listScenicSpots(queryDTO);
        return Result.success(result);
    }

    @ApiOperation("景点详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        Map<String, Object> detail = scenicSpotService.getScenicSpotDetail(id);
        return Result.success(detail);
    }

    @ApiOperation("热门推荐")
    @GetMapping("/hot")
    public Result<List<ScenicSpot>> hot() {
        List<ScenicSpot> hotSpots = scenicSpotService.getHotScenicSpots();
        return Result.success(hotSpots);
    }

    @ApiOperation("搜索景点")
    @GetMapping("/search")
    public Result<PageResult<Map<String, Object>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "12") Integer pageSize) {
        PageResult<Map<String, Object>> result = scenicSpotService.searchScenicSpots(keyword, sortBy, pageNum, pageSize);
        return Result.success(result);
    }

    @ApiOperation("获取分类列表")
    @GetMapping("/categories")
    public Result<List<Category>> categories() {
        List<Category> categories = categoryService.listCategories();
        return Result.success(categories);
    }

    @ApiOperation("个性化推荐")
    @GetMapping("/recommend")
    public Result<List<ScenicSpot>> recommend() {
        List<ScenicSpot> recommended = scenicSpotService.getRecommendedScenicSpots();
        return Result.success(recommended);
    }
}
