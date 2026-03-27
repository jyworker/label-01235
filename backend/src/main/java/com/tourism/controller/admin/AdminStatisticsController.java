package com.tourism.controller.admin;

import com.tourism.common.Result;
import com.tourism.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 管理端统计控制器
 */
@Api(tags = "管理端-数据统计")
@RestController
@RequestMapping("/api/admin/statistics")
public class AdminStatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @ApiOperation("总览数据")
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        Map<String, Object> data = statisticsService.overview();
        return Result.success(data);
    }

    @ApiOperation("订单趋势（近7天）")
    @GetMapping("/order-trend")
    public Result<List<Map<String, Object>>> orderTrend() {
        List<Map<String, Object>> data = statisticsService.orderTrend();
        return Result.success(data);
    }

    @ApiOperation("热门景点TOP10")
    @GetMapping("/hot-top10")
    public Result<List<Map<String, Object>>> hotTop10() {
        List<Map<String, Object>> data = statisticsService.hotTop10();
        return Result.success(data);
    }

    @ApiOperation("分类分布")
    @GetMapping("/category-distribution")
    public Result<List<Map<String, Object>>> categoryDistribution() {
        List<Map<String, Object>> data = statisticsService.categoryDistribution();
        return Result.success(data);
    }
}
