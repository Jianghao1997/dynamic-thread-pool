package com.hoodee.middleware.dynamic.thread.pool.sdk.config;

import com.hoodee.middleware.dynamic.thread.pool.sdk.domain.DynamicThreadPoolService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author jh
 * @Description 动态配置入口
 * @Date 2024/5/17
 */
@Configuration
public class DynamicThreadPoolAutoConfig {

    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolAutoConfig.class);

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
}
