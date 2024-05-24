package com.yhh;

import com.yhh.config.RegistryConfig;
import com.yhh.config.RpcConfig;
import com.yhh.constant.RpcConstant;
import com.yhh.registry.Registry;
import com.yhh.registry.RegistryFactory;
import com.yhh.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC框架全局入口
 * 相当于 holder，存放了项目全局用到的变量
 * 双检锁单例模式实现
 */
@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;

    /**
     * 框架初始化，支持传入自定义配置
     *
     * @param newRpcConfig
     */
    public static void init(RpcConfig newRpcConfig) {
        // 1 给rpcConfig对象赋值，要么是从application.properties中读取的配置，要么是默认配置
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());
        // 2 注册中心初始化
        // 2.1 从rpcConfig中获取注册中心的配置
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        // 2.2 根据注册中心的配置获取注册中心实例
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        // 2.3 注册中心初始化
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig);

        //创建并注册ShutDown Hook，JVM退出时执行操作
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
        //todo kryo.close()

    }

    public static void init() {
        RpcConfig newRpcConfig = null;
        try {
            //读取application.properties配置文件中的以rpc为前缀的配置，
            //将读取到的rpc配置填充到一个RpcConfig对象，并返回
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            //如果新配置（application.properties中以rpc为前缀）加载失败，则使用默认配置
            newRpcConfig = new RpcConfig();
        }
        //根据newRpcConfig对象来初始化
        init(newRpcConfig);
    }

    //双检锁单例模式
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }

    private RpcApplication() {
    }
}
