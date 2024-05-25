package com.yhh.fault.tolerant;

/**
 * @author hyh
 * @date 2024/5/24
 */
public interface TolerantStrategyKeys {
    /**
     * 故障恢复
     */
    String FAIL_BACK = "failBack";//降级到其他服务

    /**
     * 快速失败
     */
    String FAIL_FAST = "failFast";//向外层抛出异常

    /**
     * 故障转移
     */
    String FAIL_OVER = "failOver";//转移到其他服务节点

    /**
     * 静默处理
     */
    String FAIL_SAFE = "failSafe";//记录日志，正常返回
}
