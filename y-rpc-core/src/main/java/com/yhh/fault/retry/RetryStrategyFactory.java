package com.yhh.fault.retry;

import com.yhh.loadbalancer.LoadBalancer;
import com.yhh.loadbalancer.RoundRobinLoadBalancer;
import com.yhh.services.SpiLoader;

/**
 * 重试策略工厂（工厂模式） + 单例模式
 *
 * @author hyh
 * @date 2024/5/24
 */
public class RetryStrategyFactory {

    /**
     * 使用静态代码块，在工厂首次加载时，就会调用SpiLoader的load方法加载序列化器接口的所有实现类，之后
     * 就可以通过调用getInstance方法获取指定的实现类对象了。
     */
    static {
        SpiLoader.load(RetryStrategy.class);
    }

    /**
     * 默认
     */
    private static final RetryStrategy DEFAULT_RETRYSTATEGY = new NoRetryStrategy();

    /**
     * 获取实例
     */
    public static RetryStrategy getInstance(String key) {
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }

    private RetryStrategyFactory() {
    }
}
