package cn.swunlp.auth.cache;

import cn.swunlp.auth.entity.ApplicationInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 应用信息缓存
 * @author TangXi
 * @since 2024/2/1
 */
@Component
public class AppInfoCache extends AuthCache<ApplicationInfo>{
    public AppInfoCache(RedisTemplate<String, ApplicationInfo> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    protected String function() {
        return "appInfo";
    }
}
