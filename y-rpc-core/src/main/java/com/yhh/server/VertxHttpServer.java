package com.yhh.server;

import io.vertx.core.Vertx;

/**
 * https://vertx.io/get-started/
 * @author hyh
 * @date 2024/5/11
 */
public class VertxHttpServer implements HttpServer {
    @Override
    public void doStart(int port) {
        //创建vertx实例
        Vertx vertx = Vertx.vertx();
        //创建HTTP服务器
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();
        //http服务器如何处理请求，响应
        server.requestHandler(new HttpServerHandler());
        //启动HTTP服务器，并指定监听端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("Server is listening on port: " + port);
            } else {
                System.out.println("Failed to start server: " + result.cause());
            }
        });
    }
}
