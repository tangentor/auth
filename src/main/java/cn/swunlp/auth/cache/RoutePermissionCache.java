package cn.swunlp.auth.cache;

import cn.swunlp.auth.entity.RoutePermission;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用信息缓存
 * @author TangXi
 * @since 2024/2/1
 */
@Component
public class RoutePermissionCache extends AuthCache<RoutePermission>{
    public RoutePermissionCache(RedisTemplate<String, RoutePermission> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    protected String function() {
        return "routePermission";
    }

}
