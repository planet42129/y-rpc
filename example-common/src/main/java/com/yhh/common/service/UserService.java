package com.yhh.common.service;

import com.yhh.common.model.User;

/**
 * @author hyh
 * @date 2024/5/11
 */
public interface UserService {
    /**
     * 获取用户
     *
     * @param user
     * @return
     */
    User getUser(User user);

    /**
     * 用于测试 mock 接口返回值
     *
     * @return
     */
    default short getNumber() {
        return 1;
    }
}
