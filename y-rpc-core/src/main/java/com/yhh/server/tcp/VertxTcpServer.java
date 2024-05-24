package com.yhh.server.tcp;

import com.yhh.server.HttpServer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author hyh
 * @date 2024/5/23
 */
@Slf4j
public class VertxTcpServer implements HttpServer {

    /**
     * 编写处理请求的逻辑，构造响应数据并返回
     *
     * @param requestData
     * @return
     */
    private byte[] handleRequest(byte[] requestData) {
        return "Hello,Client!".getBytes(StandardCharsets.UTF_8);
    }

    /**
     * @param port 监听端口
     */
    @Override
    public void doStart(int port) {
        //创建vertx实例
        Vertx vertx = Vertx.vertx();
        //创建TCP服务器
        NetServer server = vertx.createNetServer();
        //处理请求
        /**
         * 1 在网络编程中，服务端监听某个端口，等待客户端的请求；
         * 2 一旦接收到客户端的请求，创建一个socket实例，同时服务端创建
         *   一个新线程，去处理这个请求;
         * (创建socket接收客户端的请求后，从socket从获取请求，写入响应；)
         */
        server.connectHandler(socket -> {
            RecordParser parser = RecordParser.newFixed(8);//先读取消息头
            parser.setOutput(new Handler<Buffer>() {
                //初始化size
                int size = -1;
                //完整读取头+体
                Buffer resultBuffer = Buffer.buffer();//初始化结果

                @Override
                public void handle(Buffer buffer) {
                    if (size == -1) {
                        //从消息头中读取消息体的长度
                        size = buffer.getInt(4);//这里的buffer是读取到的消息头
                        parser.fixedSizeMode(size);
                        //将消息头写入结果
                        resultBuffer.appendBuffer(buffer);
                    } else {
                        //将消息体写入结果
                        resultBuffer.appendBuffer(buffer);
                        log.info(resultBuffer.toString());

                        //重置，继续下一轮
                        size = -1;
                        parser.fixedSizeMode(8);
                        resultBuffer = Buffer.buffer();//初始化
                    }
                }
            });
            socket.handler(parser);
        });

        //启动TCP服务器并监听指定端口
        server.listen(port, res -> {
            if (res.succeeded()) {
                log.info("TCP Server started on port {}", port);
            } else {
                log.info("Failed to start TCP Server, caused by", res.cause());
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}
