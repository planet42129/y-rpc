package com.yhh.server;

import com.yhh.RpcApplication;
import com.yhh.model.RpcRequest;
import com.yhh.model.RpcResponse;
import com.yhh.registry.LocalRegistry;
import com.yhh.serializer.Serializer;
import com.yhh.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 1、反序列化请求为Java对象，从对象中获取参数
 * 2、根据服务名称从本地注册器中获取到对应的服务实现类
 * 3、通过反射机制调用方法，得到返回结果
 * 4、对返回结果进行封装和序列化，写入到响应中
 *
 * @author hyh
 * @date 2024/5/11
 */
@Slf4j
public class HttpServerHandler implements Handler<HttpServerRequest> {


    @Override
    public void handle(HttpServerRequest request) {
        //指定序列化器
        final Serializer serializer =
                SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        //记录日志
        log.info("Received request: " + request.method() + " " + request.uri());

        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //响应结果对象
            RpcResponse rpcResponse = new RpcResponse();
            //如果请求为空，直接返回
            if (rpcRequest == null) {
                rpcResponse.setMessage("rpcRequest is null");
                doResponse(request, rpcResponse, serializer);
                return;
            }
            //否则继续解析rpcRequest

            try {
                //通过Java反射机制调用服务实现类的方法，并得到调用结果
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Constructor<?> constructor = implClass.getConstructor();
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                //bug: 直接通过implClass.newInstance()方法获取实现类的实例对象，debug的时候报错implClass为null
                // 解决：先通过Class实例获取构造器对象，再通过构造器对象创建实例constructor.newInstance()
                Object invokeResult = method.invoke(constructor.newInstance(), rpcRequest.getArgs());
                //封装返回结果rpcResponse
                rpcResponse.setData(invokeResult);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            //做出响应：序列化rpcResponse
            doResponse(request, rpcResponse, serializer);


        });
    }

    /**
     * 响应
     *
     * @param request
     * @param rpcResponse
     * @param serializer
     */

    private void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type", "application/json");
        //序列化
        try {
            byte[] bytes = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
