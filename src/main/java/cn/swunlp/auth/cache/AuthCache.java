package cn.swunlp.auth.cache;

import cn.swunlp.backend.base.data.cache.BaseCache;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author TangXi
 * @since 2024/2/1
 */

public abstract class AuthCache<T> extends BaseCache<T> {

    public AuthCache(RedisTemplate<String, T> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    protected String domain() {
        return "AuthService:"+function();
    }

    protected abstract String function();
}
