package com.yhh.protocol;

import com.yhh.model.RpcRequest;
import com.yhh.model.RpcResponse;
import com.yhh.serializer.Serializer;
import com.yhh.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 1 从Buffer中依次读取指定位置的信息，填充到ProtocolMessage实例对象中并返回
 * 特别注意：一定要根据编码器中消息头和消息头放入Buffer中的顺序依次读取
 * 2 从消息头中读取序列化协议，获取到序列化器，使用序列化器反序列化字节数组
 *   并从消息头中得到消息类型（请求、响应、心跳检测、其他类型），
 *   最后new 一个ProtocolMessage并将其返回
 * @author hyh
 * @date 2024/5/23
 */
public class ProtocolMessageDecoder {
    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        //分别从指定位置依次读出Buffer，必须是指定位置
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        byte magic = buffer.getByte(0);
        if (magic != ProtocolConstant.PROTOCOL_MAGIC) {
            throw new RuntimeException("message magic is not valid.");
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));

        //获取消息体
        //解决粘包问题，只读取指定长度的数据
        byte[] bodyBytes = buffer.getBytes(17, 17 + header.getBodyLength());
        //解析消息体
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("serialize protocol is not exist.");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        if (messageTypeEnum == null) {
            throw new RuntimeException("serialize message type is not exist.");
        }
        switch (messageTypeEnum) {
            case REQUEST:
                RpcRequest request = serializer.deserialize(bodyBytes, RpcRequest.class);
                return new ProtocolMessage<>(header, request);
            case RESPONSE:
                RpcResponse response = serializer.deserialize(bodyBytes, RpcResponse.class);
                return new ProtocolMessage<>(header, response);
            case HEART_BEAT:
                //todo 消息类型为心跳检测
            case OTHERS:
            default:
                throw  new RuntimeException("not support such message type.");
        }
    }
}
