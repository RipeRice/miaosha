package com.miaoshaproject.controller;

import com.miaoshaproject.controller.VO.UserVO;
import com.miaoshaproject.error.BuinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;


@Controller("user")
@RequestMapping("/user")
//处理ajax中跨域请求的问题  origins="*" 表示所有的域名都可以访问
@CrossOrigin(origins = {"*"}, allowCredentials = "true")
public class UserController extends BaseController{
    @Autowired
    UserService userService;

    @Autowired
    HttpServletRequest httpServletRequest;

    /*
    用户登录接口
     */
    @RequestMapping(value = "/login",method = {RequestMethod.GET},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(
            @RequestParam(name="telphone") String telphone,
            @RequestParam(name = "password") String password)throws BuinessException,UnsupportedEncodingException, NoSuchAlgorithmException{
        //入参校验
        if(StringUtils.isEmpty(telphone)){
            throw new BuinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        //用户登录服务，用来校验用户登录是否合法
        UserModel userModel = userService.validateLogin(telphone, this.encodeByMd5(password));

        //将登录凭证加入到用户登录成功的session内
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);

        return CommonReturnType.creat(null);
    }
    /*
    用户的注册接口
     */
    @RequestMapping(value = "/register", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telphone") String telphone,
                                     @RequestParam(name = "otpCode") String otpCode,
                                     @RequestParam(name = "name") String name,
                                     @RequestParam(name = "gender") Integer gender,
                                     @RequestParam(name = "age") Integer age,
                                     @RequestParam(name = "password") String password) throws BuinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //验证手机号和对应的otpCode
        String inSessionOtpCode = (String)this.httpServletRequest.getSession().getAttribute(telphone);
        if (!com.alibaba.druid.util.StringUtils.equals(otpCode, inSessionOtpCode)) {
            throw new BuinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "短信验证码不符合");
        }
        //用户注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setTelphone(telphone);
        userModel.setGender(new Byte(String.valueOf(gender.intValue())));
        userModel.setAge(age);
        userModel.setRegisterMode("byphone");
        userModel.setEncrptPassword(this.encodeByMd5(password));
        userService.register(userModel);
        return CommonReturnType.creat(null);
    }

    //对密码进行md5的加密
    public String encodeByMd5(String pwd) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        Base64.Encoder encoder = Base64.getEncoder();
        String result = encoder.encodeToString(md5.digest(pwd.getBytes("utf-8")));
       return result;
    }

    /*
    用户获取otp短信接口
     */
    @RequestMapping(value = "/getotp", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name="telphone")String telphone){
        //按照一定规则生成otp验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;
        String optCode = String.valueOf(randomInt);

        //将手机号和opt进行绑定
        httpServletRequest.getSession().setAttribute(telphone, optCode);
        System.out.println("telphone" + telphone + ":optCode" + optCode);
        return CommonReturnType.creat(null);
    }

    @RequestMapping("/getUser")
    @ResponseBody
    public CommonReturnType getUserInfoById(@RequestParam(name = "id") Integer id) throws BuinessException {
        //调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);

        //如果获取的用户信息不存在
        if (userModel == null) {
            throw new BuinessException(EmBusinessError.USER_NOT_EXIST);
        }

        //将核心领域模型用户对象转化为可供UI使用的ViewObject
        UserVO userVO = converVOFromModel(userModel);
        //返回通用对象
        return CommonReturnType.creat(userVO);
    }
    private UserVO converVOFromModel(UserModel userModel){
        if(userModel==null){
            return null;
        }
        UserVO userVO=new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return userVO;
    }
}
