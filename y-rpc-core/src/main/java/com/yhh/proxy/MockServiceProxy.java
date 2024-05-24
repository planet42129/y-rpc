package com.yhh.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 根据接口服务类型返回固定值
 *
 * @author hyh
 * @date 2024/5/13
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      Class<?> methodReturnType = method.getReturnType();
      log.info("mock invoke {}", method.getName());
      return getDefaultObject(methodReturnType);
    }

    /**
     * 生成指定类型的默认对象
     * @param type
     * @return
     */
    private Object getDefaultObject(Class<?> type) {
        //基本数据类型
        if (type.isPrimitive()) {
            if (type == boolean.class) {
                return false;
            } else if (type == short.class) {
                return (short) 0;
            } else if (type == int.class) {
                return 0;
            } else if (type == long.class) {
                return 0L;
            }
        }
        //对象类型
        return null;
    }
}
