package com.tourism.service;

import java.util.List;
import java.util.Map;

/**
 * 统计服务接口
 */
public interface StatisticsService {

    /**
     * 总览数据
     */
    Map<String, Object> overview();

    /**
     * 订单趋势（近7天）
     */
    List<Map<String, Object>> orderTrend();

    /**
     * 热门景点TOP10
     */
    List<Map<String, Object>> hotTop10();

    /**
     * 分类分布
     */
    List<Map<String, Object>> categoryDistribution();
}
