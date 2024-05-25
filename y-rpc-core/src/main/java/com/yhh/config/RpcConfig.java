package com.yhh.config;

import com.yhh.fault.retry.RetryStrategyKeys;
import com.yhh.fault.tolerant.TolerantStrategyKeys;
import com.yhh.loadbalancer.LoadBalancer;
import com.yhh.loadbalancer.LoadBalancerKeys;
import com.yhh.serializer.Serializer;
import com.yhh.serializer.SerializerKeys;
import lombok.Data;

/**
 * serverHost和serverPort属性指的是RPC服务自身的地址和端口。
 * 在RPC框架中，这些配置用于指定RPC服务提供者（Server）监听的网络地址（通常是IP地址或域名）和端口号。
 * 当RPC客户端想要调用远程服务时，它会连接到这个指定的serverHost和serverPort来通信。
 * 这里的默认配置是服务运行在本地主机（localhost或127.0.0.1）的8080端口上。
 * todo 上述说法存疑？
 * @author hyh
 * @date 2024/5/11
 */
@Data
public class RpcConfig {
    /**
     * 名称
     */
    private String name = "y-rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";
    /**
     * 服务器端口号
     */
    private Integer serverPort = 8080;

    /**
     * 模拟调用
     */
    private boolean mock = false;

    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();

    /**
     * 负载均衡器
     */
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     *
     */
    private String retryStrategy = RetryStrategyKeys.NO;

    /**
     * 容错策略
     */
    private String tolerantStrategy = TolerantStrategyKeys.FAIL_FAST;

}
