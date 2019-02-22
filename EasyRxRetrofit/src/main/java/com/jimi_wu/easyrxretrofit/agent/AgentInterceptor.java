package com.jimi_wu.easyrxretrofit.agent;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wuzhiming on 2016/11/14.
 */

public class AgentInterceptor implements Interceptor {
    private static final String USER_AGENT_HEADER_NAME = "User-Agent";
    protected String agent;

    public AgentInterceptor(String agent) {
        this.agent = agent;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request oldRequest = chain.request();
        final Request newRequest = oldRequest.newBuilder()
                .removeHeader(USER_AGENT_HEADER_NAME)
                .addHeader(USER_AGENT_HEADER_NAME,agent)
                .build();
        return chain.proceed(newRequest);
    }


}
