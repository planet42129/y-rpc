package com.yhh.fault.tolerant;

import com.yhh.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 静默处理
 * 遇到异常后，记录一条日志，然后正常返回一个响应对象
 *
 * @author hyh
 * @date 2024/5/24
 */
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("静默处理异常", e);
        return new RpcResponse();
    }
}
