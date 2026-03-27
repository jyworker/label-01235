package com.tourism.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tourism.common.PageResult;
import com.tourism.dto.ScenicSpotDTO;
import com.tourism.dto.ScenicSpotQueryDTO;
import com.tourism.entity.ScenicSpot;

import java.util.List;
import java.util.Map;

/**
 * 景点服务接口
 */
public interface ScenicSpotService extends IService<ScenicSpot> {

    /**
     * 分页查询景点列表（用户端）
     */
    PageResult<Map<String, Object>> listScenicSpots(ScenicSpotQueryDTO queryDTO);

    /**
     * 获取景点详情（含图片列表，记录访问量）
     */
    Map<String, Object> getScenicSpotDetail(Long id);

    /**
     * 获取景点详情（管理端，不记录访问量，可查看已下架）
     */
    Map<String, Object> getScenicSpotDetailForAdmin(Long id);

    /**
     * 热门推荐（is_hot=1，取前8）
     */
    List<ScenicSpot> getHotScenicSpots();

    /**
     * 搜索景点（支持排序）
     */
    PageResult<Map<String, Object>> searchScenicSpots(String keyword, String sortBy, Integer pageNum, Integer pageSize);

    /**
     * 新增景点（管理端）
     */
    void addScenicSpot(ScenicSpotDTO dto);

    /**
     * 编辑景点（管理端）
     */
    void updateScenicSpot(ScenicSpotDTO dto);

    /**
     * 删除景点（管理端）
     */
    void deleteScenicSpot(Long id);

    /**
     * 上架/下架景点（管理端）
     */
    void updateScenicSpotStatus(Long id, Integer status);

    /**
     * 景点列表（管理端，含所有状态）
     */
    PageResult<Map<String, Object>> adminListScenicSpots(ScenicSpotQueryDTO queryDTO);

    /**
     * 个性化推荐（基于用户收藏的分类偏好）
     */
    List<ScenicSpot> getRecommendedScenicSpots();
}
