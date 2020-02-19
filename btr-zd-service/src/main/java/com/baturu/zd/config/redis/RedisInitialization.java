package com.baturu.zd.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Arrays;
import java.util.List;

/**
 * created by ketao by 2019/03/04
 **/
@Slf4j
@Configuration
public class RedisInitialization {

    @Autowired
    private RedisConfig redisConfig;
    @Autowired
    JedisPoolConfig jedisPoolConfig;

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(redisConfig.getPool().get("maxIdle"));
        poolConfig.setMaxWaitMillis(redisConfig.getPool().get("maxWait"));
        poolConfig.setMaxTotal(redisConfig.getPool().get("maxTotal"));
        return poolConfig;
    }

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory jcf;
        //$NON-NLS-1$
        log.info("redisConnectionFactory() - jedisPoolConfig = {}", jedisPoolConfig);

        if (redisConfig.isSentinelModel()) {
            List<String> sentinels;
            String masterName = redisConfig.getSentinel().get("master");
            String nodesStr = redisConfig.getSentinel().get("nodes");
            RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration().master(masterName);
            if (StringUtils.isNotBlank(nodesStr)) {
                String[] nodes = nodesStr.split(",");
                sentinels = Arrays.asList(nodes);
                for (String sentinel : sentinels) {
                    String[] array = sentinel.split(":");
                    sentinelConfig.sentinel(array[0], Integer.valueOf(array[1]));
                }
            }
            jcf = new JedisConnectionFactory(sentinelConfig, jedisPoolConfig);
        } else {
            jcf = new JedisConnectionFactory(jedisPoolConfig);
            RedisStandaloneConfiguration standaloneConfiguration = jcf.getStandaloneConfiguration();
            assert standaloneConfiguration != null;
            standaloneConfiguration.setHostName(redisConfig.getHostname());
            standaloneConfiguration.setPort(redisConfig.getPort());
            standaloneConfiguration.setPassword(RedisPassword.of(redisConfig.getPassword()));
        }
        return jcf;
    }


    @Bean
    @SuppressWarnings({"unchecked", "rawtypes"})
    public RedisTemplate redisTemplate() {

        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);

        //jdk的序列化
        RedisSerializer jdkSerializer = new JdkSerializationRedisSerializer();
        redisTemplate.setValueSerializer(jdkSerializer);

        //json序列化
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        return redisTemplate;
    }
}
