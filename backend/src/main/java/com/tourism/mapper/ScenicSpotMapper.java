package com.tourism.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tourism.entity.ScenicSpot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 景点 Mapper
 */
@Mapper
public interface ScenicSpotMapper extends BaseMapper<ScenicSpot> {

    /**
     * 更新景点平均评分和评论数
     */
    @Update("UPDATE scenic_spot SET avg_rating = #{avgRating}, review_count = #{reviewCount} WHERE id = #{id}")
    void updateRatingInfo(Long id, BigDecimal avgRating, int reviewCount);

    /**
     * 景点访问量 +1
     */
    @Update("UPDATE scenic_spot SET visit_count = visit_count + 1 WHERE id = #{id}")
    void incrementVisitCount(Long id);

    /**
     * 统计景点总数
     */
    @Select("SELECT COUNT(*) FROM scenic_spot WHERE status = 1")
    int countActive();

    /**
     * 按分类统计景点数量（分组查询，避免 N+1）
     */
    @Select("SELECT category_id AS categoryId, COUNT(*) AS count FROM scenic_spot WHERE status = 1 GROUP BY category_id")
    List<Map<String, Object>> countGroupByCategory();
}
