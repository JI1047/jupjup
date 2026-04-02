package com.example.Integrated.Config;

import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateMetricsConfig {

    @Bean
    public MeterBinder hibernateStatisticsBinder(EntityManagerFactory entityManagerFactory) {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);

        return new MeterBinder() {
            @Override
            public void bindTo(MeterRegistry registry) {
                FunctionCounter.builder(
                                "jupjup_hibernate_query_execution_total",
                                statistics,
                                Statistics::getQueryExecutionCount
                        )
                        .description("Total Hibernate query executions")
                        .register(registry);

                FunctionCounter.builder(
                                "jupjup_hibernate_entity_load_total",
                                statistics,
                                Statistics::getEntityLoadCount
                        )
                        .description("Total Hibernate entity loads")
                        .register(registry);
            }
        };
    }
}
