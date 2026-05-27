package com.genie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement //开启注解方式的事务管理
@Slf4j
@EnableCaching
public class GenieApplication {
    public static void main(String[] args) {
        SpringApplication.run(GenieApplication.class, args);
        log.info("server started");
    }
}

