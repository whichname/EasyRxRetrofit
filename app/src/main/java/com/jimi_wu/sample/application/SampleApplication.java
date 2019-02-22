package com.jimi_wu.sample.application;

import android.app.Application;

import com.jimi_wu.easyrxretrofit.RetrofitManager;
import com.jimi_wu.easyrxretrofit.build.DefaultRetrofitBuilder;
import com.jimi_wu.sample.Constants;

/**
 * Created by Administrator on 2016/11/9.
 */
public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        在此处初始化Retrofit
        RetrofitManager
                .init(new SampleRetrofitBuilder());
    }


}
