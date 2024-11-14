package org.example.backend.service;

import javax.annotation.Resource;

import org.example.backend.constant.RedisConstant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBitSet;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

/**
 * 用户服务测试
 */
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    @Test
    void userRegister() {
        String userAccount = "mono";
        String userPassword = "";
        String checkPassword = "123456";
        try {
            long result = userService.userRegister(userAccount, userPassword, checkPassword);
            Assertions.assertEquals(-1, result);
            userAccount = "mono";
            result = userService.userRegister(userAccount, userPassword, checkPassword);
            Assertions.assertEquals(-1, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void RedisTest() {
        String key = RedisConstant.getUserSignInRedisKey(2024, 1);
        RBitSet signInBitSet = redissonClient.getBitSet(key);
        System.out.println(signInBitSet.remainTimeToLive());
    }
}
