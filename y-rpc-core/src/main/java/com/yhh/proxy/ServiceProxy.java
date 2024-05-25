package com.yhh.proxy;

import cn.hutool.core.collection.CollUtil;
import com.yhh.RpcApplication;
import com.yhh.config.RpcConfig;
import com.yhh.constant.RpcConstant;
import com.yhh.fault.retry.RetryStrategy;
import com.yhh.fault.retry.RetryStrategyFactory;
import com.yhh.fault.tolerant.TolerantStrategy;
import com.yhh.fault.tolerant.TolerantStrategyFactory;
import com.yhh.loadbalancer.LoadBalancer;
import com.yhh.loadbalancer.LoadBalancerFactory;
import com.yhh.model.RpcRequest;
import com.yhh.model.RpcResponse;
import com.yhh.model.ServiceMetaInfo;
import com.yhh.registry.Registry;
import com.yhh.registry.RegistryFactory;
import com.yhh.server.tcp.VertxTcpClient;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

/**
 * 服务代理（JDK动态代理）
 * 替服务消费者构造rpcRequest对象、序列化rpcRequest对象、发送请求、
 * 反序列化响应、得到rpcResponse对象，获取方法调用的结果
 *
 * @author hyh
 * @date 2024/5/11
 */
@Slf4j
public class ServiceProxy implements InvocationHandler {

    /**
     * 基于TCP网络传输的动态代理
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String serviceName = method.getDeclaringClass().getName();

        //创建rpcRequest对象
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        //发请求
        //从注册中心获取服务提供者请求地址
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            //todo 换成自定义异常
            throw new RuntimeException("no service_address");
        }
        //调用负载均衡器获取服务实现类节点

        //获取负载均衡器
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
        //将调用的方法名称（请求路径）作为负载均衡参数
        HashMap<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", rpcRequest.getMethodName());
        ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);

        //使用重试机制 发送rpc请求
        RpcResponse rpcResponse;
        try{
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            rpcResponse = retryStrategy.doRetry(()->
                    VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
            );
        } catch (Exception e) {
            //容错机制
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
            rpcResponse = tolerantStrategy.doTolerant(null, e);
        }
        return rpcResponse.getData();
    }


    /**
     * 基于Http网络传输的动态代理
     */
/*    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String serviceName = method.getDeclaringClass().getName();

        //创建rpcRequest对象
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        //发请求
        try {
            //创建序列化器
            Serializer serializer =
                    SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

            //将rpc请求对象序列化，方便进行网络传输
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;

            //从注册中心获取服务提供者请求地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                //todo 换成自定义异常
                throw new RuntimeException("no service_address");
            }
            // todo 改成可以轮询的地址？
            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);

            //使用hutool工具构造http请求，获得http响应
            //todo 这里的请求地址被硬编码了，需要使用注册中心和服务发现机制解决
            try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
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
    }*/
}
