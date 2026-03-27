package com.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tourism.entity.Category;
import com.tourism.entity.ScenicSpot;
import com.tourism.entity.User;
import com.tourism.mapper.*;
import com.tourism.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统计服务实现类
 */
@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ScenicSpotMapper scenicSpotMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Map<String, Object> overview() {
        Map<String, Object> data = new HashMap<>();

        // 用户数
        long userCount = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getStatus, 1));
        data.put("userCount", userCount);

        // 景点数
        int scenicSpotCount = scenicSpotMapper.countActive();
        data.put("scenicSpotCount", scenicSpotCount);

        // 订单数
        long orderCount = orderMapper.selectCount(null);
        data.put("orderCount", orderCount);

        // 总收入
        BigDecimal totalRevenue = orderMapper.sumTotalRevenue();
        data.put("totalRevenue", totalRevenue);

        // 今日订单数
        int todayOrderCount = orderMapper.countToday();
        data.put("todayOrderCount", todayOrderCount);

        return data;
    }

    @Override
    public List<Map<String, Object>> orderTrend() {
        return orderMapper.orderTrend7Days();
    }

    @Override
    public List<Map<String, Object>> hotTop10() {
        List<ScenicSpot> spots = scenicSpotMapper.selectList(
                new LambdaQueryWrapper<ScenicSpot>()
                        .eq(ScenicSpot::getStatus, 1)
                        .orderByDesc(ScenicSpot::getVisitCount)
                        .last("LIMIT 10"));

        return spots.stream().map(spot -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", spot.getId());
            map.put("name", spot.getName());
            map.put("visitCount", spot.getVisitCount());
            map.put("avgRating", spot.getAvgRating());
            map.put("coverImage", spot.getCoverImage());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> categoryDistribution() {
        // 获取所有分类
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getStatus, 1)
                        .orderByAsc(Category::getSortOrder));

        // 一次性按分类分组统计景点数量，避免 N+1
        List<Map<String, Object>> countList = scenicSpotMapper.countGroupByCategory();
        Map<Long, Long> countMap = new HashMap<>();
        for (Map<String, Object> row : countList) {
            Object categoryIdObj = row.get("categoryId");
            Object countObj = row.get("count");
            if (categoryIdObj != null && countObj != null) {
                Long categoryId = ((Number) categoryIdObj).longValue();
                Long count = ((Number) countObj).longValue();
                countMap.put(categoryId, count);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Category category : categories) {
            Map<String, Object> map = new HashMap<>();
            map.put("categoryId", category.getId());
            map.put("categoryName", category.getName());
            map.put("count", countMap.getOrDefault(category.getId(), 0L));
            result.add(map);
        }
        return result;
    }
}
