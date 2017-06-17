package com.jimi_wu.easyrxretrofit;

import android.content.Context;


import com.jimi_wu.easyrxretrofit.agent.AgentInterceptor;
import com.jimi_wu.easyrxretrofit.cookie.CookieManager;

import java.util.ArrayList;

import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wzm on 2017/6/14.
 */

public class RetrofitManager {

    private String mAgent;
    private String mBaseUrl;
    private ArrayList<Converter.Factory> mConverterFactorys = new ArrayList<>();
    private ArrayList<CallAdapter.Factory> mCallAdapterFactory = new ArrayList<>();
    private ArrayList<Interceptor> mInterceptor = new ArrayList<>();
    private CookieJar mCookieJar;

    private OkHttpClient mOkHttpClient;
    private Retrofit mRetrofit;

    private RetrofitManager() {
    }

    private static RetrofitManager mRetrofitManager;

    public synchronized static RetrofitManager getInstance() {
        if(mRetrofitManager == null)
            mRetrofitManager = new RetrofitManager();
        return mRetrofitManager;
    }

    public RetrofitManager setBaseUrl(String baseUrl) {
        this.mBaseUrl = baseUrl;
        return this;
    }

    public RetrofitManager setAgent(String agent) {
        this.mAgent = agent;
        return this;
    }

    public RetrofitManager addConverterFactory(Converter.Factory factory) {
        this.mConverterFactorys.add(factory);
        return this;
    }

    public RetrofitManager addCallAdapterFactory(CallAdapter.Factory factory) {
        this.mCallAdapterFactory.add(factory);
        return this;
    }

    public void init(Context context) {

        if (mBaseUrl == null) throw new IllegalArgumentException("Base URL required.");

        if(mCookieJar == null) mCookieJar = new CookieManager(context);
        if(mAgent != null) mInterceptor.add(new AgentInterceptor(mAgent));
        mConverterFactorys.add(GsonConverterFactory.create());
        mCallAdapterFactory.add(RxJava2CallAdapterFactory.create());

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.cookieJar(mCookieJar);
        for(Interceptor interceptor : mInterceptor) {
            okHttpClientBuilder.addInterceptor(interceptor);
        }
        this.mOkHttpClient = okHttpClientBuilder.build();

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder.client(mOkHttpClient);
        retrofitBuilder.baseUrl(mBaseUrl);
        for (Converter.Factory factory : mConverterFactorys) {
            retrofitBuilder.addConverterFactory(factory);
        }
        for (CallAdapter.Factory factory : mCallAdapterFactory) {
            retrofitBuilder.addCallAdapterFactory(factory);
        }

        this.mRetrofit = retrofitBuilder.build();

        RetrofitUtils.setRetrofitManager(this);
    }


    public Retrofit getRetrofit() {
        return mRetrofit;
    }

}
