package com.example.template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ExampleTemplateApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ExampleTemplateApplication.class, args);
    }
}
