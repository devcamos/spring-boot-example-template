package com.example.template;

/**
 * Central constants for use across all test classes.
 * Contains only shared constants that are used across multiple test classes.
 */
public final class TestConstants {
    
    private TestConstants() {
        // Utility class - prevent instantiation
    }
    
    // API Base URL
    public static final String URL_UNDER_TEST = "/api/v1/examples";
    
    // HTTP Status Codes
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_NOT_FOUND = 404;
    
    // Entity Status Values
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    
    // Test Entity IDs
    public static final Long TEST_ENTITY_ID = 1L;
    public static final Long NON_EXISTENT_ENTITY_ID = 999L;
    
    // HTTP Headers
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    
    // JSON Path Expressions
    public static final String JSON_PATH_TIMESTAMP = "$.timestamp";
    public static final String JSON_PATH_STATUS = "$.status";
    public static final String JSON_PATH_ERROR = "$.error";
    public static final String JSON_PATH_MESSAGE = "$.message";
    public static final String JSON_PATH_CORRELATION_ID = "$.correlationId";
    public static final String JSON_PATH_PATH = "$.path";
    public static final String JSON_PATH_ID = "$.id";
    public static final String JSON_PATH_NAME = "$.name";
    public static final String JSON_PATH_CONTENT = "$.content";
    public static final String JSON_PATH_TOTAL_ELEMENTS = "$.totalElements";
}
