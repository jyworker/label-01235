package com.tourism.service.impl;

import com.tourism.config.StpUserUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tourism.common.BusinessException;
import com.tourism.common.PageResult;
import com.tourism.entity.Favorite;
import com.tourism.entity.ScenicSpot;
import com.tourism.mapper.FavoriteMapper;
import com.tourism.mapper.ScenicSpotMapper;
import com.tourism.service.FavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 收藏服务实现类
 */
@Slf4j
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Autowired
    private ScenicSpotMapper scenicSpotMapper;

    @Override
    public boolean toggleFavorite(Long scenicSpotId) {
        Long userId = StpUserUtil.getLoginIdAsLong();

        // 检查景点是否存在
        ScenicSpot spot = scenicSpotMapper.selectById(scenicSpotId);
        if (spot == null) {
            throw new BusinessException("景点不存在");
        }

        // 查询是否已收藏
        Favorite existing = this.getOne(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getScenicSpotId, scenicSpotId));

        if (existing != null) {
            // 已收藏，取消收藏
            this.removeById(existing.getId());
            log.info("取消收藏: userId={}, scenicSpotId={}", userId, scenicSpotId);
            return false;
        } else {
            // 未收藏，添加收藏
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setScenicSpotId(scenicSpotId);
            this.save(favorite);
            log.info("添加收藏: userId={}, scenicSpotId={}", userId, scenicSpotId);
            return true;
        }
    }

    @Override
    public PageResult<Map<String, Object>> myFavorites(Integer pageNum, Integer pageSize) {
        Long userId = StpUserUtil.getLoginIdAsLong();
        Page<Favorite> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreateTime);

        Page<Favorite> result = this.page(page, wrapper);
        List<Map<String, Object>> records = result.getRecords().stream()
                .map(fav -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", fav.getId());
                    map.put("scenicSpotId", fav.getScenicSpotId());
                    map.put("createTime", fav.getCreateTime());

                    // 景点信息
                    ScenicSpot spot = scenicSpotMapper.selectById(fav.getScenicSpotId());
                    if (spot != null) {
                        map.put("scenicSpotName", spot.getName());
                        map.put("coverImage", spot.getCoverImage());
                        map.put("description", spot.getDescription());
                        map.put("province", spot.getProvince());
                        map.put("city", spot.getCity());
                        map.put("ticketPrice", spot.getTicketPrice());
                        map.put("avgRating", spot.getAvgRating());
                    }
                    return map;
                })
                .collect(Collectors.toList());

        return PageResult.of(records, result.getTotal(), pageNum, pageSize);
    }

    @Override
    public boolean isFavorited(Long scenicSpotId) {
        Long userId = StpUserUtil.getLoginIdAsLong();
        long count = this.count(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getScenicSpotId, scenicSpotId));
        return count > 0;
    }
}
