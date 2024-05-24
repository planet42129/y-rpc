package com.yhh.services;

import com.yhh.loadbalancer.ConsistentHashLoadBalancer;
import com.yhh.loadbalancer.LoadBalancer;
import com.yhh.model.ServiceMetaInfo;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author hyh
 * @date 2024/5/24
 */


public class LoadBalancerTest {

  @Test
  public void testSelectPositive() {
    LoadBalancer loadBalancer = new ConsistentHashLoadBalancer();
    Map<String, Object> requestParams = new HashMap<>();
    requestParams.put("key", "value");

    List<ServiceMetaInfo> serviceMetaInfoList = new ArrayList<>();
    ServiceMetaInfo serviceMetaInfo1 = new ServiceMetaInfo();
    serviceMetaInfo1.setServiceName("myService");
    serviceMetaInfo1.setServiceVersion("1.0");
    serviceMetaInfo1.setServiceHost("127.0.0.1");
    serviceMetaInfo1.setServicePort(1234);
    ServiceMetaInfo serviceMetaInfo2 = new ServiceMetaInfo();
    serviceMetaInfo2.setServiceName("myService");
    serviceMetaInfo2.setServiceVersion("1.0");
    serviceMetaInfo2.setServiceHost("localhost");
    serviceMetaInfo2.setServicePort(80);
    serviceMetaInfoList.add(serviceMetaInfo1);
    serviceMetaInfoList.add(serviceMetaInfo2);

    ServiceMetaInfo selectedService = loadBalancer.select(requestParams, serviceMetaInfoList);

    assertNotNull(selectedService);
    assertTrue(selectedService.equals(serviceMetaInfo1) || selectedService.equals(serviceMetaInfo2));
  }

  @Test
  public void testSelectNegative() {
    LoadBalancer loadBalancer = new ConsistentHashLoadBalancer();
    Map<String, Object> requestParams = new HashMap<>();
    requestParams.put("key", "value");

    List<ServiceMetaInfo> serviceMetaInfoList = new ArrayList<>();

    ServiceMetaInfo selectedService = loadBalancer.select(requestParams, serviceMetaInfoList);

    assertNull(selectedService);
  }

  @Test
  public void testGetHash() {
    ConsistentHashLoadBalancer loadBalancer = new ConsistentHashLoadBalancer();

    long hash1 = loadBalancer.getHash("test1");
    long hash2 = loadBalancer.getHash("test2");

    assertNotEquals(hash1, hash2);
  }
}