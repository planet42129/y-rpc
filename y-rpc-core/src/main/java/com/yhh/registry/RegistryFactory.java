package com.yhh.registry;

import com.yhh.services.SpiLoader;

/**
 * 注册中心工厂类——用于获取注册中心对象实例
 * @author hyh
 * @date 2024/5/14
 */
public class RegistryFactory {
    // SPI 动态加载
    static {
        SpiLoader.load(Registry.class);
    }

    /**
     * 默认注册中心
     */
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();


    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static Registry getInstance(String key) {
        return SpiLoader.getInstance(Registry.class, key);
    }

}
