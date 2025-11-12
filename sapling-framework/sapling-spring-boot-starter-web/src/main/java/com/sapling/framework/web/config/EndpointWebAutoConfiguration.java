package com.sapling.framework.web.config;

import com.sapling.framework.web.core.exception.handler.GlobalResponseBodyHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * @author mbws
 */
@Configuration
@ComponentScan(basePackages = "com.sapling.framework.web.core")
public class EndpointWebAutoConfiguration implements WebMvcConfigurer {

    @Bean
    public GlobalResponseBodyHandler globalResponseBodyHandler() {
        return new GlobalResponseBodyHandler();
    }


}
