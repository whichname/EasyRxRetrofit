package com.jimi_wu.easyrxretrofit.exception;

/**
 * Created by Administrator on 2016/9/5.
 */
public class ServerException extends Exception {

    public static final int ERROR_NETWORK = -1;
    public static final int ERROR_OTHER = -2;

    private int errorCode = ERROR_OTHER;

    public int getErrorCode() {
        return errorCode;
    }

    public ServerException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}
