package com.yhh.server;

/**
 * HTTP服务器接口
 *
 * @author hyh
 * @date 2024/5/11
 */
public interface HttpServer {
    /**
     * 启动服务器
     * @param port
     */
    void doStart(int port);
}
