package cn.swunlp.auth.controller;

import cn.swunlp.auth.service.RegisterService;
import cn.swunlp.backend.base.security.constant.AppRegisterResult;
import cn.swunlp.backend.base.security.entity.ApplicationPermission;
import cn.swunlp.backend.base.web.annotation.JsonResult;
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
public class RegisterController {

    private final RegisterService registerService;

    /**
     * 接收注册请求
     */
    @PostMapping("/register")
    public AppRegisterResult register(@RequestBody ApplicationPermission applicationPermission) {
        return registerService.register(applicationPermission);
    }
}
