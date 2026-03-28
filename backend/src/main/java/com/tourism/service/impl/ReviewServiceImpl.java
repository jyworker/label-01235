package com.tourism.service.impl;

import com.tourism.config.StpUserUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tourism.common.BusinessException;
import com.tourism.common.PageResult;
import com.tourism.dto.ReviewDTO;
import com.tourism.entity.Review;
import com.tourism.entity.ReviewImage;
import com.tourism.entity.ScenicSpot;
import com.tourism.entity.User;
import com.tourism.mapper.ReviewImageMapper;
import com.tourism.mapper.ReviewMapper;
import com.tourism.mapper.ScenicSpotMapper;
import com.tourism.mapper.UserMapper;
import com.tourism.service.FileService;
import com.tourism.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 */
@Slf4j
@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    @Autowired
    private ReviewImageMapper reviewImageMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ScenicSpotMapper scenicSpotMapper;

    @Autowired
    private FileService fileService;

    @Override
    public PageResult<Map<String, Object>> listReviews(Long scenicSpotId, Integer pageNum, Integer pageSize) {
        Page<Review> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getScenicSpotId, scenicSpotId)
                .eq(Review::getStatus, 1)
                .orderByDesc(Review::getCreateTime);

        Page<Review> result = this.page(page, wrapper);
        List<Map<String, Object>> records = batchEnrichReviews(result.getRecords());

        return PageResult.of(records, result.getTotal(), pageNum, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addReview(ReviewDTO dto, List<MultipartFile> images) {
        Long userId = StpUserUtil.getLoginIdAsLong();

        // 检查景点是否存在
        ScenicSpot spot = scenicSpotMapper.selectById(dto.getScenicSpotId());
        if (spot == null || spot.getStatus() != 1) {
            throw new BusinessException("景点不存在或已下架");
        }

        // 创建评论
        Review review = new Review();
        review.setUserId(userId);
        review.setScenicSpotId(dto.getScenicSpotId());
        review.setContent(dto.getContent());
        review.setRating(dto.getRating());
        review.setStatus(0); // 待审核
        this.save(review);

        // 保存评论图片
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                String imageUrl = fileService.upload(images.get(i));
                ReviewImage reviewImage = new ReviewImage();
                reviewImage.setReviewId(review.getId());
                reviewImage.setImageUrl(imageUrl);
                reviewImage.setSortOrder(i);
                reviewImageMapper.insert(reviewImage);
            }
        }

        log.info("用户发表评论: userId={}, scenicSpotId={}, reviewId={}", userId, dto.getScenicSpotId(), review.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMyReview(Long id) {
        Long userId = StpUserUtil.getLoginIdAsLong();
        Review review = this.getById(id);
        if (review == null) {
            throw new BusinessException("评论不存在");
        }
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException("只能删除自己的评论");
        }

        // 删除评论图片
        reviewImageMapper.delete(new LambdaQueryWrapper<ReviewImage>()
                .eq(ReviewImage::getReviewId, id));

        this.removeById(id);

        // 更新景点评分
        updateScenicSpotRating(review.getScenicSpotId());

        log.info("用户删除评论: userId={}, reviewId={}", userId, id);
    }

    @Override
    public PageResult<Map<String, Object>> myReviews(Integer pageNum, Integer pageSize) {
        Long userId = StpUserUtil.getLoginIdAsLong();
        Page<Review> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getUserId, userId)
                .orderByDesc(Review::getCreateTime);

        Page<Review> result = this.page(page, wrapper);
        List<Review> reviews = result.getRecords();

        // 批量enrichReview（用户+图片）
        List<Map<String, Object>> records = batchEnrichReviews(reviews);

        // 批量查询景点信息，避免 N+1
        List<Long> spotIds = reviews.stream()
                .map(Review::getScenicSpotId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, ScenicSpot> spotMap = new HashMap<>();
        if (!spotIds.isEmpty()) {
            scenicSpotMapper.selectBatchIds(spotIds).forEach(
                    spot -> spotMap.put(spot.getId(), spot));
        }

        for (int i = 0; i < reviews.size(); i++) {
            Review review = reviews.get(i);
            ScenicSpot spot = spotMap.get(review.getScenicSpotId());
            if (spot != null) {
                records.get(i).put("scenicSpotName", spot.getName());
                records.get(i).put("scenicSpotCover", spot.getCoverImage());
            }
        }

        return PageResult.of(records, result.getTotal(), pageNum, pageSize);
    }

    @Override
    public PageResult<Map<String, Object>> adminListReviews(Integer pageNum, Integer pageSize, Integer status, String keyword) {
        Page<Review> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Review::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            List<Long> matchedUserIds = userMapper.selectList(
                    new LambdaQueryWrapper<User>()
                            .and(w -> w.like(User::getUsername, keyword)
                                    .or().like(User::getNickname, keyword)
                                    .or().like(User::getPhone, keyword))
            ).stream().map(User::getId).collect(Collectors.toList());

            wrapper.and(w -> {
                w.like(Review::getContent, keyword);
                if (!matchedUserIds.isEmpty()) {
                    w.or().in(Review::getUserId, matchedUserIds);
                }
            });
        }
        wrapper.orderByDesc(Review::getCreateTime);

        Page<Review> result = this.page(page, wrapper);
        List<Review> reviews = result.getRecords();

        // 批量enrichReview（用户+图片）
        List<Map<String, Object>> records = batchEnrichReviews(reviews);

        // 批量查询景点信息，避免 N+1
        List<Long> spotIds = reviews.stream()
                .map(Review::getScenicSpotId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, ScenicSpot> spotMap = new HashMap<>();
        if (!spotIds.isEmpty()) {
            scenicSpotMapper.selectBatchIds(spotIds).forEach(
                    spot -> spotMap.put(spot.getId(), spot));
        }

        for (int i = 0; i < reviews.size(); i++) {
            Review review = reviews.get(i);
            ScenicSpot spot = spotMap.get(review.getScenicSpotId());
            if (spot != null) {
                records.get(i).put("scenicSpotName", spot.getName());
            }
        }

        return PageResult.of(records, result.getTotal(), pageNum, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditReview(Long id, Integer status) {
        Review review = this.getById(id);
        if (review == null) {
            throw new BusinessException("评论不存在");
        }

        Review update = new Review();
        update.setId(id);
        update.setStatus(status);
        update.setVersion(review.getVersion());
        boolean success = this.updateById(update);
        if (!success) {
            throw new BusinessException("评论信息已被其他用户修改，请刷新后重试");
        }

        // 审核通过后更新景点评分
        if (status == 1) {
            updateScenicSpotRating(review.getScenicSpotId());
        }

        log.info("审核评论: reviewId={}, status={}", id, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminDeleteReview(Long id) {
        Review review = this.getById(id);
        if (review == null) {
            throw new BusinessException("评论不存在");
        }

        // 删除评论图片
        reviewImageMapper.delete(new LambdaQueryWrapper<ReviewImage>()
                .eq(ReviewImage::getReviewId, id));

        this.removeById(id);

        // 更新景点评分
        updateScenicSpotRating(review.getScenicSpotId());

        log.info("管理员删除评论: reviewId={}", id);
    }

    /**
     * 批量丰富评论信息（用户信息 + 图片），避免 N+1 查询
     */
    private List<Map<String, Object>> batchEnrichReviews(List<Review> reviews) {
        if (reviews.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量查询用户信息
        List<Long> userIds = reviews.stream()
                .map(Review::getUserId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            userMapper.selectBatchIds(userIds).forEach(
                    user -> userMap.put(user.getId(), user));
        }

        // 批量查询评论图片
        List<Long> reviewIds = reviews.stream()
                .map(Review::getId)
                .collect(Collectors.toList());
        Map<Long, List<String>> imageMap = new HashMap<>();
        if (!reviewIds.isEmpty()) {
            List<ReviewImage> allImages = reviewImageMapper.selectList(
                    new LambdaQueryWrapper<ReviewImage>()
                            .in(ReviewImage::getReviewId, reviewIds)
                            .orderByAsc(ReviewImage::getSortOrder));
            imageMap = allImages.stream()
                    .collect(Collectors.groupingBy(
                            ReviewImage::getReviewId,
                            Collectors.mapping(ReviewImage::getImageUrl, Collectors.toList())));
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Review review : reviews) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", review.getId());
            map.put("userId", review.getUserId());
            map.put("scenicSpotId", review.getScenicSpotId());
            map.put("content", review.getContent());
            map.put("rating", review.getRating());
            map.put("status", review.getStatus());
            map.put("createTime", review.getCreateTime());

            // 用户信息
            User user = userMap.get(review.getUserId());
            if (user != null) {
                map.put("username", user.getUsername());
                map.put("nickname", user.getNickname());
                map.put("avatar", user.getAvatar());
            }

            // 评论图片
            map.put("images", imageMap.getOrDefault(review.getId(), new ArrayList<>()));

            result.add(map);
        }
        return result;
    }

    /**
     * 丰富评论信息（用户信息 + 图片）
     */
    private Map<String, Object> enrichReview(Review review) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", review.getId());
        map.put("userId", review.getUserId());
        map.put("scenicSpotId", review.getScenicSpotId());
        map.put("content", review.getContent());
        map.put("rating", review.getRating());
        map.put("status", review.getStatus());
        map.put("createTime", review.getCreateTime());

        // 用户信息
        User user = userMapper.selectById(review.getUserId());
        if (user != null) {
            map.put("username", user.getUsername());
            map.put("nickname", user.getNickname());
            map.put("avatar", user.getAvatar());
        }

        // 评论图片
        List<ReviewImage> images = reviewImageMapper.selectList(
                new LambdaQueryWrapper<ReviewImage>()
                        .eq(ReviewImage::getReviewId, review.getId())
                        .orderByAsc(ReviewImage::getSortOrder));
        map.put("images", images.stream()
                .map(ReviewImage::getImageUrl)
                .collect(Collectors.toList()));

        return map;
    }

    /**
     * 更新景点平均评分和评论数
     */
    private void updateScenicSpotRating(Long scenicSpotId) {
        BigDecimal avgRating = baseMapper.calcAvgRating(scenicSpotId);
        int reviewCount = baseMapper.countApproved(scenicSpotId);
        scenicSpotMapper.updateRatingInfo(scenicSpotId, avgRating, reviewCount);
    }
}
