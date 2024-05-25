package com.yhh.fault.tolerant;

import com.yhh.fault.retry.NoRetryStrategy;
import com.yhh.fault.retry.RetryStrategy;
import com.yhh.services.SpiLoader;

/**
 * 容错策略工厂（工厂模式） + 单例模式
 *
 * @author hyh
 * @date 2024/5/24
 */
public class TolerantStrategyFactory {

    /**
     * 使用静态代码块，在工厂首次加载时，就会调用SpiLoader的load方法加载序列化器接口的所有实现类，之后
     * 就可以通过调用getInstance方法获取指定的实现类对象了。
     */
    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    /**
     * 默认
     */
    private static final TolerantStrategy DEFAULT_TOLERANTSTATEGY = new FailFastTolerantStrategy();

    /**
     * 获取实例
     */
    public static TolerantStrategy getInstance(String key) {
        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }

    private TolerantStrategyFactory() {
    }
}
