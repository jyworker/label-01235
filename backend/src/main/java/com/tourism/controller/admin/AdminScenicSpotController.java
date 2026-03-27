package com.tourism.controller.admin;

import com.tourism.common.PageResult;
import com.tourism.common.Result;
import com.tourism.dto.ScenicSpotDTO;
import com.tourism.dto.ScenicSpotQueryDTO;
import com.tourism.service.ScenicSpotService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 管理端景点控制器
 */
@Api(tags = "管理端-景点管理")
@RestController
@RequestMapping("/api/admin/scenic")
public class AdminScenicSpotController {

    @Autowired
    private ScenicSpotService scenicSpotService;

    @ApiOperation("景点列表")
    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> list(ScenicSpotQueryDTO queryDTO) {
        PageResult<Map<String, Object>> result = scenicSpotService.adminListScenicSpots(queryDTO);
        return Result.success(result);
    }

    @ApiOperation("景点详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        Map<String, Object> detail = scenicSpotService.getScenicSpotDetailForAdmin(id);
        return Result.success(detail);
    }

    @ApiOperation("新增景点")
    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody ScenicSpotDTO dto) {
        scenicSpotService.addScenicSpot(dto);
        return Result.success("新增成功", null);
    }

    @ApiOperation("编辑景点")
    @PutMapping("/update")
    public Result<?> update(@Valid @RequestBody ScenicSpotDTO dto) {
        scenicSpotService.updateScenicSpot(dto);
        return Result.success("更新成功", null);
    }

    @ApiOperation("删除景点")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        scenicSpotService.deleteScenicSpot(id);
        return Result.success("删除成功", null);
    }

    @ApiOperation("上架/下架景点")
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> body) {
        Integer status = body != null ? body.get("status") : null;
        if (status == null) {
            throw new com.tourism.common.BusinessException("状态不能为空");
        }
        scenicSpotService.updateScenicSpotStatus(id, status);
        return Result.success("操作成功", null);
    }
}
