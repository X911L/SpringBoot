package com.xl.redisPublishAndSubscrible;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor
class RedisCacheTest {

    private final RedisCache redisCache;

    @Test
    void convertAndSend() {

        redisCache.convertAndSend("message");

    }

    @Test
    void simpleDataTopicSend() {
    }

    @Test
    void settingTopicSend() {
    }
}