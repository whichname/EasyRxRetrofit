package com.jimi_wu.sample.model;

import com.jimi_wu.easyrxretrofit.model.BaseModel;

/**
 * Created by wzm on 2017/6/14.
 */

public class ResultBean<T> implements BaseModel<T> {

    private int code;

    private T data;

    private String errMsg;

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }



    @Override
    public boolean isError() {
        return code != 200;
    }

    @Override
    public int getErrorCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return errMsg;
    }

    @Override
    public T getResult() {
        return data;
    }


}
