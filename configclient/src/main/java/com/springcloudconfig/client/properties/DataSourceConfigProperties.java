package com.springcloudconfig.client.properties;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
@Data
@ToString
public class DataSourceConfigProperties {
    private String url;
    private String username;
    private String password;

    private String driverClassName;

    private HikariConfigProperties hikari;

    @Data
    @ToString
    public static class HikariConfigProperties {

        private Integer connectionTimeout;
        private Integer maximumPoolSize;

    }
}
