package com.dashan.p2p.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dashan.p2p.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Component
@Service(interfaceClass = RedisService.class, version = "1.0.0", timeout = 15000)
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 向 ridis中保存验证码
     * @param key
     * @param value
     * @param timeOut
     */
    @Override
    public void put(String key, String value, int timeOut) {
        // 修改RedisTemplate key的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 设置超时时间单位为分钟
        redisTemplate.opsForValue().set(key, value, timeOut, TimeUnit.MINUTES);

    }

    /**
     * 从 redis中获取验证码
     * @param key
     * @return
     */
    @Override
    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }
}
