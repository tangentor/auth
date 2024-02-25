package cn.swunlp.auth.cache;

import cn.swunlp.backend.base.web.constant.AuthResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author TangXi
 * @since 2024/2/1
 */
@Component
public class AuthResultCache extends AuthCache<AuthResult>{
    public AuthResultCache(RedisTemplate<String, AuthResult> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    protected String function() {
        return "authResult";
    }
}
