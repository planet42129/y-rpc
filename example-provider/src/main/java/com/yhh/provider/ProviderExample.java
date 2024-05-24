package com.yhh.provider;

import com.yhh.RpcApplication;
import com.yhh.common.service.UserService;
import com.yhh.config.RegistryConfig;
import com.yhh.config.RpcConfig;
import com.yhh.model.ServiceMetaInfo;
import com.yhh.registry.LocalRegistry;
import com.yhh.registry.Registry;
import com.yhh.registry.RegistryFactory;
import com.yhh.server.HttpServer;
import com.yhh.server.VertxHttpServer;
import com.yhh.server.tcp.VertxTcpServer;

/**
 * @author hyh
 * @date 2024/5/13
 */
public class ProviderExample {
    public static void main(String[] args) {
        //RPC框架初始化
        RpcApplication.init();
        //注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);
        
        //注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


/*        //启动web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());*/
        //启动TCP服务器
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(8080);

    }
}
