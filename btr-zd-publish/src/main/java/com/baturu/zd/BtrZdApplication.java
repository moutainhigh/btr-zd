package com.baturu.zd;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableDubbo
@EnableCaching
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.baturu.message.consumer", "com.baturu.message.producer", "com.baturu.zd"})
public class BtrZdApplication {

    public static void main(String[] args) {
        SpringApplication.run(BtrZdApplication.class, args);
        System.out.println("=========success==========");
    }
}
