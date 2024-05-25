package com.yhh.fault.tolerant;

import com.yhh.model.RpcResponse;

import java.util.Map;

/**
 * @author hyh
 * @date 2024/5/24
 */
public interface TolerantStrategy {

    /**
     * 容错
     * @param context 上下文
     * @param e 异常
     * @return
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e);


}
