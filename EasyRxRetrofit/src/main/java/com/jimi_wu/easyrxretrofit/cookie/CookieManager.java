package com.jimi_wu.easyrxretrofit.cookie;

import android.content.Context;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by Administrator on 2016/9/6.
 */
public class CookieManager implements CookieJar {

    private CookieStore cookieStore;

    public CookieManager(Context context) {
        cookieStore = new CookieStore(context);
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (cookies != null && cookies.size() > 0) {
            for (Cookie item : cookies) {
                cookieStore.add(url, item);
            }
        }
    }


    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies =cookieStore.get(url);
        return cookies;
    }


}
