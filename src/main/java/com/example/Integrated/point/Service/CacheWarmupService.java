package com.example.Integrated.point.Service;

import com.example.Integrated.Config.CacheMetricsService;
import com.example.Integrated.Config.CacheNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CacheWarmupService {

    private final ObjectProvider<CachedPointQueryService> cachedPointQueryServiceProvider;
    private final CacheMetricsService cacheMetricsService;

    public CacheWarmupService(
            ObjectProvider<CachedPointQueryService> cachedPointQueryServiceProvider,
            CacheMetricsService cacheMetricsService
    ) {
        this.cachedPointQueryServiceProvider = cachedPointQueryServiceProvider;
        this.cacheMetricsService = cacheMetricsService;
    }

    public void warmPointsMain(String version) {
        long startedAt = System.currentTimeMillis();
        try {
            int count = cachedPointQueryServiceProvider.getObject().warmVersion(version).size();
            long elapsedMillis = System.currentTimeMillis() - startedAt;
            cacheMetricsService.recordWarmup(CacheNames.POINTS_MAIN, "success", elapsedMillis);
            log.info("Warmed pointsMain cache version {} with {} positions in {} ms", version, count, elapsedMillis);
        } catch (RuntimeException e) {
            long elapsedMillis = System.currentTimeMillis() - startedAt;
            cacheMetricsService.recordWarmup(CacheNames.POINTS_MAIN, "failure", elapsedMillis);
            throw e;
        }
    }
}
