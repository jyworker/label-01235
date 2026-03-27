package com.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tourism.common.BusinessException;
import com.tourism.common.PageResult;
import com.tourism.config.StpUserUtil;
import com.tourism.dto.ScenicSpotDTO;
import com.tourism.dto.ScenicSpotQueryDTO;
import com.tourism.entity.Category;
import com.tourism.entity.Favorite;
import com.tourism.entity.ScenicSpot;
import com.tourism.entity.ScenicSpotImage;
import com.tourism.mapper.CategoryMapper;
import com.tourism.mapper.FavoriteMapper;
import com.tourism.mapper.ScenicSpotImageMapper;
import com.tourism.mapper.ScenicSpotMapper;
import com.tourism.service.ScenicSpotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 景点服务实现类
 */
@Slf4j
@Service
public class ScenicSpotServiceImpl extends ServiceImpl<ScenicSpotMapper, ScenicSpot> implements ScenicSpotService {

    @Autowired
    private ScenicSpotImageMapper scenicSpotImageMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public PageResult<Map<String, Object>> listScenicSpots(ScenicSpotQueryDTO queryDTO) {
        Page<ScenicSpot> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<ScenicSpot> wrapper = buildQueryWrapper(queryDTO);
        // 用户端只查上架的
        wrapper.eq(ScenicSpot::getStatus, 1);

        // 排序
        applySorting(wrapper, queryDTO.getSortBy());

        Page<ScenicSpot> result = this.page(page, wrapper);
        List<Map<String, Object>> records = result.getRecords().stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());

