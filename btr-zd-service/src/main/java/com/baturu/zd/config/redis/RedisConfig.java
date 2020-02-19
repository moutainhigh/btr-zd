package com.baturu.zd.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * created by ketao by 2019/03/04
 **/
@Data
@Configuration
@ConfigurationProperties("redis")
public class RedisConfig {

    /**
     * redis的地址
     */
    private String hostname;
    /**
     * redis的端口
     */
    private int port;
    /**
     * redis的密码
     */
    private String password;

    /**
     * 是否使用redis集群
     */
    private boolean sentinelModel;

    /**
     * redis连接池
     */
    private Map<String, Integer> pool;

    /**
     * redis哨兵名称
     */
    private Map<String, String> sentinel;
}
