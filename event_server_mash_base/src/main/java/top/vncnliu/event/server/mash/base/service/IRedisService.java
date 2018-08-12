package top.vncnliu.event.server.mash.base.service;

import java.util.*;
import java.util.concurrent.TimeUnit;

public interface IRedisService {

    void set(String key, String value, long expireTime, TimeUnit expireTimeUnit);

    String get(String key);

    void delete(String key);

    long incr(String key, long expireTime, TimeUnit expireTimeUnit);

    /**
     * @param expireTime 过期时间
     * @return
     */
    long incr(String key, Date expireTime);

    void hset(String key, String hashKey, String value, long expireTime, TimeUnit expireTimeUnit);

    Set<String> getSet(String key);

    boolean setExit(String key, String value);

    void setSet(String key, Set<String> values, long expireTime, TimeUnit expireTimeUnit);

    void hsetAll(String key, Map<String, String> keyValuePairs, long expireTime, TimeUnit expireTimeUnit);

    void hsetAll(String key, Map<String, String> keyValuePairs);

    void hset(String key, String hashKey, String value);

    String hget(String key, String hashKey);

    Map<String, String> hgetAll(String key);

    List<String> multiGet(String key, Collection<String> hashKeys);

    /**
     * @param requestId 锁拥有者
     * @param expireTime 秒
     */
    boolean lock(String lockKey, String requestId, int expireTime);

    boolean releaseLock(String lockKey, String requestId);

    void hdel(String serverClientAddrsKey, String format);
}
