package com.genie;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@SpringBootTest
@ActiveProfiles("dev")
public class test {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private Environment env;

    @Test
    public void testRedisTemplate(){

        System.out.println(redisTemplate);

    }
    @Test
    public void testString(){


        redisTemplate.opsForValue().setIfAbsent("name","genie", 60, TimeUnit.SECONDS);
        redisTemplate.opsForValue().setIfAbsent("name","genie1", 60, TimeUnit.SECONDS);
        String name = (String) redisTemplate.opsForValue().get("name");
        System.out.println(name);
    }
    @Test
    public void testHash(){
        redisTemplate.opsForHash().put("user:1","name","genie");
        redisTemplate.opsForHash().put("user:1","age",18);
        String name=(String) redisTemplate.opsForHash().get("user:1","name");
        System.out.println(name);

    }
}
