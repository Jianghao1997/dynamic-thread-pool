package com.hoodee.middleware.dynamic.thread.pool.sdk.config;

import com.hoodee.middleware.dynamic.thread.pool.sdk.domain.DynamicThreadPoolService;
import com.hoodee.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import com.hoodee.middleware.dynamic.thread.pool.sdk.registry.redis.RedisRegistry;
import com.hoodee.middleware.dynamic.thread.pool.sdk.trigger.ThreadPoolDataReportJob;
import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author jh
 * @Description 动态配置入口
 * @Date 2024/5/17
 */
@Configuration
@EnableConfigurationProperties(DynamicThreadPoolAutoProperties.class)
@EnableScheduling
public class DynamicThreadPoolAutoConfig {

    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolAutoConfig.class);

    @Bean("redissonClient")
    public RedissonClient redissonClient(DynamicThreadPoolAutoProperties properties) {
        Config config = new Config();
        // 根据需要可以设定编解码器；https://github.com/redisson/redisson/wiki/4.-%E6%95%B0%E6%8D%AE%E5%BA%8F%E5%88%97%E5%8C%96
        config.setCodec(JsonJacksonCodec.INSTANCE);
        config.useSingleServer()
                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                .setPassword(properties.getPassword())
                .setConnectionPoolSize(properties.getPoolSize())
                .setConnectionMinimumIdleSize(properties.getMinIdleSize())
                .setIdleConnectionTimeout(properties.getIdleTimeout())
                .setConnectTimeout(properties.getConnectTimeout())
                .setRetryAttempts(properties.getRetryAttempts())
                .setRetryInterval(properties.getRetryInterval())
                .setPingConnectionInterval(properties.getPingInterval())
                .setKeepAlive(properties.isKeepAlive());
        RedissonClient redissonClient = Redisson.create(config);
        logger.info("dynamic thread pool => register（redis）config complete。[{}] [{}] [{}]",
                properties.getHost(), properties.getPoolSize(), !redissonClient.isShutdown()
        );
        return redissonClient;
    }

    @Bean
    public IRegistry redisRegistry(RedissonClient redissonClient) {
        return new RedisRegistry(redissonClient);
    }

    @Bean("dynamicThreadPoolService")
    public DynamicThreadPoolService dynamicThreadPoolService(ApplicationContext applicationContext,
                                                             Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        String applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        String active = applicationContext.getEnvironment().getProperty("spring.profiles.active");
        if (StringUtils.isBlank(applicationName)) {
            logger.warn("application info => applicationName is null");
        }
        logger.info("application info => applicationName:[{}], active:[{}]", applicationName, active);
        return new DynamicThreadPoolService(applicationName, threadPoolExecutorMap);
    }

    @Bean
    public ThreadPoolDataReportJob threadPoolDataReportJob(DynamicThreadPoolService dynamicThreadPoolService,
                                                           IRegistry registry) {
        return new ThreadPoolDataReportJob(dynamicThreadPoolService, registry);
    }
}
