package com.spring.memodb.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public final class ApplicationConstants {

    public static final String APP_NAME = "MemoDB";

    private ApplicationConstants() {
    }
}
