package com.hoodee.test;

import com.hoodee.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RTopic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author jh
 * @Description
 * @Date 2024/5/17
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Resource
    private RTopic dynamicThreadPoolRedisTopic;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor01;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor02;

    @Test
    public void test_dynamicThreadPoolRedisTopic() throws InterruptedException {
        ThreadPoolConfigEntity threadPoolConfigEntity = new ThreadPoolConfigEntity(
                "dynamic-thread-pool-test-app", "threadPoolExecutor01"
        );
        threadPoolConfigEntity.setCorePoolSize(100);
        threadPoolConfigEntity.setMaximumPoolSize(100);
        dynamicThreadPoolRedisTopic.publish(threadPoolConfigEntity);
        new CountDownLatch(1).await();
    }

    @Test
    public void testUseDynamicThreadPool() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            threadPoolExecutor01.execute(() -> log.info("threadPoolExecutor01 => [{}]", Thread.currentThread().getName()));
        }
    }
}
