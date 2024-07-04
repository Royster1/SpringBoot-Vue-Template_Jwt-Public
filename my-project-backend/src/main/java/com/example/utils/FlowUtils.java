package com.example.utils;

import ch.qos.logback.core.util.TimeUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

// 通过redis实现限流
@Component
public class FlowUtils {

    @Resource
    RedisTemplate redisTemplate;

    public boolean limitOnceCheck(String key, int blockTime){
        // 在冷却时间中
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            return false;
        } else {
            // 如果不在冷却时间中, 就进入冷却时间中
            redisTemplate.opsForValue().set(key, "", blockTime, TimeUnit.SECONDS);
        }
        return true;
    }

}

//*
// 通过redisTemplate.hasKey(key)方法判断Redis中是否存在指定key，如果存在说明处于冷却时间中，直接返回false，表示限流。
// 如果Redis中不存在指定key，即不在冷却时间中，则通过redisTemplate.opsForValue().set(key, "", blockTime, TimeUnit.SECONDS)方法将该key设置到Redis中，并设置过期时间为blockTime秒。
// 最后返回true，表示可以继续执行操作。
// */
