package cn.swunlp.auth.controller;

import cn.swunlp.auth.service.RegisterService;
import cn.swunlp.backend.base.security.annotation.NotRequireAuth;
import cn.swunlp.backend.base.security.annotation.RequireAuth;
import cn.swunlp.backend.base.security.constant.AppRegisterResult;
import cn.swunlp.backend.base.security.entity.ApplicationPermission;
import cn.swunlp.backend.base.web.annotation.JsonResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注册控制器
 * @author TangXi
 * @since 2024/1/31
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "应用注册")
public class RegisterController {

    private final RegisterService registerService;

    /**
     * 接收注册请求
     */
    @PostMapping("/register")
    @NotRequireAuth(isPublic = true)
    public AppRegisterResult register(@RequestBody ApplicationPermission applicationPermission) {
        return registerService.register(applicationPermission);
    }
}
