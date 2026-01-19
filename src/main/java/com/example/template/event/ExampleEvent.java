package com.example.template.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExampleEvent {
    private String id;
    private String type;
    private String payload;
    private LocalDateTime timestamp;
}
