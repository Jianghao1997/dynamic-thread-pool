package com.hoodee.trigger;

import com.alibaba.fastjson2.JSON;
import com.hoodee.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.hoodee.types.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.hoodee.types.CommonResult.error;
import static com.hoodee.types.CommonResult.success;

/**
 * @author jh
 * @Description
 * @Date 2024/5/23
 */
@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/dynamic/thread/pool")
public class DynamicThreadPoolController {

    @Resource
    public RedissonClient redissonClient;

    /**
     * 查询线程池数据
     * curl --request GET \
     * --url 'http://localhost:8089/api/v1/dynamic/thread/pool/query_thread_pool_list'
     */
    @RequestMapping(value = "query_thread_pool_list", method = RequestMethod.GET)
    public CommonResult<List<ThreadPoolConfigEntity>> queryThreadPoolList() {
        try {
            RList<ThreadPoolConfigEntity> cacheList = redissonClient.getList("THREAD_POOL_CONFIG_LIST_KEY");
            return success(cacheList.readAll());
        } catch (Exception e) {
            log.error("查询线程池数据异常", e);
            return error("查询线程池数据异常");
        }
    }

    /**
     * 查询线程池配置
     * curl --request GET \
     * --url 'http://localhost:8089/api/v1/dynamic/thread/pool/query_thread_pool_config?appName=dynamic-thread-pool-test-app&threadPoolName=threadPoolExecutor'
     */
    @RequestMapping(value = "query_thread_pool_config", method = RequestMethod.GET)
    public CommonResult<ThreadPoolConfigEntity> queryThreadPoolConfig(@RequestParam String appName, @RequestParam String threadPoolName) {
        try {
            String cacheKey = "THREAD_POOL_CONFIG_PARAMETER_LIST_KEY" + "_" + appName + "_" + threadPoolName;
            ThreadPoolConfigEntity threadPoolConfigEntity = redissonClient.<ThreadPoolConfigEntity>getBucket(cacheKey).get();
            return success(threadPoolConfigEntity);
        } catch (Exception e) {
            log.error("查询线程池配置异常", e);
            return error("查询线程池配置异常");
        }
    }

    /**
     * 修改线程池配置
     * curl --request POST \
     * --url http://localhost:8089/api/v1/dynamic/thread/pool/update_thread_pool_config \
     * --header 'content-type: application/json' \
     * --data '{
     * "appName":"dynamic-thread-pool-test-app",
     * "threadPoolName": "threadPoolExecutor",
     * "corePoolSize": 1,
     * "maximumPoolSize": 10
     * }'
     */
    @RequestMapping(value = "update_thread_pool_config", method = RequestMethod.POST)
    public CommonResult<Boolean> updateThreadPoolConfig(@RequestBody ThreadPoolConfigEntity request) {
        try {
            log.info("修改线程池配置开始 {} {} {}", request.getAppName(), request.getThreadPoolName(), JSON.toJSONString(request));
            RTopic topic = redissonClient.getTopic("DYNAMIC_THREAD_POOL_REDIS_TOPIC" + "_" + request.getAppName());
            topic.publish(request);
            log.info("修改线程池配置完成 {} {}", request.getAppName(), request.getThreadPoolName());
            return success(true);
        } catch (Exception e) {
            log.error("修改线程池配置异常 {}", JSON.toJSONString(request), e);
            return error("修改线程池配置异常");
        }
    }
}
