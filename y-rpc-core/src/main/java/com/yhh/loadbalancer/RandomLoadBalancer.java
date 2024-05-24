package com.yhh.loadbalancer;

import com.yhh.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 随机-负载均衡器
 * @author hyh
 * @date 2024/5/24
 */
public class RandomLoadBalancer implements LoadBalancer{

    private Random random = new Random();

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        int size = serviceMetaInfoList.size();
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            return serviceMetaInfoList.get(0);
        }
        return serviceMetaInfoList.get(random.nextInt(size));
        //nextInt()方法会返回一个大于等于0且小于size的随机整数。
    }
}
