package com.tourism.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tourism.common.PageResult;
import com.tourism.dto.ReviewDTO;
import com.tourism.entity.Review;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 评论服务接口
 */
public interface ReviewService extends IService<Review> {

    /**
     * 获取景点评论列表（只显示status=1的，含用户信息和图片）
     */
    PageResult<Map<String, Object>> listReviews(Long scenicSpotId, Integer pageNum, Integer pageSize);

    /**
     * 发表评论（含图片上传）
     */
    void addReview(ReviewDTO dto, List<MultipartFile> images);

    /**
     * 删除我的评论
     */
    void deleteMyReview(Long id);

    /**
     * 我的评论列表
     */
    PageResult<Map<String, Object>> myReviews(Integer pageNum, Integer pageSize);

    /**
     * 管理端：评论列表（含所有状态）
     */
    PageResult<Map<String, Object>> adminListReviews(Integer pageNum, Integer pageSize, Integer status, String keyword);

    /**
     * 管理端：审核评论（通过/拒绝）
     */
    void auditReview(Long id, Integer status);

    /**
     * 管理端：删除评论
     */
    void adminDeleteReview(Long id);
}
