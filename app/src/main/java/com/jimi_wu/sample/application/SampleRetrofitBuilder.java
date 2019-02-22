package com.jimi_wu.sample.application;

import com.jimi_wu.easyrxretrofit.agent.AgentInterceptor;
import com.jimi_wu.easyrxretrofit.build.RetrofitBuilder;
import com.jimi_wu.sample.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * created by wuzhiming on 2019/2/21
 */
public class SampleRetrofitBuilder implements RetrofitBuilder {

    @Override
    public Retrofit initRetrofit() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(new AgentInterceptor("EasyRxRetrofit"))
                .build();

        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

}
