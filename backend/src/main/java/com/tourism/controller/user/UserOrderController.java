package com.tourism.controller.user;

import com.tourism.common.PageResult;
import com.tourism.common.Result;
import com.tourism.dto.OrderDTO;
import com.tourism.entity.TicketOrder;
import com.tourism.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 用户端订单控制器
 */
@Api(tags = "用户端-订单接口")
@RestController
@RequestMapping("/api/order")
public class UserOrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("创建订单")
    @PostMapping("/create")
    public Result<TicketOrder> create(@Valid @RequestBody OrderDTO dto) {
        TicketOrder order = orderService.createOrder(dto);
        if (order.getStatus() != null
                && order.getStatus() == 1
                && order.getTotalAmount() != null
                && order.getTotalAmount().compareTo(java.math.BigDecimal.ZERO) == 0) {
            return Result.success("订单创建成功（0元景点已自动支付）", order);
        }
        return Result.success("订单创建成功", order);
    }

    @ApiOperation("模拟支付")
    @PostMapping("/{id}/pay")
    public Result<?> pay(@PathVariable Long id) {
        orderService.payOrder(id);
        return Result.success("支付成功", null);
    }

    @ApiOperation("取消订单")
    @PostMapping("/{id}/cancel")
    public Result<?> cancel(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return Result.success("订单已取消", null);
    }

    @ApiOperation("我的订单列表")
    @GetMapping("/my")
    public Result<PageResult<Map<String, Object>>> myOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        PageResult<Map<String, Object>> result = orderService.myOrders(pageNum, pageSize, status);
        return Result.success(result);
    }

    @ApiOperation("订单详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        Map<String, Object> detail = orderService.getOrderDetail(id);
        return Result.success(detail);
    }
}
