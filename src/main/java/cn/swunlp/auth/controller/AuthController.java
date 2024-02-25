package cn.swunlp.auth.controller;

import cn.swunlp.auth.service.AuthService;
import cn.swunlp.backend.base.web.annotation.JsonResult;
import cn.swunlp.backend.base.web.constant.AuthResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 功能描述：
 *      鉴权接口
 * @author TangXi
 */

@RestController
//@JsonResult
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 辅助网关鉴权
     * @param token token
     * @return 鉴权结果
     */
    @GetMapping("/gateway")
    public AuthResult fromGateway(String token,String uri) {
        return authService.authFromGateway(token,uri);
    }

}
