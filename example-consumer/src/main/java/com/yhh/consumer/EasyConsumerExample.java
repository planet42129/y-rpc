package com.yhh.consumer;

import com.yhh.common.model.User;
import com.yhh.common.service.UserService;
import com.yhh.consumer.proxy.UserServiceStaticProxy;
import com.yhh.proxy.ServiceProxyFactory;


/**
 * 服务消费者是需要调用服务的模块
 */
public class EasyConsumerExample {
    public static void main(String[] args) {
        //todo 需要获取UserService的实现类对象
        //静态代理的缺点是给每个服务接口都写一个静态代理实现类，麻烦
//        UserService userService = new UserServiceStaticProxy();
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        //所以采用动态代理
        User user = new User();
        user.setName("xuan");
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}
