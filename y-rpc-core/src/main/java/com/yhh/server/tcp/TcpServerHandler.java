package com.yhh.server.tcp;

import com.yhh.RpcApplication;
import com.yhh.model.RpcRequest;
import com.yhh.model.RpcResponse;
import com.yhh.protocol.ProtocolMessage;
import com.yhh.protocol.ProtocolMessageDecoder;
import com.yhh.protocol.ProtocolMessageEncoder;
import com.yhh.protocol.ProtocolMessageTypeEnum;
import com.yhh.registry.LocalRegistry;
import com.yhh.serializer.Serializer;
import com.yhh.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 1、从TCP的socket请求中获取协议消息对象，解码器先解码，从消息体中获取RpcRequest对象
 * 2、根据服务名称从本地注册器中获取到对应的服务实现类
 * 3、通过反射机制调用方法，得到返回结果
 * 4、对返回结果进行封装，编码器编码，写入到TCP的socket响应中
 * 注意：
 * a 序列化和反序列化都在编码器和解码器中执行
 * b 在编码前，需要从请求中获取序列化协议，存入响应的header中，确保发送方和接收方使用的序列化协议相同
 * c 解码传入的参数为Buffer
 * d 编码传入的参数为ProtocolMessage（header和body）
 *
 * @author hyh
 * @date 2024/5/23
 */
@Slf4j
public class TcpServerHandler implements Handler<NetSocket> {


    @Override
    public void handle(NetSocket netSocket) {
        TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            //接收请求，解码
            ProtocolMessage<RpcRequest> requestProtocolMessage;
            try {
                requestProtocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException("protocol message decode error", e);
            }
            RpcRequest rpcRequest = requestProtocolMessage.getBody();
            //处理请求
            //构造响应结构对象
            RpcResponse rpcResponse = new RpcResponse();
            //通过反射获取要调用的服务的实现类

            try {
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Constructor<?> constructor = implClass.getConstructor();
                Object result = method.invoke(constructor.newInstance(), rpcRequest.getArgs());

                //封装返回结果
                rpcResponse.setMessage("ok");
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }

            //发送响应，编码
            ProtocolMessage.Header header = requestProtocolMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
            Buffer encode = null;
            try {
                encode = ProtocolMessageEncoder.encode(responseProtocolMessage);
                netSocket.write(encode);
            } catch (IOException e) {
                throw new RuntimeException("protocol message encode error", e);
            }
        });
        //处理连接
        netSocket.handler(tcpBufferHandlerWrapper);
    }
}
