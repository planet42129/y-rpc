package com.yhh.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.yhh.RpcApplication;
import com.yhh.model.RpcRequest;
import com.yhh.model.RpcResponse;
import com.yhh.model.ServiceMetaInfo;
import com.yhh.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Vert TCP 请求客户端
 *
 * @author hyh
 * @date 2024/5/23
 */
@Slf4j
public class VertxTcpClient {

    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceAddress(),
                result -> {
                    if (!result.succeeded()) {
                        log.error("Failed to connect TCP Server");
                        return;
                    }

                    log.info("Successful connected to TCP Server");
                    NetSocket socket = result.result();
                    //发送数据，构造消息
                    ProtocolMessage<RpcRequest> requestProtocolMessage = new ProtocolMessage<>();
                    ProtocolMessage.Header header = new ProtocolMessage.Header();
                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    header.setSerializer((byte) ProtocolMessageSerializerEnum
                            .getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                    //全局请求id
                    header.setRequestId(IdUtil.getSnowflakeNextId());
                    requestProtocolMessage.setHeader(header);
                    requestProtocolMessage.setBody(rpcRequest);

                    //编码，发送请求
                    Buffer encodeBuffer = null;
                    try {
                        encodeBuffer = ProtocolMessageEncoder.encode(requestProtocolMessage);
                        socket.write(encodeBuffer);
                    } catch (IOException e) {
                        throw new RuntimeException("protocol message encode error", e);
                    }

                    TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
                        try {
                            ProtocolMessage<RpcResponse> decode =
                                    (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                        } catch (IOException e) {
                            throw new RuntimeException("protocol message decode error", e);
                        }
                    });
                    socket.handler(tcpBufferHandlerWrapper);
                });
        RpcResponse rpcResponse = responseFuture.get();
        //关闭连接
        netClient.close();
        return rpcResponse;
    }
}




/*    public void start() {
        Vertx vertx = Vertx.vertx();
        vertx.createNetClient().connect(8888, "localhost", result -> {
            if (result.succeeded()) {
                log.info("Connected to TCP Server");
                NetSocket socket = result.result();
                for (int i = 0; i < 1000; i++) {
                    socket.write("Hello, server!Hello, server!Hello, server!Hello, server!");
                }
                socket.handler(buffer -> {
                    log.info("Received response from server: {}", buffer.toString(StandardCharsets.UTF_8));
                });
            } else {
                log.info("Failed to connect TCP Server");
            }
        });

    }

    public static void main(String[] args) {
        new VertxTcpClient().start();
    }*/

