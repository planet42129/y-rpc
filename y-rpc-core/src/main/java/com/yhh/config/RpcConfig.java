package com.yhh.config;

import lombok.Data;

/**
 * @author hyh
 * @date 2024/5/11
 */
@Data
public class RpcConfig {
    /**
     * 名称
     */
    private String name = "y-rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";

    /**
     * 服务器端口号
     */
    private Integer serverPort = 8080;
}
