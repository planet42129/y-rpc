package com.yhh.loadbalancer;

import com.yhh.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 负载均衡器接口
 * @author hyh
 * @date 2024/5/24
 */
public interface LoadBalancer {

    /**
     * 选择调用服务实现类
     * @param requestParams 请求参数
     * @param serviceMetaInfoList 可用的服务实现类列表
     * @return
     */
    ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
