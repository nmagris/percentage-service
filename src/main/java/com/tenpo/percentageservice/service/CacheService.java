package com.tenpo.percentageservice.service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private static final Logger log = LoggerFactory.getLogger(CacheService.class);
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String FALLBACK_KEY = "_FALLBACK";

    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveValue(String key, Integer value, long expiresIn) {
        String fallbackKey = Strings.concat(key, FALLBACK_KEY);
        log.info("Saving value {} on cache with key {}", value, key);
        redisTemplate.opsForValue().set(key, value, expiresIn, TimeUnit.MINUTES);
        log.info("Saving value {} on cache with key {}", value, fallbackKey);
        redisTemplate.opsForValue().set(fallbackKey, value);
    }

    public Optional<Integer> getValue(String key) {
        log.info("Getting value from cache with key {}", key);
        try {
            Integer value = (Integer) redisTemplate.opsForValue().get(key);
            return Optional.of(value);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
