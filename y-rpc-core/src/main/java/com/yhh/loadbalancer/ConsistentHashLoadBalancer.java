package com.yhh.loadbalancer;

import cn.hutool.core.util.HashUtil;
import com.yhh.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author hyh
 * @date 2024/5/24
 */
@Slf4j
public class ConsistentHashLoadBalancer implements LoadBalancer {
    /**
     * 一致性Hash环，存放虚拟节点
     * 节点的哈希值->节点信息
     */
    private final TreeMap<Long, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    /**
     * 虚拟节点数
     */
    private static final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }

        //构建虚拟节点环
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                long hashValue = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hashValue, serviceMetaInfo);
            }
        }

        /**
         * 调用请求的hash值
         */
        long hash = getHash(requestParams);

        //选择最接近且大于等于调用请求的hash值的虚拟节点
        //ceilingEntry方法用于返回大于等于指定键的最小键-值映射关系，如果没有找到大于等于指定键的键-值映射关系，则返回null。
        Map.Entry<Long, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);


        //如果没有大于等于调用请求的hash值的虚拟节点
        //则返回首部节点
        if (entry == null) {
            entry = virtualNodes.firstEntry();
        }
        return entry.getValue();
    }

    /**
     * Hash算法
     *
     * @param key
     * @return
     */
    public long getHash(Object key) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(String.valueOf(key.hashCode()).getBytes(StandardCharsets.UTF_8));
            return ((digest[0] & 0xFFL) << 24) | ((digest[1] & 0xFFL) << 16) |
                    ((digest[2] & 0xFFL) << 8) | (digest[3] & 0xFFL);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
//        return key.hashCode();
    }
}
/*
这段代码实现了一致性哈希负载均衡器的逻辑，但是还存在一些可以改进的地方：

1. 虚拟节点的构建：在select方法中每次调用时都会重新构建虚拟节点环，
这样可能会导致性能开销较大。可以考虑将虚拟节点环的构建放到初始化阶段，而不是每次调用select方法时重新构建。

2. 哈希算法：当前的哈希算法仅使用了key的hashCode作为哈希值，这种简单的哈希算法可能会导致哈希冲突较多，
影响负载均衡的效果。可以考虑使用更好的哈希算法，如MD5、SHA-1等，以减少哈希冲突的可能性。

3. 虚拟节点数量：当前代码中固定了虚拟节点数量为100，可以考虑将虚拟节点数量作为可配置参数，以便根据实际情况进行调整。

4. 错误处理：当前代码在虚拟节点为空时直接返回null，可以考虑增加日志记录或异常处理机制，以提高代码的健壮性。

通过对以上方面进行改进，可以使负载均衡器的性能和效果得到进一步提升。
 */