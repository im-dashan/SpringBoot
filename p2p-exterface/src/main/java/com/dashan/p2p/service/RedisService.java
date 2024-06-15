package com.dashan.p2p.service;

public interface RedisService {

    /**
     * 向redis中保存数据
     * @param key
     * @param value
     * @param timeOut
     */
    void put(String key, String value, int timeOut);

    /**
     * 验证码对比
     * @param key
     * @return
     */
    String get(String key);
}
