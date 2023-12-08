package com.universal.core.library.configuration;

import com.universal.core.library.snowflake.SnowflakeIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnowflakeConfig {
    /**
     * Bean of snow flake
     *
     * @return snow flake bean
     */
    @Bean
    public SnowflakeIdGenerator snowflakeIdGenerator() {
        return new SnowflakeIdGenerator();
    }
}
