package com.yhh.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 协议的消息格式（结构） 类
 * ProtocolMessage为消息
 * 消息头为内部类
 * 消息体为泛型类型
 *
 * @author hyh
 * @date 2024/5/23
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T> {
    /**
     * 消息头
     */
    private Header header;

    /**
     * 消息体
     */
    private T body;


    /**
     * 消息头
     */
    @Data
    public static class Header{
        /**
         * 魔数，用于安全校验
         */
        private byte magic;
        /**
         * 版本号
         */
        private byte version;
        /**
         * 序列化器
         */
        private byte serializer;
        /**
         * 消息类型，判断是请求还是响应
         */
        private byte type;

        /**
         * 状态
         */
        private byte status;

        /**
         * 请求id
         */
        private long requestId;

        /**
         * 消息体的长度/传输的数据长度
         */
        private int bodyLength;

    }
}
