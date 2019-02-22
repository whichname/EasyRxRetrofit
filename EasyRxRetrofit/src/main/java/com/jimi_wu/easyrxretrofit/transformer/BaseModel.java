package com.jimi_wu.easyrxretrofit.transformer;

/**
 * created by wuzhiming on 2019/2/21
 */
public interface BaseModel<T> {

    boolean isError();

    String getMsg();

    T getResult();

}
