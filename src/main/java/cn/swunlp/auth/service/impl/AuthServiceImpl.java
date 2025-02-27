package cn.swunlp.auth.service.impl;

import cn.swunlp.auth.cache.AppInfoCache;
import cn.swunlp.auth.cache.AuthResultCache;
import cn.swunlp.auth.client.UserServiceClient;
import cn.swunlp.auth.entity.ApplicationInfo;
import cn.swunlp.auth.entity.RoutePermission;
import cn.swunlp.auth.exception.RoutePermissionException;
import cn.swunlp.auth.manager.RoutePermissionManager;
import cn.swunlp.auth.service.AuthService;
import cn.swunlp.backend.base.base.exception.BusinessException;
import cn.swunlp.backend.base.security.constant.AppAuthenticate;
import cn.swunlp.backend.base.security.entity.AppAccessControl;
import cn.swunlp.backend.base.security.entity.AuthenticatedUserProfile;
import cn.swunlp.backend.base.security.entity.MethodPermission;
import cn.swunlp.backend.base.web.constant.AuthResult;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Arrays;

/**
 * @author TangXi
 * @since 2024/2/1
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AppInfoCache appInfoCache;

    private final AuthResultCache authResultCache;

    private final UserServiceClient userServiceClient;

    private final RoutePermissionManager routePermissionManager;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public AuthResult authFromGateway(String token, String uri) {
        // 匹配路由权限
        RoutePermission matched = routePermissionManager.match(uri);
        if(matched == null) {
            throw new RoutePermissionException("路由路径不存在");
        }
        // 不需要鉴权
        if(isNotRequired(matched)){
            return new AuthResult(1, "当前路径：" + uri + "，不需要鉴权");
        }
        if(token == null) {
            return new AuthResult(0, "用户未登录");
        }
        // 检查是否有缓存,同一用户的同一请求不需要重复鉴权，缓存时间为1分钟
        String id = DigestUtils.md5DigestAsHex((token + uri).getBytes());
        if(authResultCache.exist(id)){
            return authResultCache.get(id);
        }

        AuthenticatedUserProfile userProfile = userServiceClient.getUserProfile(token);
        logger.info("用户信息："+userProfile);
        if(userProfile == null){
            throw new BusinessException("用户凭证无效");
        }
        AuthResult authResult = checkUserPermission(userProfile, matched);
        // 缓存鉴权结果
        int expireTime = 60;
        authResultCache.set(id, authResult, expireTime);
        return authResult;
    }

    /**
     * 检查用户权限
     * @param userProfile 用户信息
     * @param matched 匹配的路由权限
     */
    private AuthResult checkUserPermission(AuthenticatedUserProfile userProfile, RoutePermission matched) {
        // 检查应用层面是否控制
        ApplicationInfo info = appInfoCache.get(matched.getAppName());
        if(!info.isAccessible()){
            return new AuthResult(0, "当前应用：" + matched.getAppName() + "，已被禁止访问");
        }
        AuthResult authResult = new AuthResult();
        // 具体的方法所需权限
        MethodPermission methodPermission = matched.getMethodPermission();
        // 需要同时满足权限与角色
        boolean isAccess;
        if(methodPermission.isRequireAll()) {
            isAccess = checkRole(methodPermission.getRoles(), userProfile) && checkPermission(methodPermission.getPermissions(), userProfile);
        } else {
            isAccess = checkRole(methodPermission.getRoles(), userProfile) || checkPermission(methodPermission.getPermissions(), userProfile);
        }
        // 提示信息
        authResult.setMsg("用户：" + userProfile.getUsername() + "，" + (isAccess ? "有" : "无") + "权限访问");
        if(isAccess) {
            authResult.setCode(1);
            authResult.setUsername(userProfile.getUsername());
            authResult.setNickname(userProfile.getNickname());
            // 获取访问权限信息
            AppAccessControl appAccessControl = matched.getAppAccessControl();
            if(appAccessControl != null && appAccessControl.isEnable()) {
                authResult.setAccessHeader(appAccessControl.getHeader());
                authResult.setAccessValue(appAccessControl.getValidValue());
            }
        } else {
            authResult.setCode(0);
        }
        return authResult;
    }

    /**
     * 是否不需要鉴权,此时不检测token
     * 当前版本检测规则：
     *  1. 路由权限中没有任何权限信息
     *  2. 路由权限中没有任何角色信息
     * @param routePermission 路由权限
     */
    private boolean isNotRequired(RoutePermission routePermission) {
        MethodPermission methodPermission = routePermission.getMethodPermission();
        return methodPermission.getPermissions() == null && methodPermission.getRoles() == null;
    }

    /**
     * 检查用户权限，使用白名单模式，当出现NOT_REQUIRED时，直接返回true
     */
    private boolean checkPermission(String[] permissions, AuthenticatedUserProfile userProfile) {
        if(permissions == null){
            return false;
        }
        for (String permission : permissions) {
            if(AppAuthenticate.NOT_REQUIRED.equals(permission)){
                return true;
            }
            boolean contains = Arrays.asList(userProfile.getPermissions()).contains(permission);
            if(contains) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查用户角色，使用白名单模式,当出现NOT_REQUIRED时，直接返回true
     */
    private boolean checkRole(String[] roles, AuthenticatedUserProfile userProfile) {
        if(roles == null || userProfile.getRoles() == null){
            return false;
        }
        for (String role : roles) {
            if(AppAuthenticate.NOT_REQUIRED.equals(role)){
                return true;
            }
            boolean contains = Arrays.asList(userProfile.getRoles()).contains(role);
            if(contains) {
                return true;
            }
        }
        return false;
    }
}
