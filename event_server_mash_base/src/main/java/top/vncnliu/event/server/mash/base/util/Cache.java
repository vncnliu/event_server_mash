package top.vncnliu.event.server.mash.base.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * User: vncnliu
 * Date: 2018/8/14
 * Description:
 */
public class Cache<K, V> {

    /**
     * 毫秒
     */
    private int expireTime;
    private Consumer<Map.Entry<K,V>> consumer;

    private Map<K, Data> hashMapPositive = new ConcurrentHashMap<>();
    private Map<V, K> hashMapReverse = new ConcurrentHashMap<>();

    class CacheEntry implements Map.Entry<K,V> {

        public CacheEntry(K k, V v) {
            this.k = k;
            this.v = v;
        }

        K k;
        V v;

        @Override
        public K getKey() {
            return k;
        }

        @Override
        public V getValue() {
            return v;
        }

        @Override
        public V setValue(V value) {
            this.v=value;
            return value;
        }
    }

    public Cache(int expireTime, int cleanUpDelayTime, Consumer<Map.Entry<K,V>> consumer) {
        this.expireTime = expireTime;
        this.consumer = consumer;
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            for (Map.Entry<K, Data> kDataEntry : hashMapPositive.entrySet()) {
                if(kDataEntry.getValue().expireTime<System.currentTimeMillis()){
                    hashMapPositive.remove(kDataEntry.getKey());
                    hashMapReverse.remove(kDataEntry.getValue().value);
                    if(consumer!=null){
                        consumer.accept(new CacheEntry(kDataEntry.getKey(),kDataEntry.getValue().value));
                    }
                }
            }
        },cleanUpDelayTime,cleanUpDelayTime,TimeUnit.MILLISECONDS);
    }

    public void put(K key, V value){
        hashMapPositive.put(key, new Data(value,System.currentTimeMillis()+expireTime));
        hashMapReverse.put(value,key);
    }

    public V get(K key){
        Data data = hashMapPositive.get(key);
        if(data!=null){
            if(System.currentTimeMillis()>data.expireTime){
                return null;
            } else {
                return data.value;
            }
        } else {
            return null;
        }
    }

    public V removeByKey(K key){
        Data data = hashMapPositive.remove(key);
        if(data!=null){
            if(consumer!=null){
                consumer.accept(new CacheEntry(key,data.value));
            }
            hashMapReverse.remove(data.value);
            return data.value;
        }
        return null;
    }

    public V removeByValue(V value){
        K key = hashMapReverse.remove(value);
        if(key!=null){
            Data data = hashMapPositive.remove(key);
            if(consumer!=null){
                consumer.accept(new CacheEntry(key,value));
            }
            return data.value;
        }
        return null;
    }

    private class Data {
        Data(V value, long expireTime) {
            this.value = value;
            this.expireTime = expireTime;
        }

        private V value;
        private long expireTime;
    }

}
