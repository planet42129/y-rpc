package com.yhh.registry;

import com.yhh.model.ServiceMetaInfo;
import java.util.Collections;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册中心服务的本地缓存（消费端使用）
 *
 * @author hyh
 * @date 2024/5/23
 */
public class RegistryServiceCache {

    /**
     * 服务节点信息的本地缓存
     */
    Map<String, List<ServiceMetaInfo>> cacheMap = new ConcurrentHashMap<>();

    public void writeCache(String key, List<ServiceMetaInfo> value) {
        cacheMap.put(key, value);
    }

    public List<ServiceMetaInfo> readCache(String key) {
        if (this.cacheMap == null) {
            return Collections.emptyList();
        }
        return cacheMap.get(key);
    }

    public void clearCache() {
        this.cacheMap.clear();
    }


}
