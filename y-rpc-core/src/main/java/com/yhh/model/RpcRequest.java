package com.yhh.model;

import com.yhh.constant.RpcConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RPC请求
 * 封装调用接口实现方法所需的信息，如服务名称、方法名称、调用参数的类型列表、参数列表
 * 这些都是反射机制所需的参数
 * 对应反射中需要根据类对应的Class实例，调用其getDeclaredMethod(String paramName, Class paramClassName..)
 * https://www.liaoxuefeng.com/wiki/1252599548343744/1264803678201760
 *
 * @author hyh
 * @date 2024/5/11
 */
@Data//这个注解包括 getter、setter、toString、Equals和hashCode方法。。。
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 默认服务版本号
     */
    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;

    /**
     * 参数类型列表
     */
    private Class<?>[] parameterTypes;

    /**
     * 参数列表
     */
    private Object[] args;

}
