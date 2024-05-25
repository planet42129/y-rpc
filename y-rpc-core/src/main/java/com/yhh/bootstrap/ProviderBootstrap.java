package com.yhh.bootstrap;

import com.yhh.RpcApplication;
import com.yhh.config.RegistryConfig;
import com.yhh.config.RpcConfig;
import com.yhh.model.ServiceMetaInfo;
import com.yhh.model.ServiceRegisterInfo;
import com.yhh.registry.LocalRegistry;
import com.yhh.registry.Registry;
import com.yhh.registry.RegistryFactory;
import com.yhh.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * @author hyh
 * @date 2024/5/24
 */
public class ProviderBootstrap {

    public static void init(List<ServiceRegisterInfo> serviceRegisterInfoList) {
        //RPC框架初始化
        RpcApplication.init();
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            //本地注册
            String serviceName = serviceRegisterInfo.getServiceName();
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());

            //注册服务到注册中心
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
            //启动TCP服务器
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(8080);
        }

    }
}