        return PageResult.of(records, result.getTotal(),
                queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public Map<String, Object> getScenicSpotDetail(Long id) {
        return buildScenicSpotDetail(id, true);
    }

    @Override
    public Map<String, Object> getScenicSpotDetailForAdmin(Long id) {
        return buildScenicSpotDetail(id, false);
    }

    private Map<String, Object> buildScenicSpotDetail(Long id, boolean forUser) {
        ScenicSpot spot = this.getById(id);
        if (spot == null) {
            throw new BusinessException("景点不存在");
        }
        if (forUser && spot.getStatus() != 1) {
            throw new BusinessException("景点不存在或已下架");
        }

        // 仅用户端查看详情时记录访问量
        if (forUser) {
            baseMapper.incrementVisitCount(id);
        }

        // 获取图片列表
        List<ScenicSpotImage> images = scenicSpotImageMapper.selectList(
                new LambdaQueryWrapper<ScenicSpotImage>()
                        .eq(ScenicSpotImage::getScenicSpotId, id)
                        .orderByAsc(ScenicSpotImage::getSortOrder));

        // 获取分类信息
        Map<String, Object> detail = convertToMap(spot);
        detail.put("images", images.stream()
                .map(ScenicSpotImage::getImageUrl)
                .collect(Collectors.toList()));

        if (spot.getCategoryId() != null) {
            Category category = categoryMapper.selectById(spot.getCategoryId());
            if (category != null) {
                detail.put("categoryName", category.getName());
            }
        }

        return detail;
    }

    @Override
    public List<ScenicSpot> getHotScenicSpots() {
        return this.list(new LambdaQueryWrapper<ScenicSpot>()
                .eq(ScenicSpot::getStatus, 1)
                .eq(ScenicSpot::getIsHot, 1)
                .orderByDesc(ScenicSpot::getVisitCount)
                .last("LIMIT 8"));
    }

    @Override
    public PageResult<Map<String, Object>> searchScenicSpots(String keyword, String sortBy, Integer pageNum, Integer pageSize) {
        Page<ScenicSpot> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ScenicSpot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScenicSpot::getStatus, 1);

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(ScenicSpot::getName, keyword)
                    .or().like(ScenicSpot::getDescription, keyword)
                    .or().like(ScenicSpot::getAddress, keyword)
                    .or().like(ScenicSpot::getProvince, keyword)
                    .or().like(ScenicSpot::getCity, keyword));
        }
        applySorting(wrapper, sortBy);

        Page<ScenicSpot> result = this.page(page, wrapper);
        List<Map<String, Object>> records = result.getRecords().stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());

        return PageResult.of(records, result.getTotal(), pageNum, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addScenicSpot(ScenicSpotDTO dto) {
        ScenicSpot spot = new ScenicSpot();
        BeanUtils.copyProperties(dto, spot);
        spot.setStatus(1);
        spot.setAvgRating(new java.math.BigDecimal("0"));
        spot.setReviewCount(0);
        spot.setVisitCount(0);
        this.save(spot);

        // 保存图片
        saveImages(spot.getId(), dto.getImages());

        log.info("新增景点: id={}, name={}", spot.getId(), spot.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateScenicSpot(ScenicSpotDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("景点ID不能为空");
        }

        ScenicSpot existing = this.getById(dto.getId());
        if (existing == null) {
            throw new BusinessException("景点不存在");
        }

        ScenicSpot spot = new ScenicSpot();
        BeanUtils.copyProperties(dto, spot);
        this.updateById(spot);

        // 更新图片：先删除旧图片，再保存新图片
        if (dto.getImages() != null) {
            scenicSpotImageMapper.delete(new LambdaQueryWrapper<ScenicSpotImage>()
                    .eq(ScenicSpotImage::getScenicSpotId, dto.getId()));
            saveImages(dto.getId(), dto.getImages());
        }

        log.info("编辑景点: id={}, name={}", dto.getId(), dto.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteScenicSpot(Long id) {
        ScenicSpot spot = this.getById(id);
        if (spot == null) {
            throw new BusinessException("景点不存在");
        }

        // 删除关联图片
        scenicSpotImageMapper.delete(new LambdaQueryWrapper<ScenicSpotImage>()
                .eq(ScenicSpotImage::getScenicSpotId, id));

        this.removeById(id);
        log.info("删除景点: id={}, name={}", id, spot.getName());
    }

    @Override
    public void updateScenicSpotStatus(Long id, Integer status) {
        ScenicSpot spot = this.getById(id);
        if (spot == null) {
            throw new BusinessException("景点不存在");
        }

        ScenicSpot update = new ScenicSpot();
        update.setId(id);
        update.setStatus(status);
        this.updateById(update);

        log.info("更新景点状态: id={}, status={}", id, status);
    }

    @Override
    public PageResult<Map<String, Object>> adminListScenicSpots(ScenicSpotQueryDTO queryDTO) {
        Page<ScenicSpot> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<ScenicSpot> wrapper = buildQueryWrapper(queryDTO);

        // 管理端可以按状态筛选
        if (queryDTO.getStatus() != null) {
            wrapper.eq(ScenicSpot::getStatus, queryDTO.getStatus());
        }

        wrapper.orderByDesc(ScenicSpot::getCreateTime);

        Page<ScenicSpot> result = this.page(page, wrapper);

        // 批量查询分类信息，避免 N+1
        List<ScenicSpot> spots = result.getRecords();
        List<Long> categoryIds = spots.stream()
                .map(ScenicSpot::getCategoryId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> categoryMap = new HashMap<>();
        if (!categoryIds.isEmpty()) {
            categoryMapper.selectBatchIds(categoryIds).forEach(
                    cat -> categoryMap.put(cat.getId(), cat.getName()));
        }

        List<Map<String, Object>> records = spots.stream()
                .map(spot -> {
                    Map<String, Object> map = convertToMap(spot);
                    if (spot.getCategoryId() != null && categoryMap.containsKey(spot.getCategoryId())) {
                        map.put("categoryName", categoryMap.get(spot.getCategoryId()));
                    }
                    return map;
                })
                .collect(Collectors.toList());

        return PageResult.of(records, result.getTotal(),
                queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<ScenicSpot> buildQueryWrapper(ScenicSpotQueryDTO queryDTO) {
        LambdaQueryWrapper<ScenicSpot> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.and(w -> w.like(ScenicSpot::getName, queryDTO.getKeyword())
                    .or().like(ScenicSpot::getDescription, queryDTO.getKeyword()));
        }

        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(ScenicSpot::getCategoryId, queryDTO.getCategoryId());
        }

        if (StringUtils.hasText(queryDTO.getProvince())) {
            wrapper.eq(ScenicSpot::getProvince, queryDTO.getProvince());
        }

        if (StringUtils.hasText(queryDTO.getCity())) {
            wrapper.eq(ScenicSpot::getCity, queryDTO.getCity());
        }

        if (queryDTO.getMinPrice() != null) {
            wrapper.ge(ScenicSpot::getTicketPrice, queryDTO.getMinPrice());
        }

        if (queryDTO.getMaxPrice() != null) {
            wrapper.le(ScenicSpot::getTicketPrice, queryDTO.getMaxPrice());
        }

        if (queryDTO.getMinRating() != null) {
            wrapper.ge(ScenicSpot::getAvgRating, queryDTO.getMinRating());
        }

        return wrapper;
    }

    /**
     * 应用排序
     */
    private void applySorting(LambdaQueryWrapper<ScenicSpot> wrapper, String sortBy) {
        if ("price_asc".equals(sortBy)) {
            wrapper.orderByAsc(ScenicSpot::getTicketPrice);
        } else if ("price_desc".equals(sortBy)) {
            wrapper.orderByDesc(ScenicSpot::getTicketPrice);
        } else if ("rating".equals(sortBy)) {
            wrapper.orderByDesc(ScenicSpot::getAvgRating);
        } else if ("visit".equals(sortBy)) {
            wrapper.orderByDesc(ScenicSpot::getVisitCount);
        } else {
            // 默认按创建时间降序
            wrapper.orderByDesc(ScenicSpot::getCreateTime);
        }
    }

    /**
     * 保存景点图片
     */
    private void saveImages(Long scenicSpotId, List<String> images) {
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                String imageUrl = images.get(i);
                // 过滤前端本地 blob URL，避免落库无效地址
                if (!StringUtils.hasText(imageUrl) || imageUrl.startsWith("blob:")) {
                    continue;
                }
                ScenicSpotImage image = new ScenicSpotImage();
                image.setScenicSpotId(scenicSpotId);
                image.setImageUrl(imageUrl);
                image.setSortOrder(i);
                scenicSpotImageMapper.insert(image);
            }
        }
    }

    /**
     * 景点转 Map
     */
    private Map<String, Object> convertToMap(ScenicSpot spot) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", spot.getId());
        map.put("name", spot.getName());
        map.put("categoryId", spot.getCategoryId());
        map.put("coverImage", spot.getCoverImage());
        map.put("description", spot.getDescription());
        map.put("detailContent", spot.getDetailContent());
        map.put("videoUrl", spot.getVideoUrl());
        map.put("province", spot.getProvince());
        map.put("city", spot.getCity());
        map.put("address", spot.getAddress());
        map.put("longitude", spot.getLongitude());
        map.put("latitude", spot.getLatitude());
        map.put("ticketPrice", spot.getTicketPrice());
        map.put("openTime", spot.getOpenTime());
        map.put("closeTime", spot.getCloseTime());
        map.put("tips", spot.getTips());
        map.put("avgRating", spot.getAvgRating());
        map.put("reviewCount", spot.getReviewCount());
        map.put("visitCount", spot.getVisitCount());
        map.put("status", spot.getStatus());
        map.put("isHot", spot.getIsHot());
        map.put("createTime", spot.getCreateTime());
        return map;
    }

    /**
     * 景点转 Map，含分类名称
     */
    private Map<String, Object> convertToMapWithCategory(ScenicSpot spot) {
        Map<String, Object> map = convertToMap(spot);
        if (spot.getCategoryId() != null) {
            Category category = categoryMapper.selectById(spot.getCategoryId());
            if (category != null) {
                map.put("categoryName", category.getName());
            }
        }
        return map;
    }

    @Override
    public List<ScenicSpot> getRecommendedScenicSpots() {
        try {
            Long userId = StpUserUtil.getLoginIdAsLong();
            // 获取用户收藏的景点ID
            List<Favorite> favorites = favoriteMapper.selectList(
                    new LambdaQueryWrapper<Favorite>().eq(Favorite::getUserId, userId));

            if (!favorites.isEmpty()) {
                // 获取收藏景点的分类ID
                List<Long> spotIds = favorites.stream()
                        .map(Favorite::getScenicSpotId)
                        .collect(Collectors.toList());
                List<ScenicSpot> favSpots = this.listByIds(spotIds);
                List<Long> categoryIds = favSpots.stream()
                        .map(ScenicSpot::getCategoryId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList());

                if (!categoryIds.isEmpty()) {
                    // 推荐同分类的其他景点
                    return this.list(new LambdaQueryWrapper<ScenicSpot>()
                            .eq(ScenicSpot::getStatus, 1)
                            .in(ScenicSpot::getCategoryId, categoryIds)
                            .notIn(ScenicSpot::getId, spotIds)
                            .orderByDesc(ScenicSpot::getAvgRating)
                            .last("LIMIT 6"));
                }
            }
        } catch (Exception e) {
            // 未登录用户，返回默认推荐
        }
        // 默认推荐：高评分景点
        return this.list(new LambdaQueryWrapper<ScenicSpot>()
                .eq(ScenicSpot::getStatus, 1)
                .orderByDesc(ScenicSpot::getAvgRating)
                .last("LIMIT 6"));
    }
}
