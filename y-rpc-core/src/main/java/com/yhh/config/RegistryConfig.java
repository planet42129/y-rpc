package com.yhh.config;

import com.yhh.registry.RegistryKeys;
import lombok.Data;

/**
 * 注册中心配置类
 * @author hyh
 * @date 2024/5/14
 */
@Data
public class RegistryConfig {
    /**
     * 注册中心类别
     */
    private String registry = RegistryKeys.ETCO;

    /**
     * 注册中心地址
     */
    private String address = "http://localhost:2379";

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 超时时间（单位毫秒）
     */
    private Long timeout = 10000L;
}
