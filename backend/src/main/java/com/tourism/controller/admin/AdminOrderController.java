package com.tourism.controller.admin;

import com.tourism.common.PageResult;
import com.tourism.common.Result;
import com.tourism.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理端订单控制器
 */
@Api(tags = "管理端-订单管理")
@RestController
@RequestMapping("/api/admin/order")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("订单列表")
    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String keyword) {
        PageResult<Map<String, Object>> result = orderService.adminListOrders(pageNum, pageSize, status, orderNo, keyword);
        return Result.success(result);
    }

    @ApiOperation("订单详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        Map<String, Object> detail = orderService.adminGetOrderDetail(id);
        return Result.success(detail);
    }

    @ApiOperation("更新订单状态")
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> body) {
        Integer status = body != null ? body.get("status") : null;
        if (status == null) {
            throw new com.tourism.common.BusinessException("状态不能为空");
        }
        orderService.updateOrderStatus(id, status);
        return Result.success("操作成功", null);
    }
}
