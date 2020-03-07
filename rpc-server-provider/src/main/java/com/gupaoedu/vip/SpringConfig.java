package com.gupaoedu.vip;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.gupaoedu.vip")
public class SpringConfig {
    @Bean
    public GpRpcServer gpRpcServer() {
        return new GpRpcServer(8080);
    }
}
