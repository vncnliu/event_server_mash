package top.vncnliu.event.server.mash.base.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * User: liuyq
 * Date: 2018/7/27
 * Description:
 */
@Configuration
public class RedisConfig {
    public static final String SERVER_REDIS_PREFIX = "lyq_wjl_test";
    @Bean
    @Qualifier("unLockScript")
    public RedisScript<String> releaseLock() {
        DefaultRedisScript redisScript = new DefaultRedisScript();
        redisScript.setLocation(new ClassPathResource("redis/release_lock.lua"));
        redisScript.setResultType(String.class);
        return redisScript;
    }

    @Bean
    @Qualifier("lockScript")
    public RedisScript<String> lock() {
        DefaultRedisScript redisScript = new DefaultRedisScript();
        redisScript.setLocation(new ClassPathResource("redis/lock.lua"));
        redisScript.setResultType(String.class);
        return redisScript;
    }
}
