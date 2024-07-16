package cn.swunlp.auth.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 应用名 -> 是否由平台介入管理
 * @author TangXi
 * @since 2024/7/3 11:20
 */
@Component
public class AppAuthConfigCache extends AuthCache<Boolean>{
    public AppAuthConfigCache(RedisTemplate<String, Boolean> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    protected String function() {
        return "appPlatformIntervention";
    }
}
