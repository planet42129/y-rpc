package com.yhh.loadbalancer;

/**
 * @author hyh
 * @date 2024/5/24
 */
public interface LoadBalancerKeys {
    String ROUND_ROBIN = "roundRobin";

    String RANDOM = "random";

    String CONSISTENT_HASH = "consistentHash";
}
