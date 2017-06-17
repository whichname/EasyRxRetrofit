package com.jimi_wu.easyrxretrofit.model;

/**
 * Created by Administrator on 2016/9/5.
 */
public interface BaseModel<T> {

    boolean isError();

    int getErrorCode();

    String getMsg();

    T getResult();

}
