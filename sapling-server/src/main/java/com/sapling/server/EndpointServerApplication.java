package com.sapling.server;

import com.dtflys.forest.springboot.annotation.ForestScan;
import lombok.extern.slf4j.Slf4j;
import com.sapling.framework.web.core.version.SpringBeanNameGenerator;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author mbws
 */
@Slf4j
@SuppressWarnings("SpringComponentScan")
@SpringBootApplication
@ComponentScan(basePackages = {"${endpoint.info.base-package}.server", "${endpoint.info.base-package}.module"}, nameGenerator = SpringBeanNameGenerator.class)
@MapperScan("com.sapling.module.system.infrastructure.gatewayImpl.database.mapper")
@ForestScan(basePackages = {"com.sapling.module.system.client.components.rc"})
public class EndpointServerApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(EndpointServerApplication.class, args);
        } catch (Throwable e) {
            log.error("EndpointServerApplication Start Failed , Reason: {}", ExceptionUtils.getStackTrace(e));
        }
    }

}