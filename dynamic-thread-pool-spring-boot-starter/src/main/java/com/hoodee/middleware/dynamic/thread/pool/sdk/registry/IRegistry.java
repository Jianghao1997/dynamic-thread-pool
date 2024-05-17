package com.hoodee.middleware.dynamic.thread.pool.sdk.registry;

import com.hoodee.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * @author jh
 * @Description 注册中心接口
 * @Date 2024/5/17
 */
public interface IRegistry {

    /**
     * 线程池配置上报
     * @param threadPoolConfigEntities 线程池配置实体对象集合
     */
    void reportThreadPool(List<ThreadPoolConfigEntity> threadPoolConfigEntities);

    /**
     * 线程池配置参数上报
     * @param threadPoolConfigEntity 线程池配置实体对象
     */
    void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity);

}
