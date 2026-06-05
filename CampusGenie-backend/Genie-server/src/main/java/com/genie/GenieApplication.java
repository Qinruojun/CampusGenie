package com.genie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
<<<<<<< HEAD
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
=======
>>>>>>> eda670b118d65e13926874e1488fb3cef4e8c49f

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

<<<<<<< HEAD
// 加上 @Bean 注解，Spring 启动时就会自动执行这个方法
// 把 new 出来的 RestTemplate 存到 Spring 的容器里当管家
@Bean
public RestTemplate restTemplate() {
    return new RestTemplate();
}
=======
>>>>>>> eda670b118d65e13926874e1488fb3cef4e8c49f
