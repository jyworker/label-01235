package com.tourism.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tourism.common.PageResult;
import com.tourism.dto.OrderDTO;
import com.tourism.entity.TicketOrder;

import java.util.Map;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<TicketOrder> {

    /**
     * 创建订单
     */
    TicketOrder createOrder(OrderDTO dto);

    /**
     * 模拟支付
     */
    void payOrder(Long orderId);

    /**
     * 取消订单
     */
    void cancelOrder(Long orderId);

    /**
     * 我的订单列表
     */
    PageResult<Map<String, Object>> myOrders(Integer pageNum, Integer pageSize, Integer status);

    /**
     * 订单详情
     */
    Map<String, Object> getOrderDetail(Long orderId);

    /**
     * 管理端：订单列表（keyword 可搜索订单号/用户名/昵称/景点名）
     */
    PageResult<Map<String, Object>> adminListOrders(Integer pageNum, Integer pageSize, Integer status, String orderNo, String keyword);

    /**
     * 管理端：订单详情（无需验证用户归属）
     */
    Map<String, Object> adminGetOrderDetail(Long orderId);

    /**
     * 管理端：更新订单状态
     */
    void updateOrderStatus(Long orderId, Integer status);
}
