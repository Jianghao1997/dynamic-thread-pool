package com.hoodee.middleware.dynamic.thread.pool.sdk.trigger.listener;

import com.hoodee.middleware.dynamic.thread.pool.sdk.domain.IDynamicThreadPoolService;
import com.hoodee.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.hoodee.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author jh
 * @Description 动态线程池变更监听
 * @Date 2024/5/17
 */
public class ThreadPoolConfigAdjustListener implements MessageListener<ThreadPoolConfigEntity> {

    private final Logger logger = LoggerFactory.getLogger(ThreadPoolConfigAdjustListener.class);

    private final IDynamicThreadPoolService dynamicThreadPoolService;

    private final IRegistry registry;

    public ThreadPoolConfigAdjustListener(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry registry) {
        this.dynamicThreadPoolService = dynamicThreadPoolService;
        this.registry = registry;
    }

    @Override
    public void onMessage(CharSequence charSequence, ThreadPoolConfigEntity threadPoolConfigEntity) {
        logger.info("dynamic thread pool => receive message, thread name: [{}] core thread size: [{}] max thread size: [{}]",
                threadPoolConfigEntity.getThreadPoolName(), threadPoolConfigEntity.getCorePoolSize(), threadPoolConfigEntity.getMaximumPoolSize()
        );
        dynamicThreadPoolService.updateThreadPoolConfig(threadPoolConfigEntity);
        // 更新后的上报最新数据
        List<ThreadPoolConfigEntity> threadPoolConfigEntities = dynamicThreadPoolService.queryThreadPoolList();
        registry.reportThreadPool(threadPoolConfigEntities);
        ThreadPoolConfigEntity threadPoolConfigEntityCurrent = dynamicThreadPoolService.queryThreadPoolConfig(
                threadPoolConfigEntity.getThreadPoolName()
        );
        registry.reportThreadPoolConfigParameter(threadPoolConfigEntityCurrent);
        logger.info("dynamic thread pool => report thread pool config parameter success");
    }
}
