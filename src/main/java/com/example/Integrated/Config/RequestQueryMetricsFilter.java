package com.example.Integrated.Config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestQueryMetricsFilter extends OncePerRequestFilter {

    private static final String TARGET_URI = "/api/map/main";

    private final MeterRegistry meterRegistry;
    private final Statistics statistics;

    public RequestQueryMetricsFilter(MeterRegistry meterRegistry, EntityManagerFactory entityManagerFactory) {
        this.meterRegistry = meterRegistry;
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        this.statistics = sessionFactory.getStatistics();
        this.statistics.setStatisticsEnabled(true);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !TARGET_URI.equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long beforeQueryCount = statistics.getQueryExecutionCount();
        long beforeEntityLoadCount = statistics.getEntityLoadCount();

        filterChain.doFilter(request, response);

        long queryDelta = Math.max(0, statistics.getQueryExecutionCount() - beforeQueryCount);
        long entityLoadDelta = Math.max(0, statistics.getEntityLoadCount() - beforeEntityLoadCount);

        Tags tags = Tags.of(
                Tag.of("uri", TARGET_URI),
                Tag.of("method", request.getMethod()),
                Tag.of("status", String.valueOf(response.getStatus()))
        );

        meterRegistry.summary("jupjup_request_query_count", tags).record(queryDelta);
        meterRegistry.summary("jupjup_request_entity_load_count", tags).record(entityLoadDelta);
    }
}
