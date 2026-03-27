package com.tourism.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tourism.common.PageResult;
import com.tourism.entity.Favorite;

import java.util.Map;

/**
 * 收藏服务接口
 */
public interface FavoriteService extends IService<Favorite> {

    /**
     * 收藏/取消收藏（toggle）
     */
    boolean toggleFavorite(Long scenicSpotId);

    /**
     * 我的收藏列表（含景点信息）
     */
    PageResult<Map<String, Object>> myFavorites(Integer pageNum, Integer pageSize);

    /**
     * 检查是否已收藏
     */
    boolean isFavorited(Long scenicSpotId);
}
