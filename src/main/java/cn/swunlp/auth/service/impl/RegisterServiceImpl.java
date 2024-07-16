package cn.swunlp.auth.service.impl;

import cn.swunlp.auth.cache.AppAuthConfigCache;
import cn.swunlp.auth.entity.RoutePermission;
import cn.swunlp.auth.manager.RoutePermissionManager;
import cn.swunlp.auth.service.ApplicationService;
import cn.swunlp.auth.service.RegisterService;
import cn.swunlp.backend.base.security.constant.AppRegisterResult;
import cn.swunlp.backend.base.security.entity.ApplicationPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author TangXi
 * @since 2024/1/31
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterServiceImpl implements RegisterService {

    /**
     * 是否由平台介入管理应用权
     */
    private final AppAuthConfigCache appAuthConfigCache;

    private final ApplicationService applicationService;

    // 路由权限管理器
    private final RoutePermissionManager routePermissionManager;
    @Override
    public AppRegisterResult register(ApplicationPermission appPermission) {
        log.info("接收到注册请求:{}", appPermission.getApplicationName());
        //验证应用的名称编码合法性
        validateAppInfo(appPermission);
        log.info("应用信息验证通过");
        // 是否已经由平台接管
        Boolean flag = appAuthConfigCache.get(appPermission.getApplicationName());
        if (flag!= null && flag) {
            log.info("应用{}已经由平台接管", appPermission.getApplicationName());
            //return AppRegisterResult.PLATFORM_INTERVENE; // 平台介入管理应用权限
            return AppRegisterResult.SUCCESS;
        }
        //解析传递过来的应用权限
        return parseAppPermission(appPermission);
    }

    private void validateAppInfo(ApplicationPermission appPermission) {
        applicationService.checkAppInfo(
                appPermission.getApplicationName(),
                appPermission.getApplicationCode(),
                appPermission.getPrefix());
    }

    private AppRegisterResult parseAppPermission(ApplicationPermission appPermission) {
        log.info("开始解析并注册应用权限");
        String prefix = appPermission.getPrefix();
        List<RoutePermission> routePermissions = appPermission.getMethodPermissions()
                .stream()
                .map(mp -> RoutePermission.of(mp.getUri(),appPermission.getApplicationName(), appPermission.getAppAccessControl(), mp)).toList();
        //注册到路由权限管理器
        routePermissionManager.addRoutePermission(prefix, routePermissions);
        log.info("应用权限注册成功");
        return AppRegisterResult.SUCCESS;
    }

}
