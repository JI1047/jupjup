package com.example.Integrated.Config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheMetricsService {

    private final MeterRegistry meterRegistry;

    public CacheMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void recordLookup(String cacheName, String outcome) {
        Counter.builder("jupjup_cache_lookup_total")
                .description("Cache lookup outcomes")
                .tag("cache_name", cacheName)
                .tag("outcome", outcome)
                .register(meterRegistry)
                .increment();
    }

    public void recordLock(String cacheName, String outcome) {
        Counter.builder("jupjup_cache_lock_total")
                .description("Distributed lock outcomes for cache population")
                .tag("cache_name", cacheName)
                .tag("outcome", outcome)
                .register(meterRegistry)
                .increment();
    }

    public void recordPopulation(String cacheName, String trigger) {
        Counter.builder("jupjup_cache_population_total")
                .description("Cache population count by trigger")
                .tag("cache_name", cacheName)
                .tag("trigger", trigger)
                .register(meterRegistry)
                .increment();
    }

    public void recordVersionSwitch(String cacheName) {
        Counter.builder("jupjup_cache_version_switch_total")
                .description("Cache version switch count")
                .tag("cache_name", cacheName)
                .register(meterRegistry)
                .increment();
    }

    public void recordWarmup(String cacheName, String result, long elapsedMillis) {
        Timer.builder("jupjup_cache_warmup_duration")
                .description("Cache warmup duration")
                .tag("cache_name", cacheName)
                .tag("result", result)
                .register(meterRegistry)
                .record(elapsedMillis, TimeUnit.MILLISECONDS);
    }
}
