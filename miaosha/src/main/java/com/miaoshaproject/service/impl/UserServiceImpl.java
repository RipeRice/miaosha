package com.miaoshaproject.service.impl;

import com.miaoshaproject.dao.UserDOMapper;
import com.miaoshaproject.dao.UserPasswordDOMapper;
import com.miaoshaproject.dataobject.UserDO;
import com.miaoshaproject.dataobject.UserPasswordDO;
import com.miaoshaproject.error.BuinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import com.miaoshaproject.validator.ValidationResult;
import com.miaoshaproject.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDOMapper userDOMapper;

    @Autowired
    UserPasswordDOMapper userPasswordDOMapper;

    @Autowired
    private ValidatorImpl validator;
//登录服务
    @Override
    public UserModel validateLogin(String telphone,String encryptPassword) throws BuinessException {
        UserDO userDO = userDOMapper.selectByTelphone(telphone);
        if(userDO==null){
            throw new BuinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        if(!StringUtils.equals(encryptPassword,userPasswordDO.getEncrptPassword())){
            throw new BuinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserModel userModel = converFromUser(userDO, userPasswordDO);
        return userModel;
    }
//注册服务
    @Override
    @Transactional
    public void register(UserModel userModel) throws BuinessException {
        if (userModel == null) {
            throw new BuinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
//        if (StringUtils.isEmpty(userModel.getName())
//                || userModel.getGender() == null
//                || userModel.getAge() == null
//                || StringUtils.isEmpty(userModel.getTelphone())) {
//            throw new BuinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
//        }
        ValidationResult result = validator.validate(userModel);
        if (result.isHasErrors()) {
            throw new BuinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }

        UserDO userDO = converUserFromModel(userModel);
        //实现model->dataobject方法
        try {
            userDOMapper.insertSelective(userDO);
        } catch (DuplicateKeyException e) {
            throw new BuinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"手机号已被注册！");
        }
        userModel.setId(userDO.getId());
        UserPasswordDO userPasswordDO = converPasswordFromModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);
        return;
    }


    //从model转为UserDO方法
    private UserDO converUserFromModel(UserModel userModel){
        if(userModel==null){
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel,userDO);
        return userDO;
    }
    //从model转为UserPsaawordDO方法
    private UserPasswordDO converPasswordFromModel(UserModel userModel){
        if (userModel==null){
            return null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userModel.getId());
        return userPasswordDO;
    }

    @Override
    public UserModel getUserById(Integer id) {
        //通过userDOMapper获取userdo实例
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        //通过userPasswordDOMapper获取到加密密码
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        return converFromUser(userDO,userPasswordDO);
    }
    //从userDO和UserPasswordDO转为方法
    public UserModel converFromUser(UserDO userDO,UserPasswordDO userPasswordDO){
        if(userDO==null){
            return null;
        }
        UserModel userModel = new UserModel();
        //将userDO的信息给到userModel
        BeanUtils.copyProperties(userDO,userModel);
        if(userPasswordDO!=null){
            //将userpassworddo信息给到usermodel
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }
        return userModel;
    }


}
