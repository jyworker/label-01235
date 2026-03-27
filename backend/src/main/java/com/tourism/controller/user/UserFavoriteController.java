package com.tourism.controller.user;

import com.tourism.common.PageResult;
import com.tourism.common.Result;
import com.tourism.service.FavoriteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户端收藏控制器
 */
@Api(tags = "用户端-收藏接口")
@RestController
@RequestMapping("/api/favorite")
public class UserFavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @ApiOperation("收藏/取消收藏")
    @PostMapping("/toggle")
    public Result<Map<String, Object>> toggle(@RequestBody Map<String, Long> body) {
        Long scenicSpotId = body.get("scenicSpotId");
        boolean favorited = favoriteService.toggleFavorite(scenicSpotId);
        Map<String, Object> data = new HashMap<>();
        data.put("favorited", favorited);
        data.put("message", favorited ? "收藏成功" : "已取消收藏");
        return Result.success(data);
    }

    @ApiOperation("我的收藏列表")
    @GetMapping("/my")
    public Result<PageResult<Map<String, Object>>> myFavorites(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Map<String, Object>> result = favoriteService.myFavorites(pageNum, pageSize);
        return Result.success(result);
    }

    @ApiOperation("检查是否已收藏")
    @GetMapping("/check/{scenicId}")
    public Result<Map<String, Object>> check(@PathVariable Long scenicId) {
        boolean favorited = favoriteService.isFavorited(scenicId);
        Map<String, Object> data = new HashMap<>();
        data.put("favorited", favorited);
        return Result.success(data);
    }
}
