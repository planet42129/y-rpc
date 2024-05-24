package com.yhh.server.tcp;

import com.yhh.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;


/**
 * 装饰者模式
 * 使用RecordParser对原有的Buffer处理能力进行增强
 * 解决半包粘包问题
 * 分两次读取
 * 第一次读取消息头，获取消息体的长度
 * 第二次读取消息体
 * @author hyh
 * @date 2024/5/24
 */
@Slf4j
public class TcpBufferHandlerWrapper implements Handler<Buffer> {
    private final RecordParser recordParser;

    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
        this.recordParser = initRecordParser(bufferHandler);
    }

    /**
     * 初始化RecordParser
     * 分两次读取固定长度的字节，并设置
     * @param bufferHandler
     * @return
     */
    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);
        parser.setOutput(new Handler<Buffer>() {
            //初始化size
            int bodyLength = -1;
            //临时结果，完整存储头+体
            Buffer currentBuffer = Buffer.buffer();//初始化临时结果

            @Override
            public void handle(Buffer buffer) {
                if (bodyLength == -1) {
                    //从消息头中读取消息体的长度
                    bodyLength = buffer.getInt(13);
                    parser.fixedSizeMode(bodyLength);
                    //将消息头写入临时结果
                    currentBuffer.appendBuffer(buffer);
                } else {
                    //将消息体写入临时结果
                    currentBuffer.appendBuffer(buffer);

                    if (currentBuffer.length() == bodyLength + ProtocolConstant.MESSAGE_HEADER_LENGTH) {
                        //处理临时结果
                        bufferHandler.handle(currentBuffer);

                        //重置，继续下一轮
                        bodyLength = -1;
                        parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                        currentBuffer = Buffer.buffer();//初始化
                    }
                }
            }
        });
        return parser;
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }
}
