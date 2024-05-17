package com.hoodee.middleware.dynamic.thread.pool.sdk.trigger;

import com.alibaba.fastjson2.JSON;
import com.hoodee.middleware.dynamic.thread.pool.sdk.domain.IDynamicThreadPoolService;
import com.hoodee.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.hoodee.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * @author jh
 * @Description 线程池数据上报任务
 * @Date 2024/5/17
 */
public class ThreadPoolDataReportJob {

    private final Logger logger = LoggerFactory.getLogger(ThreadPoolDataReportJob.class);

    private final IDynamicThreadPoolService dynamicThreadPoolService;

    private final IRegistry registry;

    public ThreadPoolDataReportJob(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry registry) {
        this.dynamicThreadPoolService = dynamicThreadPoolService;
        this.registry = registry;
    }

    @Scheduled(cron = "0/20 * * * * ?")
    public void executeReportThreadPoolList() {
        List<ThreadPoolConfigEntity> threadPoolConfigEntities = dynamicThreadPoolService.queryThreadPoolList();
        registry.reportThreadPool(threadPoolConfigEntities);
        logger.info("reportThreadPool success threadPoolConfigEntities:{}",
                JSON.toJSONString(threadPoolConfigEntities)
        );
        for (ThreadPoolConfigEntity threadPoolConfigEntity : threadPoolConfigEntities) {
            registry.reportThreadPoolConfigParameter(threadPoolConfigEntity);
            logger.info("reportThreadPoolConfigParameter success, threadPoolConfigEntity:{}",
                    JSON.toJSONString(threadPoolConfigEntity)
            );
        }
    }

}
