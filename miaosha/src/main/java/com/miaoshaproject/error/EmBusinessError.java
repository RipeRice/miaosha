package com.miaoshaproject.error;

public enum EmBusinessError implements CommonError{
    /*
    自定义错误类型
     */

    //通用类型错误
    PARAMETER_VALIDATION_ERROR(10001, "参数不合法"),
    //未知错误
    UNKNOWN_ERROR(10002, "未知错误"),

    //与用户有关的类型错误
    USER_NOT_EXIST(20001, "用户不存在"),
    USER_LOGIN_FAIL(20002, "用户名或密码错误"),
    USER_NOT_LOGIN(20003, "该用户还没有登入"),

    //交易类型的错误
    STOCK_OF_ENOUGH(30001, "库存不足");

    ;

    private EmBusinessError(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    private int errCode;
    private String errMsg;

    @Override
    public int getErrorCode() {
        return this.errCode;
    }

    @Override
    public String getErrorMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrorMsg(String errorMsg) {
        this.errMsg = errorMsg;
        return this;
    }
}
