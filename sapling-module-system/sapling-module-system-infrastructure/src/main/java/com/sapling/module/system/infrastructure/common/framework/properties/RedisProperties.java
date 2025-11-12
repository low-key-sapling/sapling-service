package com.sapling.module.system.infrastructure.common.framework.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@AllArgsConstructor
@NoArgsConstructor
public class RedisProperties {
    private String host;
    private String port;
    private String password;

    private Cluster cluster = new Cluster();
    private Sentinel sentinel = new Sentinel();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Cluster {
        private List<String> nodes;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Sentinel {
        private String master = "mymaster";
        private List<String> nodes;
    }
}
