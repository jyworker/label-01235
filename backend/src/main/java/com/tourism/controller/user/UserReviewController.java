package com.tourism.controller.user;

import com.tourism.common.PageResult;
import com.tourism.common.Result;
import com.tourism.dto.ReviewDTO;
import com.tourism.service.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 用户端评论控制器
 */
@Api(tags = "用户端-评论接口")
@RestController
@RequestMapping("/api/review")
public class UserReviewController {

    @Autowired
    private ReviewService reviewService;

    @ApiOperation("获取景点评论列表")
    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> list(
            @RequestParam Long scenicSpotId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Map<String, Object>> result = reviewService.listReviews(scenicSpotId, pageNum, pageSize);
        return Result.success(result);
    }

    @ApiOperation("发表评论（multipart 方式，支持图片上传）")
    @PostMapping("/add")
    public Result<?> add(@Valid ReviewDTO dto,
                         @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        reviewService.addReview(dto, images);
        return Result.success("评论发表成功", null);
    }

    @ApiOperation("发表评论（JSON方式，不含图片）")
    @PostMapping("/submit")
    public Result<?> submit(@Valid @RequestBody ReviewDTO dto) {
        reviewService.addReview(dto, null);
        return Result.success("评论已提交，待审核后可见", null);
    }

    @ApiOperation("删除我的评论")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        reviewService.deleteMyReview(id);
        return Result.success("评论删除成功", null);
    }

    @ApiOperation("我的评论列表")
    @GetMapping("/my")
    public Result<PageResult<Map<String, Object>>> myReviews(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Map<String, Object>> result = reviewService.myReviews(pageNum, pageSize);
        return Result.success(result);
    }
}
