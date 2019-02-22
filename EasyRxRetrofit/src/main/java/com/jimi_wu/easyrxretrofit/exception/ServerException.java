package com.jimi_wu.easyrxretrofit.exception;

/**
 * Created by Administrator on 2016/9/5.
 */
public class ServerException extends Exception {

    public ServerException() {
    }

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerException(Throwable cause) {
        super(cause);
    }

}
