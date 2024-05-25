package com.yhh.fault.retry;

import com.yhh.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * @author hyh
 * @date 2024/5/24
 */
public class NoRetryStrategy implements RetryStrategy {

    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {

        return callable.call();
    }
}
