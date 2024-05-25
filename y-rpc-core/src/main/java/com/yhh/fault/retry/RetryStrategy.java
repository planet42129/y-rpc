package com.yhh.fault.retry;

import com.yhh.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 重试策略接口
 * @author hyh
 * @date 2024/5/24
 */
public interface RetryStrategy {
    /**
     * 重试
     * @param callable
     * @return
     * @throws Exception
     */

    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
