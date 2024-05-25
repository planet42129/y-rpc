package com.yhh.consumer;

import com.yhh.bootstrap.ConsumerBootstrap;
import com.yhh.common.model.User;
import com.yhh.common.service.UserService;
import com.yhh.proxy.ServiceProxyFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hyh
 * @date 2024/5/13
 */
@Slf4j
public class ConsumerExample {
    public static void main(String[] args) {
        ConsumerBootstrap.init();

        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("xuan");
        User newUser = userService.getUser(user);
        if (newUser != null) {
            log.info(newUser.getName());
        } else {
            log.info("user == null");
        }
/*        short number = userService.getNumber();
        log.info("number = " + number);*/
/*        User newUser2 = userService.getUser(user);
        if (newUser2 != null) {
            log.info(newUser2.getName());
        } else {
            log.info("user == null");
        }

        User newUser3 = userService.getUser(user);
        if (newUser3 != null) {
            log.info(newUser3.getName());
        } else {
            log.info("user == null");
        }*/
    }
}
