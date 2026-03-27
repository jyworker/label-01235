package com.tourism.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tourism.entity.TicketOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单 Mapper
 */
@Mapper
public interface OrderMapper extends BaseMapper<TicketOrder> {

    /**
     * 统计总收入（已支付+已使用的订单）
     */
    @Select("SELECT IFNULL(SUM(total_amount), 0) FROM ticket_order WHERE status IN (1, 2)")
    BigDecimal sumTotalRevenue();

    /**
     * 统计近7天订单趋势
     */
    @Select("SELECT DATE(create_time) AS date, COUNT(*) AS count, IFNULL(SUM(total_amount), 0) AS amount " +
            "FROM ticket_order WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
            "GROUP BY DATE(create_time) ORDER BY date")
    List<Map<String, Object>> orderTrend7Days();

    /**
     * 统计今日订单数
     */
    @Select("SELECT COUNT(*) FROM ticket_order WHERE DATE(create_time) = CURDATE()")
    int countToday();
}
