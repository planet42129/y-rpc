package com.yhh.fault.tolerant;

import com.yhh.model.RpcResponse;

import java.util.Map;

/**
 * 快速失败
 * 遇到异常后，将异常再次抛出，交给外层处理
 *
 * @author hyh
 * @date 2024/5/24
 */
public class FailFastTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
       throw new RuntimeException("服务报错", e);
    }
}
