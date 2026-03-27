package com.tourism.controller.admin;

import com.tourism.common.PageResult;
import com.tourism.common.Result;
import com.tourism.service.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理端评论控制器
 */
@Api(tags = "管理端-评论管理")
@RestController
@RequestMapping("/api/admin/review")
public class AdminReviewController {

    @Autowired
    private ReviewService reviewService;

    @ApiOperation("评论列表")
    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        PageResult<Map<String, Object>> result = reviewService.adminListReviews(pageNum, pageSize, status, keyword);
        return Result.success(result);
    }

    @ApiOperation("审核评论")
    @PutMapping("/{id}/audit")
    public Result<?> audit(@PathVariable Long id, @RequestParam Integer status) {
        reviewService.auditReview(id, status);
        String msg = status == 1 ? "审核通过" : "审核拒绝";
        return Result.success(msg, null);
    }

    @ApiOperation("删除评论")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        reviewService.adminDeleteReview(id);
        return Result.success("删除成功", null);
    }
}
