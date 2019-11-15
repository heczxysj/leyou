package com.leyou.user.service;

import com.leyou.user.pojo.User;

public interface IUserService {
    /**
     * 检查用户是否存在
     * @param data
     * @param type
     * @return
     */
    Boolean checkUser(String data, Integer type);

    /**
     * 注册
     * @param user
     */
    void register(User user);

    User queryUser(String username, String password);
}
