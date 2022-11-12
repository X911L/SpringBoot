/**
 * <p>Copyright (c) Shanghai TY Technology Co., Ltd. All Rights Reserved.</p>
 *
 * @FileName: RedisUtil.java
 * @Description:RedisUtil
 * @author: User
 * @Creat: 2017年5月5日
 * <p>
 * Modification History:
 * Data         Author      Version        Description
 * -------------------------------------------------------------
 * 2017年5月5日        User
 */
package com.xl.common.util;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.commands.JedisCommands;

/**
 * @author user
 */
@RequiredArgsConstructor
@Slf4j
public class RedisUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public final static int RETRY_CNT = 10;
    /**
     * 将key 的值设为value ，当且仅当key 不存在，等效于 SETNX。
     */
    public static final String NX = "NX";

    /**
     * 将key 的值设为value ，当且仅当key存在，等效于 SETXX。
     */
    public static final String XX = "XX";

    /**
     * seconds — 以秒为单位设置 key 的过期时间，等效于EXPIRE key seconds
     */
    public static final String EX = "EX";

    /**
     * 调用set后的返回值
     */
    public static final String OK = "OK";

    public final RedisTemplate<String, Object> redisTemplate;

    public Boolean hasKey(String key) {
        return this.exchange(() -> redisTemplate.hasKey(key), false, key);
    }

    public void saveObject(String key, Object value) {
        this.exchange((k) -> redisTemplate.opsForValue().set(k, value), key, true, key, value);
    }

    public <T> T lPopList(String key) {
        Object value = this.exchange(() -> redisTemplate.opsForList().leftPop(key), true, key);
        if (value != null) {
            return (T) value;
        } else {
            return null;
        }
    }

    public Long getListSize(String key) {
        return this.exchange(() -> redisTemplate.opsForList().size(key), false, key);
    }

    public void rPushList(String key, Object value) {
       this.exchange((k) -> redisTemplate.opsForList().rightPush(k, value), key, true, key, value);
    }

    public List<Object> rangList(String key, long start, long end) {
        return this.exchange(() -> redisTemplate.opsForList().range(key, start, end), false, key, start, end);
    }

    public void lPushList(String key, Object value) {
        this.exchange((k) -> redisTemplate.opsForList().leftPush(k, value), key, true, key, value);
    }
    public void rpushAll(String key, Collection values) {
        this.exchange((k) -> redisTemplate.opsForList().rightPushAll(k, values), key, true, values);
    }

    public void saveHash(String key, String hashKey, Object value) {
        this.exchange((k) -> redisTemplate.opsForHash().put(k, hashKey, value), key, true, key, hashKey, value);
    }

    public Long deleteHash(String key, Object ... hashKeys) {
        return this.exchange(() -> redisTemplate.opsForHash().delete(key, hashKeys), true, key, hashKeys);
    }

    public Boolean setKeyTimeout(String key, long timeout, TimeUnit unit) {
        return this.exchange(() -> redisTemplate.expire(key, timeout, unit), true, key, timeout, unit);
    }

    public void addSetObject(String key, Object value) {
        this.exchange((k) -> redisTemplate.opsForSet().add(k, value), key, true, value);
    }

    public Long addSetObject2(String key, Object value) {
       return this.exchange(() -> redisTemplate.opsForSet().add(key, value), true, key, value);
    }

    public Long addSetObject3(String key, Object... value) {
        return this.exchange(() -> redisTemplate.opsForSet().add(key, value), true, key, value);
    }

    public Set<Object> getSetObject(String key) {
        return this.exchange(() -> redisTemplate.opsForSet().members(key), false, key);
    }

    public <T> List<T> getMultiObjects(Collection<String> keys) {
        List<Object> value = this.exchange(() -> redisTemplate.opsForValue().multiGet(keys), false, keys);
        if (value != null) {
            return (List<T>) value;
        } else {
            return null;
        }
    }

    public <T> List<T> getHashMultiValues(String key, List<Object> hashKeys) {
        List<Object> value = this.exchange(() -> redisTemplate.opsForHash().multiGet(key, hashKeys), false, key, hashKeys);
        if(CollectionUtils.isNotEmpty(value)) {
            return (List<T>) value;
        }
        return null;
    }
    public void saveTimeOut(String key, Object value, long timeout, TimeUnit unit) {
        this.exchange((k) -> redisTemplate.opsForValue().set(k, value, timeout, unit), key, true, key, value, timeout);
    }

    public Long incrementTimeOut(String key, Long timeout) {
        return this.exchange(() -> redisTemplate.opsForValue().increment(key), true, key, timeout);
    }

    public void decrement(String key) {
        this.exchange((k) -> redisTemplate.opsForValue().decrement(k), key, true, key);
    }

    public void saveTimeOut(String key, Object value, int timeout) {
        this.exchange((k) -> redisTemplate.opsForValue().set(k, value, timeout, TimeUnit.MINUTES), key, true, key, value, timeout);
    }

    public Boolean delete(String key) {
        return this.exchange(() -> redisTemplate.delete(key), true, key);
    }

    public Long delete(Collection<String> keys) {
        return this.exchange(() -> redisTemplate.delete(keys), true, keys);
    }

    public Long deleteAll(Collection keys) {
        return this.exchange(() -> redisTemplate.delete(keys), true, keys);
    }

    public void removeSetObject(String key, Object value) {
        this.exchange((k) -> redisTemplate.opsForSet().remove(k, value), key, true, key, value);
    }

    public List<Object> popSet(String key, long count) {
        return this.exchange(() -> redisTemplate.opsForSet().pop(key, count), true, key, count);
    }

    public Long getSetSize(String key) {
        return this.exchange(() -> redisTemplate.opsForSet().size(key), false, key);
    }

    public Map<Object, Object> getHashEntries(String key) {
        return this.exchange(() -> redisTemplate.opsForHash().entries(key), false, key);
    }

    public Boolean renameIfAbsent(String key, String newKey) {
        return this.exchange(() -> redisTemplate.renameIfAbsent(key, newKey), true, key, newKey);
    }

    public Set<Object> getHashKeys(String key) {
        return this.exchange(() -> redisTemplate.opsForHash().keys(key), false, key);
    }

    public <T> List<T> getHashValues(String key) {
        List<Object> value = this.exchange(() -> redisTemplate.opsForHash().values(key), false, key);
        if(CollectionUtils.isNotEmpty(value)) {
            return (List<T>) value;
        }
        return null;
    }

    public <T> T getHashValue(String key, Object hashKey) {
        Object value = this.exchange(() -> redisTemplate.opsForHash().get(key, hashKey), false, key, hashKey);
        if (value != null) {
            return (T) value;
        } else {
            return null;
        }
    }

    public Long increment(String key, Long delta) {
        return this.exchange(() -> redisTemplate.opsForValue().increment(key,delta), true, key, delta);
    }

    public Long getExpire(String key) {
        return this.exchange(() -> redisTemplate.opsForValue().getOperations().getExpire(key), false, key);
    }

    //public Boolean setNXAndEX(String key, String value, long seconds) {
    //    return setXX(key, value, seconds, NX);
    //}

    public Long incrementHashValue(String key, Object hashKey, long delta) {
        return this.exchange(() -> redisTemplate.opsForHash().increment(key, hashKey, delta), true, key, hashKey, delta);
    }

    public Map<Object, Double> typedTupleZetByScoreWithScores(String key, double min, double max) {
        Set<ZSetOperations.TypedTuple<Object>> tuples = this
                .exchange(() -> redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max), false, key, min, max);

        if (CollectionUtils.isNotEmpty(tuples)) {
            return tuples.stream()
                    .collect(Collectors.toMap(ZSetOperations.TypedTuple::getValue, ZSetOperations.TypedTuple::getScore));
        }
        return null;
    }

    public Double incrementZSetScore(String key, Object value, Double score) {
        return this.exchange(() -> redisTemplate.opsForZSet().incrementScore(key, value, score), true, key, value, score);
    }

    public void saveHashTimeOut(String key, String hashKey, Object value, Long timeout) {
        this.exchange((k) -> {
            redisTemplate.opsForHash().put(k, hashKey, value);
            if (Objects.nonNull(timeout)) {
                redisTemplate.expire(k, timeout, TimeUnit.MILLISECONDS);
            }
        }, key, true, key, hashKey, value, timeout);
    }

    /**
     * 添加或更新成员分数
     * @param key zset key
     * @param value 成员名
     * @param score 成员分数
     * @return 成功或失败
     */
    public Boolean zsetAdd(String key, Object value, Double score) {
        return this.exchange(() -> redisTemplate.opsForZSet().add(key, value, score), true, key, value, score);
    }
    
    public Long zSetAdd(String key, Set<ZSetOperations.TypedTuple<Object>> value) {
        return this.exchange(() -> redisTemplate.opsForZSet().add(key, value), true, key, value);
    }

    //public Boolean setXXAndEX(String key, String value, long seconds) {
    //    return setXX(key, value, seconds, XX);
    //}

    //private Boolean setXX(String key, String value, long seconds, String xx) {
    //    return this.exchange(() -> redisTemplate.execute((RedisCallback<Boolean>) connection -> {
    //        Object nativeConnection = connection.getNativeConnection();
    //        // if (nativeConnection instanceof JedisCommands) {
    //        String result = ((JedisCommands) nativeConnection).set(key, value, xx, EX, seconds);
    //        // }
    //        connection.close();
    //        return Objects.equals(result, OK);
    //    }), true, key, value, seconds);
    //}

    private static String write2JsonString(Object obj) {
        if (obj != null) {
            try {
                return objectMapper.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void exchange(Consumer<String> consumer, String key, boolean edit, Object ... param) {
        int cnt = 0;
        do {
            cnt += 1;
            try {
                consumer.accept(key);
                return;
            } catch (RedisConnectionFailureException ex) {
                log.error("redis 操作异常，当前第" + cnt + "次" + write2JsonString(param), ex);
                // 超时，直接抛出异常
                if (edit && Objects.nonNull(ex.getCause()) && Objects.nonNull(ex.getCause().getCause())
                        && ex.getCause().getCause() instanceof SocketTimeoutException) {
                    throw ex;
                }
                RedisConnectionUtils.unbindConnection(Objects.requireNonNull(redisTemplate.getConnectionFactory()));
            }
        } while (cnt <= RETRY_CNT);
        throw new RuntimeException("redis方法没有成功，key is " + write2JsonString(param));
    }

    private  <T> T exchange(Supplier<T> supplier, boolean edit, Object ... param) {
        int cnt = 0;
        do {
            cnt += 1;
            try {
                return supplier.get();
            } catch (RedisConnectionFailureException ex) {
                log.error("redis 操作异常，当前第" + cnt + "次" + write2JsonString(param), ex);
                // 超时，直接抛出异常
                if (edit && Objects.nonNull(ex.getCause()) && Objects.nonNull(ex.getCause().getCause())
                        && ex.getCause().getCause() instanceof SocketTimeoutException) {
                    throw ex;
                }
                RedisConnectionUtils.unbindConnection(Objects.requireNonNull(redisTemplate.getConnectionFactory()));
            }
        } while (cnt <= RETRY_CNT);
        throw new RuntimeException("redis方法没有成功，key is " + write2JsonString(param));
    }



    /**
     * 查询集合中指定顺序的值和score (其中有序集成员按 score 值递增(从小到大)排序)，0, -1 表示获取全部的集合内容
     */
    public Set<ZSetOperations.TypedTuple<Object>> zsetRangeWithScores(String key, int start, int stop) {
        return this.exchange(() -> redisTemplate.opsForZSet().rangeWithScores(key, start, stop), false, key, start, stop);
    }

    /**
     * 查询集合中指定顺序的值和score (其中有序集成员按 score 值递减(从大到小)排序)，0, -1 表示获取全部的集合内容
     */
    public Set<ZSetOperations.TypedTuple<Object>> zsetRevRangeWithScores(String key, int start, int stop) {
        return this.exchange(() -> redisTemplate.opsForZSet().reverseRangeWithScores(key, start, stop), false, key, start, stop);
    }
    public Long zsetRemoveRange(String key, int start, int stop) {
        return this.exchange(() -> redisTemplate.opsForZSet().removeRange(key, start, stop), true, key, start, stop);
    }


    public Set<String> getPatternkeys(String pattern) {
        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keys = new HashSet<>();
            ScanOptions.ScanOptionsBuilder scanOptionsBuilder = ScanOptions.scanOptions();
            scanOptionsBuilder.match(pattern);
            Cursor<byte[]> cursor = connection.scan(scanOptionsBuilder.build());
            while (cursor.hasNext()) {
                keys.add(new String(cursor.next()));
            }
            return keys;
        });
    }


    public <T> T rbPop(String key) {
        Object value = this.exchange(() -> redisTemplate.opsForList().rightPop(key, 5, TimeUnit.SECONDS), false, key);
        if (value != null) {
            return (T) value;
        } else {
            return null;
        }

    }

    public <T> T getObject(String key) {
        Object value =  this.exchange(() -> redisTemplate.opsForValue().get(key), false, key);
        if (value != null) {
            return (T) value;
        } else {
            return null;
        }
    }

    public long getMemberId(String key) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        return entityIdCounter.incrementAndGet();
    }

    public static <T> List<T> readJson2EntityList(String jsonString, Class<T> clazz) {
        if (jsonString != null) {
            ObjectMapper mapper = new ObjectMapper();

            JavaType javaType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
            try {
                return mapper.readValue(jsonString, javaType);
            } catch (IOException var4) {
                log.error("readJson2EntityList error jsonString={},class={}", jsonString, clazz.getName(), var4);
            }
        }
        return null;
    }


    public <T, R> void saveHashAll(String key, Map<T, R> stringListMap) {
        this.exchange((k) -> redisTemplate.opsForHash().putAll(key, stringListMap), key, true, key,stringListMap);
    }

    public <T, R> void saveHashAllTimeOut(String key, Map<T, R> stringListMap, Long timeout) {
        this.exchange((k) -> {
            redisTemplate.opsForHash().putAll(key, stringListMap);
            redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
        }, key, true, key,stringListMap, timeout);
    }


    public Long zsetRemoveByValue(String key, Object... value) {
        return this.exchange(() -> redisTemplate.opsForZSet().remove(key, value), true, key, value);
    }

    public Double getZset(String key, String value, Integer cnt) {
        return this.exchange(() -> redisTemplate.opsForZSet().score(key, value), false, key, value);
    }

    public Long getZSetSize(String key, Integer cnt) {
        return this.exchange(() -> redisTemplate.opsForZSet().size(key), false, key);
    }

    public Long getZSetRank(String key, String value, Integer cnt) {
        return this.exchange(() -> redisTemplate.opsForZSet().reverseRank(key,value), false, key, value);
    }

    public Long getZCount(String key, double start, double end,int cnt) {
        return this.exchange(() -> redisTemplate.opsForZSet().count(key,start,end), false, key, start, end);
    }

    public Long removeListValue(String key, long var2, Object var4) {
        return this.exchange(() -> redisTemplate.opsForList().remove(key,var2, var4), false, key);
    }
}
