package cn.swunlp.auth.entity;

import cn.swunlp.backend.base.security.entity.AppAccessControl;
import cn.swunlp.backend.base.security.entity.MethodPermission;
import lombok.Data;

/**
 * 路由权限映射表
 * @author TangXi
 * @version 1.0
 */

@Data
public class RoutePermission {
    /**
     * 路由URI
     */
    private String uri;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 对应的应用访问控制
     */
    private AppAccessControl appAccessControl;

    /**
     * 对应的方法权限
     */
    private MethodPermission methodPermission;

    /**
     * 设置时间
     */
    private Long createTime;

    public static RoutePermission of(String uri, MethodPermission methodPermission) {
        RoutePermission routePermission = new RoutePermission();
        routePermission.setUri(uri);
        routePermission.setMethodPermission(methodPermission);
        return routePermission;
    }
    public static RoutePermission of(String uri, String appName,AppAccessControl appAccessControl,MethodPermission methodPermission) {
        RoutePermission routePermission = new RoutePermission();
        routePermission.setUri(uri);
        routePermission.setAppName(appName);
        routePermission.setAppAccessControl(appAccessControl);
        routePermission.setMethodPermission(methodPermission);
        routePermission.setCreateTime(System.currentTimeMillis());
        return routePermission;
    }

}
