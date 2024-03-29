package com.bingo.fastframe.library.net;


/**
 * Created by jt on 2016/10/26.
 */
public class ApiException extends RuntimeException {

    public static final int RSP_CODE_SUCCESS = 0; //操作成功
    public static final int RSP_CODE_NEED_LOGIN = 1000; //需要登陆
    private int code;
    private String msg;

    public ApiException(HttpResult result) {
        this.code = result.getErrorCode();
        this.msg = result.getErrorMsg();
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    public boolean isNeedLogin() {
        return code == RSP_CODE_NEED_LOGIN;
    }
}

