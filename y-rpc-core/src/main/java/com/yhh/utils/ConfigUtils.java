package com.yhh.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

/**
 * 读取配置文件并返回配置对象，简化调用
 *
 * @author hyh
 * @date 2024/5/11
 */
public class ConfigUtils {
    private ConfigUtils() {
    }
    /**
     * 加载配置对象
     *
     * @param tClass
     * @param prefix
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }

    /**
     * 加载配置对象，支持区分环境
     * @param tClass 要转换为的配置对象的类型，如RpcConfig、RegistryConfig
     * @param prefix
     * @param environment 区分环境 ，默认是application.properties，加入该变量变为application-environment.properties
     * @return
     * @param <T>
     */
    private static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }
        configFileBuilder.append(".properties");//application.properties
        Props props = new Props(configFileBuilder.toString());
        return props.toBean(tClass, prefix);
    }
}
