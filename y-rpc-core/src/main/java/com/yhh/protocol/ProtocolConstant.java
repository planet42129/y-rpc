package com.yhh.protocol;

/**
 * 协议常量
 * @author hyh
 * @date 2024/5/23
 */
public interface ProtocolConstant {
    //默认是静态常量，定义时必须初始化

    /**
     * 消息头的长度
     */
    int MESSAGE_HEADER_LENGTH = 17;

    /**
     * 协议魔数
     */
    byte PROTOCOL_MAGIC = 0x1;

    /**
     * 协议版本号
     */
    byte PROTOCOL_VERSION = 0x1;
}
