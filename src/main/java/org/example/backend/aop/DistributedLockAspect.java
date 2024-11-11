package org.example.backend.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.backend.annotation.DistributedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class DistributedLockAspect {
    @Resource
    private RedissonClient redissonClient;

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, DistributedLock distributedLock) throws Exception {
        String lockKey = distributedLock.key();
        long waitTime = distributedLock.waitTime();
        long leaseTime = distributedLock.leaseTime();
        TimeUnit timeUnit = distributedLock.timeUnit();
        RLock rLock = redissonClient.getLock(lockKey);

        boolean acquired = false;
        try{
            acquired = rLock.tryLock(waitTime, leaseTime, timeUnit);
            if(acquired) {
                // 获得锁执行目标方法
                return proceedingJoinPoint.proceed();
            } else {
                throw new RuntimeException("Could not acquire lock:" + lockKey);
            }
        } catch (Throwable e) {
            throw new Exception(e);
        } finally {
            if (acquired) {
                rLock.unlock();
            }
        }
    }
}
