package com.yhh.loadbalancer;

import com.yhh.serializer.JdkSerializer;
import com.yhh.serializer.Serializer;
import com.yhh.services.SpiLoader;

/**
 * 负载均衡器工厂（工厂模式） + 单例模式
 *
 * @author hyh
 * @date 2024/5/24
 */
public class LoadBalancerFactory {

    /**
     * 使用静态代码块，在工厂首次加载时，就会调用SpiLoader的load方法加载序列化器接口的所有实现类，之后
     * 就可以通过调用getInstance方法获取指定的实现类对象了。
     */
    static {
        SpiLoader.load(LoadBalancer.class);
    }

    /**
     * 默认
     */
    private static final LoadBalancer DEFAULT_LOADBALANCER = new RoundRobinLoadBalancer();

    /**
     * 获取实例
     */
    public static LoadBalancer getInstance(String key) {
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }

    private LoadBalancerFactory() {
    }
}
