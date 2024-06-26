package cn.swunlp.auth.client;

import cn.swunlp.backend.base.security.entity.AuthenticatedUserProfile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author TangXi
 * @since 2024/2/1
 */

@FeignClient(name = "userService")
public interface UserServiceClient {

    /**
     * 获取用户权限信息
     * @param token token
     * @return 用户权限信息
     */
    @GetMapping("/user/auth/loadAuthenticatedUserprofile")
    AuthenticatedUserProfile getUserProfile(@RequestParam("token") String token);
}
