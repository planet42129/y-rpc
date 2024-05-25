package com.yhh.services;

import com.yhh.fault.retry.NoRetryStrategy;
import com.yhh.fault.retry.RetryStrategy;
import com.yhh.model.RpcResponse;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * @author hyh
 * @date 2024/5/24
 */
public class RetryStrategyTest {

    RetryStrategy retryStrategy = new NoRetryStrategy();

    @Test
    public void doRetry() {
        try {
            RpcResponse rpcResponse = retryStrategy.doRetry(() -> {
                System.out.println("测试重试策略".toString());
                throw new RuntimeException("模拟重试失败");
            });
            System.out.println(rpcResponse);

        } catch (Exception e) {
            System.out.println("重试多次失败");
            e.printStackTrace();
        }
    }
}
