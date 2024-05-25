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
public class ConsumerBootstrap {

    public static void init() {
        //RPC框架初始化
        RpcApplication.init();

    }

}

