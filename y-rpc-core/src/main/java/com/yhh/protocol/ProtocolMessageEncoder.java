package com.yhh.protocol;


import com.yhh.serializer.Serializer;
import com.yhh.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * @author hyh
 * @date 2024/5/23
 */
public class ProtocolMessageEncoder {


    /**
     * 依次向缓存区写入字节（header），
     * 同时从消息头中获取所使用的序列化协议，
     * 获取序列化器，对消息体进行序列化
     * @param protocolMessage 消息结构：包括消息头和消息体
     * @return 返回一个Buffer类型的数据，Buffer是import io.vertx.core.buffer.Buffer;
     * @throws IOException 序列化消息体时可能会发生IO异常
     */
    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException {
        if (protocolMessage == null || protocolMessage.getHeader() == null) {
            return Buffer.buffer();
        }
        ProtocolMessage.Header header = protocolMessage.getHeader();
        //依次向缓冲区中写入字节
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw  new RuntimeException("serialized protocol is not exist");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        //序列化消息体
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
        //向Buffer写入消息体的长度和消息体数据
        buffer.appendInt(bodyBytes.length);
        buffer.appendBytes(bodyBytes);
        return buffer;
    }
}
