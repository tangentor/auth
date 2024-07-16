package cn.swunlp.auth.controller;

import cn.swunlp.auth.entity.*;
import cn.swunlp.auth.service.ApplicationService;
import cn.swunlp.backend.base.security.annotation.RequireAuth;
import cn.swunlp.backend.base.web.annotation.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author TangXi
 * @since 2024/2/5
 */

@JsonResult
@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Tag(name = "应用管理")
@RequireAuth(roles = {"app_admin"})
public class ApplicationControl {

    private final ApplicationService applicationService;

    @PostMapping("apply")
    @Operation(summary = "应用申请")
    public boolean apply(@RequestBody ApplicationInfoApplyDTO applicationInfo) {
        return applicationService.apply(applicationInfo);
    }

    @GetMapping("list")
    @Operation(summary = "获取应用信息")
    public List<ApplicationInfoVO> listByUser() {
        return applicationService.listByUser();
    }

    @GetMapping("detail")
    @Operation(summary = "获取应用详情")
    public ApplicationInfoVO detail(String id) {
        return applicationService.detail(id);
    }

    @PostMapping("edit")
    @Operation(summary = "编辑应用信息")
    public boolean edit(@RequestBody ApplicationInfoDTO applicationInfo) {
        return applicationService.edit(applicationInfo);
    }

    @GetMapping("delete")
    @Operation(summary = "删除应用")
    public boolean delete(String id) {
        return applicationService.delete(id);
    }

    /**
     * 更新应用路由权限信息
     */
    @PostMapping("updateAuth")
    @Operation(summary = "更新应用路由权限信息")
    public boolean updateAuth(@RequestBody ApplicationAuthInfoDTO authInfo) {
        return applicationService.updateAuth(authInfo);
    }

}
