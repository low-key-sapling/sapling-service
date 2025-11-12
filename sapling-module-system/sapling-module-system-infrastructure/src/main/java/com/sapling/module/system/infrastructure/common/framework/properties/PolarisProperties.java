package com.sapling.module.system.infrastructure.common.framework.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "endpoint.polaris")
@Configuration
@AllArgsConstructor
@NoArgsConstructor
public class PolarisProperties {
    private String serviceName;
    private String serviceIp;
    private String servicePort;
    private String serverHost;
    private String serviceToken;
    private String jasyptKey;
}
