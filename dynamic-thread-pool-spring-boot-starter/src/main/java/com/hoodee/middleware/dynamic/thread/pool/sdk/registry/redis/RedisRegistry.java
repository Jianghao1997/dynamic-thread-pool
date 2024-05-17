package com.hoodee.middleware.dynamic.thread.pool.sdk.registry.redis;

import com.hoodee.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.hoodee.middleware.dynamic.thread.pool.sdk.domain.model.valobj.RegistryEnumVO;
import com.hoodee.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.List;

/**
 * @author jh
 * @Description
 * @Date 2024/5/17
 */
public class RedisRegistry implements IRegistry {

    private final RedissonClient redissonClient;

    public RedisRegistry(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 线程池配置上报
     *
     * @param threadPoolConfigEntities 线程池配置实体对象集合
     */
    @Override
    public void reportThreadPool(List<ThreadPoolConfigEntity> threadPoolConfigEntities) {
        RList<ThreadPoolConfigEntity> list = redissonClient.getList(RegistryEnumVO.THREAD_POOL_CONFIG_LIST_KEY.getKey());
        list.delete();
        list.addAll(threadPoolConfigEntities);
    }

    /**
     * 线程池配置参数上报
     *
     * @param threadPoolConfigEntity 线程池配置实体对象
     */
    @Override
    public void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity) {
        String cacheKey = RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_" + threadPoolConfigEntity.getAppName() + "_" + threadPoolConfigEntity.getThreadPoolName();
        RBucket<ThreadPoolConfigEntity> bucket = redissonClient.getBucket(cacheKey);
        bucket.set(threadPoolConfigEntity, Duration.ofDays(30));
    }
}
