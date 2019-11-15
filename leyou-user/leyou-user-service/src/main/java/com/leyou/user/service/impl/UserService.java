package com.leyou.user.service.impl;

import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.service.IUserService;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class UserService implements IUserService {

    @Autowired
    private UserMapper userMapper;


    @Override
    public Boolean checkUser(String data, Integer type) {
        User user = new User();
        switch (type){
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
            default:
                return null;
        }
        return userMapper.selectCount(user) == 0;
    }

    @Override
    public void register(User user) {
        //生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);

        //加盐加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));

        //新增用户
        user.setId(null);
        user.setCreated(new Date());
        userMapper.insertSelective(user);

    }

    @Override
    public User queryUser(String username, String password) {
        User record = new User();
        record.setUsername(username);
        User user = userMapper.selectOne(record);

        //判断user是否为空
        if(user == null){
            return null;
        }

        //获取盐，对用户输入的密码进行加密
        password = CodecUtils.md5Hex(password,user.getSalt());

        //和数据库中的密码比较
        if(StringUtils.equals(password,user.getPassword())){
            return user;
        }
        return null;
    }
}
