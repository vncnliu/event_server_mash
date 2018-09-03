package top.vncnliu.event.server.mash.base.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import top.vncnliu.event.server.mash.base.config.RedisConfig;
import top.vncnliu.event.server.mash.base.service.IRedisService;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisServiceImpl implements IRedisService {

    @Autowired
    RedisTemplate<String,String> redisTemplate;
    @Autowired
    @Qualifier("unLockScript")
    RedisScript<String> unLockScript;
    @Autowired
    @Qualifier("lockScript")
    RedisScript<String> lockScript;

    private String build(String key) {
        return RedisConfig.SERVER_REDIS_PREFIX+"|"+key;
    }

    @Override
    public void set(String key, String value, long expireTime, TimeUnit expireTimeUnit) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(build(key), value);
        redisTemplate.expire(build(key),expireTime,expireTimeUnit);
    }

    @Override
    public String get(String key) {
        ValueOperations<String, String> stringValueOperations = redisTemplate.opsForValue();
        return stringValueOperations.get(build(key));
    }
    @Override
    public void hset(String key, String hashKey, String value, long expireTime, TimeUnit expireTimeUnit) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        opsForHash.put(build(key), hashKey, value);
        redisTemplate.expire(build(key), expireTime, expireTimeUnit);
    }

    @Override
    public Set<String> getSet(String key){
        SetOperations<String,String> setOperations = redisTemplate.opsForSet();
        return setOperations.members(build(key));
    }

    @Override
    public boolean setExit(String key,String value){
        SetOperations<String,String> setOperations = redisTemplate.opsForSet();
        return setOperations.isMember(build(key),value);
    }

    @Override
    public void setSet(String key, Set<String> values,long expireTime, TimeUnit expireTimeUnit){
        redisTemplate.delete(build(key));
        SetOperations<String,String> setOperations = redisTemplate.opsForSet();
        setOperations.add(build(key),values.toArray(new String[0]));
        redisTemplate.expire(build(key), expireTime, expireTimeUnit);
    }

    @Override
    public void hsetAll(String key, Map<String,String> keyValuePairs, long expireTime, TimeUnit expireTimeUnit) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        opsForHash.putAll(build(key),keyValuePairs);
        redisTemplate.expire(build(key), expireTime, expireTimeUnit);
    }
    @Override
    public void hsetAll(String key, Map<String,String> keyValuePairs) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        opsForHash.putAll(build(key),keyValuePairs);
    }

    @Override
    public void hset(String key, String hashKey, String value) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        opsForHash.put(build(key), hashKey, value);
    }

    @Override
    public void hdel(String key, String hashKey) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        opsForHash.delete(build(key), hashKey);
    }

    @Override
    public String hget(String key, String hashKey) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(build(key), hashKey);
    }

    @Override
    public List<String> multiGet(String key, Collection<String> hashKeys) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        return opsForHash.multiGet(build(key), hashKeys);
    }
    @Override
    public Map<String,String> hgetAll(String key) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        return opsForHash.entries(build(key));
    }


    @Override
    public void delete(String key) {
        redisTemplate.delete(build(key));
    }

    @Override
    public long incr(String key, long expireTime, TimeUnit expireTimeUnit) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        long counter = valueOperations.increment(build(key), 1);
        if (counter == 1) {
            redisTemplate.expire(build(key), expireTime, expireTimeUnit);
        }
        return counter;
    }

    @Override
    public long incr(String key, Date expireTime) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        long counter = valueOperations.increment(build(key), 1);
        if (counter == 1) {
            redisTemplate.expireAt(build(key), expireTime);
        }
        return counter;
    }

    @Override
    public boolean lock(String lockKey, String requestId, int expireTime){
        String result = redisTemplate.execute(lockScript,Collections.singletonList(build(lockKey)),requestId,""+expireTime);
        log.trace("{}获取锁结果{}",build(lockKey),result);
        return "1".equals(result);
    }

    @Override
    public boolean releaseLock(String lockKey, String requestId){
        String result = redisTemplate.execute(unLockScript,Collections.singletonList(build(lockKey)),requestId);
        log.trace("{}释放锁结果{}",build(lockKey),result);
        return "1".equals(result);
    }

}