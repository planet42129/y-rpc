package com.yhh.model;

import cn.hutool.core.util.StrUtil;
import com.yhh.constant.RpcConstant;
import lombok.Data;

/**
 * 服务元信息（注册信息）
 *
 * @author hyh
 * @date 2024/5/14
 */
@Data
public class ServiceMetaInfo {
    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务版本号
     */
    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;

    /**
     * 服务域名
     */
    private String serviceHost;

    /**
     * 服务端口号
     */
    private Integer servicePort;

    /**
     * 服务分组（暂未实现）
     */
    private String serviceGroup = "default";

    /**
     * 在注册中心使用：获取服务键名（key）
     *
     * @return
     */
    public String getServiceKey() {
        //UserService:1.0
        // 后续可扩展服务分组
//        return String.format("%s:%s:%s", serviceName, serviceVersion, serviceGroup);
        return String.format("%s:%s", serviceName, serviceVersion);
    }

    /**
     * 在注册中心使用：获取服务注册节点键名（key）
     *
     * @return
     */
    public String getServiceNodeKey() {
        //   UserService:1.0/http:101.21.34.1:8081
        return String.format("%s/%s:%s", getServiceKey(), serviceHost, servicePort);
    }

    /**
     * 在服务消费者使用：获取完整服务地址
     *
     * @return
     */
    public String getServiceAddress() {
        if (!StrUtil.contains(serviceHost, "http")) {
            return String.format("http://%s:%s", serviceHost, servicePort);
        }
        return String.format("%s:%s", serviceHost, servicePort);
    }
}
