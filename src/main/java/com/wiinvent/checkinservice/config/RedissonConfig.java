package com.wiinvent.checkinservice.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${redisson.singleServerConfig.address}")
    private String address;

    @Value("${redisson.singleServerConfig.password}")
    private String password;

    @Bean(destroyMethod="shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(address)
                .setPassword(password)
                .setConnectionPoolSize(64)
                .setConnectionMinimumIdleSize(16)
                .setTimeout(3000);
        return Redisson.create(config);
    }
}
