package com.yhh.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.yhh.model.RpcRequest;
import com.yhh.model.RpcResponse;
import com.yhh.serializer.JdkSerializer;
import com.yhh.serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 服务代理（JDK动态代理）
 *
 * @author hyh
 * @date 2024/5/11
 */
public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //创建序列化器
        Serializer serializer = new JdkSerializer();
        //创建rpcRequest对象
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        //发请求
        try {
            //将rpc请求对象序列化，方便进行网络传输
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            //使用hutool工具构造http请求，获得http响应
            //todo 这里的请求地址被硬编码了，需要使用注册中心和服务发现机制解决
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes)
                    .execute()) {
                result = httpResponse.bodyBytes();
            }
            //将获取到的http响应反序列化为RpcResponse对象
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return rpcResponse.getData();//从RpcResponse对象中获取方法调用的结果
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
