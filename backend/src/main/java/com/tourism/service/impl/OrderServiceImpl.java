package com.tourism.service.impl;

import com.tourism.config.StpUserUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tourism.common.BusinessException;
import com.tourism.common.PageResult;
import com.tourism.dto.OrderDTO;
import com.tourism.entity.ScenicSpot;
import com.tourism.entity.TicketOrder;
import com.tourism.entity.User;
import com.tourism.mapper.OrderMapper;
import com.tourism.mapper.ScenicSpotMapper;
import com.tourism.mapper.UserMapper;
import com.tourism.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, TicketOrder> implements OrderService {

    @Autowired
    private ScenicSpotMapper scenicSpotMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TicketOrder createOrder(OrderDTO dto) {
        Long userId = StpUserUtil.getLoginIdAsLong();

        // 检查景点是否存在
        ScenicSpot spot = scenicSpotMapper.selectById(dto.getScenicSpotId());
        if (spot == null || spot.getStatus() != 1) {
            throw new BusinessException("景点不存在或已下架");
        }

        // 生成订单号: ORD + yyyyMMddHHmmss + 4位随机数
        String orderNo = "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", new Random().nextInt(10000));

        // 计算总价
        BigDecimal totalAmount = spot.getTicketPrice().multiply(new BigDecimal(dto.getQuantity()));

        TicketOrder order = new TicketOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setScenicSpotId(dto.getScenicSpotId());
        order.setQuantity(dto.getQuantity());
        order.setTotalAmount(totalAmount);
        order.setContactName(dto.getContactName());
        order.setContactPhone(dto.getContactPhone());
        // 0 元订单自动置为已支付，避免进入支付流程
        if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            order.setStatus(1); // 已支付
            order.setPayTime(LocalDateTime.now());
        } else {
            order.setStatus(0); // 待支付
        }

        if (StringUtils.hasText(dto.getVisitDate())) {
            order.setVisitDate(LocalDate.parse(dto.getVisitDate()));
        }

        this.save(order);

        log.info("创建订单: orderNo={}, userId={}, scenicSpotId={}, totalAmount={}, status={}",
                orderNo, userId, dto.getScenicSpotId(), totalAmount, order.getStatus());
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long orderId) {
        Long userId = StpUserUtil.getLoginIdAsLong();
        TicketOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不正确，无法支付");
        }

        // 使用MyBatis-Plus的更新方法，添加状态检查条件，防止竞态条件
        TicketOrder update = new TicketOrder();
        update.setId(orderId);
        update.setStatus(1); // 已支付
        update.setPayTime(LocalDateTime.now());
        
        // 创建更新条件
        LambdaQueryWrapper<TicketOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TicketOrder::getId, orderId)
               .eq(TicketOrder::getStatus, 0); // 只有状态为0的订单才能更新
        
        // 执行更新
        int affectedRows = this.baseMapper.update(update, wrapper);
        
        if (affectedRows == 0) {
            // 没有更新到任何记录，说明订单状态已经被其他线程修改
            throw new BusinessException("订单状态已变更，无法支付");
        }

        log.info("订单支付成功: orderNo={}, orderId={}", order.getOrderNo(), orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId) {
        Long userId = StpUserUtil.getLoginIdAsLong();
        TicketOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("仅待支付订单可取消");
        }

        // 使用MyBatis-Plus的更新方法，添加状态检查条件，防止竞态条件
        TicketOrder update = new TicketOrder();
        update.setId(orderId);
        update.setStatus(3); // 已取消
        
        // 创建更新条件
        LambdaQueryWrapper<TicketOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TicketOrder::getId, orderId)
               .eq(TicketOrder::getStatus, 0); // 只有状态为0的订单才能更新
        
        // 执行更新
        int affectedRows = this.baseMapper.update(update, wrapper);
        
        if (affectedRows == 0) {
            // 没有更新到任何记录，说明订单状态已经被其他线程修改
            throw new BusinessException("订单状态已变更，无法取消");
        }

        log.info("订单取消: orderNo={}, orderId={}", order.getOrderNo(), orderId);
    }

    @Override
    public PageResult<Map<String, Object>> myOrders(Integer pageNum, Integer pageSize, Integer status) {
        Long userId = StpUserUtil.getLoginIdAsLong();
        Page<TicketOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TicketOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TicketOrder::getUserId, userId);
        if (status != null) {
            wrapper.eq(TicketOrder::getStatus, status);
        }
        wrapper.orderByDesc(TicketOrder::getCreateTime);

        Page<TicketOrder> result = this.page(page, wrapper);
        List<Map<String, Object>> records = batchEnrichOrders(result.getRecords());

        return PageResult.of(records, result.getTotal(), pageNum, pageSize);
    }

    @Override
    public Map<String, Object> getOrderDetail(Long orderId) {
        Long userId = StpUserUtil.getLoginIdAsLong();
        TicketOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权查看此订单");
        }
        return enrichOrder(order);
    }

    @Override
    public Map<String, Object> adminGetOrderDetail(Long orderId) {
        TicketOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        Map<String, Object> map = enrichOrder(order);
        // 附加用户信息
        User user = userMapper.selectById(order.getUserId());
        if (user != null) {
            map.put("username", user.getUsername());
            map.put("nickname", user.getNickname());
            map.put("phone", user.getPhone());
        }
        return map;
    }

    @Override
    public PageResult<Map<String, Object>> adminListOrders(Integer pageNum, Integer pageSize, Integer status, String orderNo, String keyword) {
        Page<TicketOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TicketOrder> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(TicketOrder::getStatus, status);
        }
        if (StringUtils.hasText(orderNo)) {
            wrapper.like(TicketOrder::getOrderNo, orderNo);
        }
        if (StringUtils.hasText(keyword)) {
            List<Long> userIds = userMapper.selectList(
                    new LambdaQueryWrapper<User>()
                            .and(w -> w.like(User::getUsername, keyword).or().like(User::getNickname, keyword).or().like(User::getPhone, keyword))
            ).stream().map(User::getId).collect(Collectors.toList());
            List<Long> scenicIds = scenicSpotMapper.selectList(
                    new LambdaQueryWrapper<ScenicSpot>().like(ScenicSpot::getName, keyword)
            ).stream().map(ScenicSpot::getId).collect(Collectors.toList());
            wrapper.and(w -> {
                w.like(TicketOrder::getOrderNo, keyword);
                if (!userIds.isEmpty()) w.or().in(TicketOrder::getUserId, userIds);
                if (!scenicIds.isEmpty()) w.or().in(TicketOrder::getScenicSpotId, scenicIds);
            });
        }
        wrapper.orderByDesc(TicketOrder::getCreateTime);

        Page<TicketOrder> result = this.page(page, wrapper);
        List<TicketOrder> orders = result.getRecords();

        // 批量enrichOrder（景点信息）
        List<Map<String, Object>> records = batchEnrichOrders(orders);

        // 批量查询用户信息，避免 N+1
        List<Long> userIds = orders.stream()
                .map(TicketOrder::getUserId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            userMapper.selectBatchIds(userIds).forEach(
                    user -> userMap.put(user.getId(), user));
        }

        for (int i = 0; i < orders.size(); i++) {
            TicketOrder order = orders.get(i);
            User user = userMap.get(order.getUserId());
            if (user != null) {
                records.get(i).put("username", user.getUsername());
                records.get(i).put("nickname", user.getNickname());
            }
        }

        return PageResult.of(records, result.getTotal(), pageNum, pageSize);
    }

    @Override
    public void updateOrderStatus(Long orderId, Integer status) {
        TicketOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        TicketOrder update = new TicketOrder();
        update.setId(orderId);
        update.setStatus(status);
        if (status == 1) {
            update.setPayTime(LocalDateTime.now());
        }
        this.updateById(update);

        log.info("管理员更新订单状态: orderId={}, status={}", orderId, status);
    }

    /**
     * 批量丰富订单信息（含景点信息），避免 N+1 查询
     */
    private List<Map<String, Object>> batchEnrichOrders(List<TicketOrder> orders) {
        if (orders.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量查询景点信息
        List<Long> spotIds = orders.stream()
                .map(TicketOrder::getScenicSpotId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, ScenicSpot> spotMap = new HashMap<>();
        if (!spotIds.isEmpty()) {
            scenicSpotMapper.selectBatchIds(spotIds).forEach(
                    spot -> spotMap.put(spot.getId(), spot));
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (TicketOrder order : orders) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("orderNo", order.getOrderNo());
            map.put("userId", order.getUserId());
            map.put("scenicSpotId", order.getScenicSpotId());
            map.put("quantity", order.getQuantity());
            map.put("totalAmount", order.getTotalAmount());
            map.put("visitDate", order.getVisitDate());
            map.put("contactName", order.getContactName());
            map.put("contactPhone", order.getContactPhone());
            map.put("status", order.getStatus());
            map.put("payTime", order.getPayTime());
            map.put("createTime", order.getCreateTime());

            // 景点信息
            ScenicSpot spot = spotMap.get(order.getScenicSpotId());
            if (spot != null) {
                map.put("scenicSpotName", spot.getName());
                map.put("scenicSpotCover", spot.getCoverImage());
                map.put("ticketPrice", spot.getTicketPrice());
            }

            result.add(map);
        }
        return result;
    }

    /**
     * 丰富订单信息（含景点信息）
     */
    private Map<String, Object> enrichOrder(TicketOrder order) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", order.getId());
        map.put("orderNo", order.getOrderNo());
        map.put("userId", order.getUserId());
        map.put("scenicSpotId", order.getScenicSpotId());
        map.put("quantity", order.getQuantity());
        map.put("totalAmount", order.getTotalAmount());
        map.put("visitDate", order.getVisitDate());
        map.put("contactName", order.getContactName());
        map.put("contactPhone", order.getContactPhone());
        map.put("status", order.getStatus());
        map.put("payTime", order.getPayTime());
        map.put("createTime", order.getCreateTime());

        // 景点信息
        ScenicSpot spot = scenicSpotMapper.selectById(order.getScenicSpotId());
        if (spot != null) {
            map.put("scenicSpotName", spot.getName());
            map.put("scenicSpotCover", spot.getCoverImage());
            map.put("ticketPrice", spot.getTicketPrice());
        }

        return map;
    }
}
