package com.miaoshaproject.service;

import com.miaoshaproject.error.BuinessException;
import com.miaoshaproject.service.model.UserModel;

import javax.swing.*;

public interface UserService {
     UserModel getUserById(Integer id);
    //用户的注册
     void register(UserModel userModel) throws BuinessException;
     //用户登录校验
    UserModel validateLogin(String telphone,String encryptPassword) throws BuinessException;
}
