package com.example.Integrated.Config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Configuration
public class CacheConfig implements CachingConfigurer {

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            @Value("${app.cache.ttl-days:7}") long cacheTtlDays
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(cacheTtlDays))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer(objectMapper)
                        )
                );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(configuration)
                .transactionAware()
                .build();
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new SortedCollectionKeyGenerator();
    }

    private static class SortedCollectionKeyGenerator implements KeyGenerator {
        @Override
        public Object generate(Object target, Method method, Object... params) {
            String normalizedParams = Arrays.stream(params)
                    .map(this::normalize)
                    .collect(Collectors.joining("|"));
            return target.getClass().getSimpleName() + ":" + method.getName() + ":" + normalizedParams;
        }

        private String normalize(Object value) {
            if (value instanceof Collection<?> collection) {
                return collection.stream()
                        .map(String::valueOf)
                        .sorted(Comparator.naturalOrder())
                        .collect(Collectors.joining(","));
            }
            return String.valueOf(value);
        }
    }
}
