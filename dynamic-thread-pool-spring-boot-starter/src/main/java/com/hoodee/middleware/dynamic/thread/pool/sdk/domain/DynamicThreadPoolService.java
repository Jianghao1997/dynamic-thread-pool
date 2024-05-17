package com.hoodee.middleware.dynamic.thread.pool.sdk.domain;

import com.alibaba.fastjson2.JSON;
import com.hoodee.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author jh
 * @Description
 * @Date 2024/5/17
 */
public class DynamicThreadPoolService implements IDynamicThreadPoolService{

    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolService.class);

    private final String applicationName;

    private final Map<String, ThreadPoolExecutor> threadPoolExecutorMap;


    public DynamicThreadPoolService( String applicationName, Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        this.applicationName = applicationName;
        this.threadPoolExecutorMap = threadPoolExecutorMap;
    }

    @Override
    public List<ThreadPoolConfigEntity> queryThreadPoolList() {
        Set<String> threadPoolBeanNames = threadPoolExecutorMap.keySet();
        List<ThreadPoolConfigEntity> threadPoolConfigEntities = new ArrayList<>(threadPoolBeanNames.size());
        for (String threadPoolBeanName : threadPoolBeanNames) {
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolBeanName);
            if (threadPoolExecutor != null) {
                threadPoolConfigEntities.add(this.buildThreadPoolConfigEntity(threadPoolBeanName, threadPoolExecutor));
            }
        }
        return threadPoolConfigEntities;
    }

    @Override
    public ThreadPoolConfigEntity queryThreadPoolConfig(String threadPoolName) {
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolName);
        if (threadPoolExecutor == null) {
            return new ThreadPoolConfigEntity(applicationName, threadPoolName);
        }
        ThreadPoolConfigEntity threadPoolConfigEntity = this.buildThreadPoolConfigEntity(threadPoolName, threadPoolExecutor);
        if (logger.isDebugEnabled()) {
            logger.info("dynamic thread pool config => application name: [{}], thread name: [{}] threadPoolConfigEntity:[{}]",
                    applicationName, threadPoolName, JSON.toJSONString(threadPoolConfigEntity));
        }
        return threadPoolConfigEntity;
    }

    @Override
    public void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity) {
        if (threadPoolConfigEntity == null || !applicationName.equals(threadPoolConfigEntity.getAppName())) {
            return;
        }
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolConfigEntity.getThreadPoolName());
        if (threadPoolExecutor == null) {
            return;
        }
        // 更新线程池配置，这里只更新核心线程数和最大线程数
        threadPoolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
        threadPoolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());
    }

    private ThreadPoolConfigEntity buildThreadPoolConfigEntity(String threadPoolName, ThreadPoolExecutor threadPoolExecutor) {
        ThreadPoolConfigEntity threadPoolConfigEntity = new ThreadPoolConfigEntity(applicationName, threadPoolName);
        threadPoolConfigEntity.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
        threadPoolConfigEntity.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
        threadPoolConfigEntity.setPoolSize(threadPoolExecutor.getPoolSize());
        threadPoolConfigEntity.setActiveCount(threadPoolExecutor.getActiveCount());
        threadPoolConfigEntity.setQueueSize(threadPoolExecutor.getQueue().size());
        threadPoolConfigEntity.setRemainingCapacity(threadPoolExecutor.getQueue().remainingCapacity());
        threadPoolConfigEntity.setQueueType(threadPoolExecutor.getQueue().getClass().getSimpleName());
        return threadPoolConfigEntity;
    }
}
