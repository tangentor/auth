package cn.swunlp.auth.cache;

import cn.swunlp.auth.entity.ApplicationInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 应用名 -> UID映射缓存
 * @author TangXi
 * @since 2024/2/1
 */
@Component
public class AppCheckCache extends AuthCache<String>{
    public AppCheckCache(RedisTemplate<String, String> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    protected String function() {
        return "appCheck";
    }
}
