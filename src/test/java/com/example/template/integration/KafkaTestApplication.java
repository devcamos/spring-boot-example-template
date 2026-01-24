package com.example.template.integration;

import com.example.template.config.KafkaConfig;
import com.example.template.event.EventProducer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
        basePackages = {"com.example.template"},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "com\\.example\\.template\\.config\\.(JpaConfig|RedisConfig|.*)"
        )
)
public class KafkaTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(KafkaTestApplication.class, args);
    }
}
