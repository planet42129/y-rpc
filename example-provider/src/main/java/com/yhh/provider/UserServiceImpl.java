package com.yhh.provider;

import com.yhh.common.model.User;
import com.yhh.common.service.UserService;

/**
 * @author hyh
 * @date 2024/5/11
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}
