package com.yhh.provider;

import com.yhh.common.service.UserService;
import com.yhh.registry.LocalRegistry;
import com.yhh.server.HttpServer;
import com.yhh.server.VertxHttpServer;

/**
 * 服务提供者是真正实现了接口的模块
 * 简易服务提供者实例
 */
public class EasyProviderExample {
    public static void main(String[] args) {
        //服务提供者启动时， 需要将提供的服务注册到注册器中
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        //启动web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);


    }
}
