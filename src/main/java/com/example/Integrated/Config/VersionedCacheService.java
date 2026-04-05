package com.example.Integrated.Config;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VersionedCacheService {

    private static final String LATEST_VERSION_SUFFIX = ":latest_version";
    private static final String DEFAULT_VERSION = "v1";

    private final StringRedisTemplate redisTemplate;

    public VersionedCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String getCurrentVersion(String cacheName) {
        String version = redisTemplate.opsForValue().get(latestVersionKey(cacheName));
        if (version != null) {
            return version;
        }

        redisTemplate.opsForValue().setIfAbsent(latestVersionKey(cacheName), DEFAULT_VERSION);
        String initializedVersion = redisTemplate.opsForValue().get(latestVersionKey(cacheName));
        return initializedVersion != null ? initializedVersion : DEFAULT_VERSION;
    }

    public void switchToVersion(String cacheName, String newVersion) {
        redisTemplate.opsForValue().set(latestVersionKey(cacheName), newVersion);
    }

    public String createNextVersion() {
        return "v" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public String buildLockKey(String cacheName, String version) {
        return "lock:" + cacheName + ":" + version;
    }

    private String latestVersionKey(String cacheName) {
        return cacheName + LATEST_VERSION_SUFFIX;
    }
}
