package com.yhh.serializer;

import com.yhh.services.SpiLoader;

/**
 * 序列化器工厂（工厂模式，用于获取序列化器对象） + 单例模式
 *
 * @author hyh
 * @date 2024/5/13
 */
public class SerializerFactory {

    /**
     * 使用静态代码块，在工厂首次加载时，就会调用SpiLoader的load方法加载序列化器接口的所有实现类，之后
     * 就可以通过调用getInstance方法获取指定的实现类对象了。
     */
    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     * 获取实例
     */
    public static Serializer getInstance(String key) {
        return SpiLoader.getInstance(Serializer.class, key);
    }

    private SerializerFactory() {
    }
}
