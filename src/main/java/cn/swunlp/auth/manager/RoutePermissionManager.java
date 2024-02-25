package cn.swunlp.auth.manager;

import cn.swunlp.auth.cache.RoutePermissionCache;
import cn.swunlp.auth.entity.RoutePermission;
import cn.swunlp.auth.exception.RoutePermissionException;
import cn.swunlp.auth.util.UrlUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 路由权限管理器
 * @author TangXi
 * @version 1.0
 */
@Component
public class RoutePermissionManager {

    /**
     * <p><b>路由权限映射表</b></p>
     * Key为应用前缀 Value为某个应用的路由权限列表
     */
    private final Map<String, List<RoutePermission>> routePermissionMap;

    private final RoutePermissionCache routePermissionCache;

    /**
     * 路由权限过期时间
     */
    private final int expireTime = 60;


    public RoutePermissionManager(RoutePermissionCache routePermissionCache) {
        this.routePermissionCache = routePermissionCache;
        this.routePermissionMap = new HashMap<>();
        initData();
    }

    private void initData() {
        //获取所有
        Map<String, List<RoutePermission>> groupResult = routePermissionCache.lGetAll(null).stream().collect(Collectors.groupingBy(RoutePermission::getUri));
        //加载数据到各个应用副本
        this.routePermissionMap.putAll(groupResult);
    }

    /**
     * 添加路由权限
     * @param prefix 应用前缀
     * @param routePermissionList 路由权限列表
     */
    public void addRoutePermission(String prefix, List<RoutePermission> routePermissionList) {
        routePermissionMap.put(prefix, routePermissionList);
        //删除之前的
        routePermissionCache.remove(prefix);
        routePermissionCache.lSet(prefix, routePermissionList, expireTime);
    }

    /**
     * 匹配路由权限
     */
    public RoutePermission match(String uri) {
        String prefix = "/"+UrlUtils.getPrefix(uri);
        List<RoutePermission> routePermissionList = routePermissionMap.get(prefix);
        if (routePermissionList == null || routePermissionList.isEmpty()) {
            throw new RoutePermissionException("未匹配到对应的路由权限表");
        }
        checkExpire(routePermissionList);
        for (RoutePermission routePermission : routePermissionList) {
            if (matchWithSegment(prefix+routePermission.getUri(), uri)) {
                return routePermission;
            }
        }
        return null;
    }

    /**
     * 检查路由权限是否过期
     * 这里的所有路由权限过期时间都是一致的，所以只需要检查一个即可
     */
    private void checkExpire(List<RoutePermission> routePermissionList) {
        Long createTime = routePermissionList.get(0).getCreateTime();
        String key = routePermissionList.get(0).getUri();
        // 存的时间戳是毫秒，所以这里要乘以1000
        if (System.currentTimeMillis() - createTime > expireTime * 1000) {
            routePermissionMap.remove(key);
            // 可能被其他线程删除了
            if(routePermissionCache.exist(key)){
                routePermissionCache.remove(key);
            }
            throw new RoutePermissionException("路由权限已过期");
        }
    }

    private boolean matchWithSegment(String targetUri, String requestUri) {
        //去掉所有的参数
        requestUri = requestUri.replaceAll("\\?.*", "");
        // @PathVariable的情况需要对其进一步处理
        if(!targetUri.contains("{")){
            return targetUri.equals(requestUri);
        }
        //每项依次比较
        String[] targetUriSegments = targetUri.split("/");
        String[] requestUriSegments = requestUri.split("/");
        if (targetUriSegments.length != requestUriSegments.length) {
            return false;
        }
        for (int i = 1; i < targetUriSegments.length; i++) {
            String targetUriSegment = targetUriSegments[i];
            String requestUriSegment = requestUriSegments[i];
            if (targetUriSegment.startsWith("{") && targetUriSegment.endsWith("}")) {
                continue;
            }
            if (!targetUriSegment.equals(requestUriSegment)) {
                return false;
            }
        }
        return true;
    }


    public List<RoutePermission> getAppRoutePermission(String prefix) {
        return routePermissionCache.lGetAll(prefix);
    }

}
