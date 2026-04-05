package com.example.Integrated.point.Service;

import com.example.Integrated.Config.CacheNames;
import com.example.Integrated.Config.CacheMetricsService;
import com.example.Integrated.Config.VersionedCacheService;
import com.example.Integrated.point.Dto.PositionDto;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CachedPointQueryService {

    private final PositionApiService positionApiService;
    private final CacheMetricsService cacheMetricsService;
    private final VersionedCacheService versionedCacheService;
    private final CacheManager cacheManager;
    private final RedissonClient redissonClient;
    private final long lockWaitSeconds;
    private final long lockLeaseSeconds;

    public CachedPointQueryService(
            PositionApiService positionApiService,
            CacheMetricsService cacheMetricsService,
            VersionedCacheService versionedCacheService,
            CacheManager cacheManager,
            RedissonClient redissonClient,
            @Value("${app.cache.lock-wait-seconds:3}") long lockWaitSeconds,
            @Value("${app.cache.lock-lease-seconds:5}") long lockLeaseSeconds
    ) {
        this.positionApiService = positionApiService;
        this.cacheMetricsService = cacheMetricsService;
        this.versionedCacheService = versionedCacheService;
        this.cacheManager = cacheManager;
        this.redissonClient = redissonClient;
        this.lockWaitSeconds = lockWaitSeconds;
        this.lockLeaseSeconds = lockLeaseSeconds;
    }

    public List<PositionDto> getPosition() {
        String currentVersion = versionedCacheService.getCurrentVersion(CacheNames.POINTS_MAIN);
        return getOrLoad(CacheNames.POINTS_MAIN, currentVersion);
    }

    public List<PositionDto> warmVersion(String version) {
        Cache cache = getCache(CacheNames.POINTS_MAIN);
        List<PositionDto> positions = positionApiService.loadPositions();
        cache.put(version, positions);
        cacheMetricsService.recordPopulation(CacheNames.POINTS_MAIN, "warmup");
        return positions;
    }

    @SuppressWarnings("unchecked")
    private List<PositionDto> getOrLoad(String cacheName, String version) {
        Cache cache = getCache(cacheName);
        Cache.ValueWrapper cachedValue = cache.get(version);
        if (cachedValue != null) {
            Object value = cachedValue.get();
            if (value instanceof List<?>) {
                cacheMetricsService.recordLookup(cacheName, "hit");
                return (List<PositionDto>) value;
            }
        }

        cacheMetricsService.recordLookup(cacheName, "miss");
        return loadWithLock(cache, cacheName, version);
    }

    @SuppressWarnings("unchecked")
    private List<PositionDto> loadWithLock(Cache cache, String cacheName, String version) {
        RLock lock = redissonClient.getLock(versionedCacheService.buildLockKey(cacheName, version));
        boolean locked = false;

        try {
            locked = lock.tryLock(lockWaitSeconds, lockLeaseSeconds, TimeUnit.SECONDS);
            if (locked) {
                cacheMetricsService.recordLock(cacheName, "acquired");
                Cache.ValueWrapper cachedValue = cache.get(version);
                if (cachedValue != null) {
                    Object value = cachedValue.get();
                    if (value instanceof List<?>) {
                        cacheMetricsService.recordLookup(cacheName, "hit_after_lock");
                        return (List<PositionDto>) value;
                    }
                }

                List<PositionDto> positions = positionApiService.loadPositions();
                cache.put(version, positions);
                cacheMetricsService.recordPopulation(cacheName, "cache_miss");
                return positions;
            }

            cacheMetricsService.recordLock(cacheName, "timeout");
            cacheMetricsService.recordLookup(cacheName, "db_fallback");
            return loadWithoutCaching();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            cacheMetricsService.recordLock(cacheName, "interrupted");
            cacheMetricsService.recordLookup(cacheName, "db_fallback");
            return loadWithoutCaching();
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private List<PositionDto> loadWithoutCaching() {
        List<PositionDto> positions = positionApiService.loadPositions();
        return positions != null ? positions : Collections.emptyList();
    }

    private Cache getCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new IllegalStateException("Cache not configured: " + cacheName);
        }
        return cache;
    }
}
