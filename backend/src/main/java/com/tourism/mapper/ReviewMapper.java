package com.tourism.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tourism.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * 评论 Mapper
 */
@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    /**
     * 计算景点的平均评分
     */
    @Select("SELECT IFNULL(AVG(rating), 0) FROM review WHERE scenic_spot_id = #{scenicSpotId} AND status = 1")
    BigDecimal calcAvgRating(Long scenicSpotId);

    /**
     * 统计景点的通过评论数
     */
    @Select("SELECT COUNT(*) FROM review WHERE scenic_spot_id = #{scenicSpotId} AND status = 1")
    int countApproved(Long scenicSpotId);
}
