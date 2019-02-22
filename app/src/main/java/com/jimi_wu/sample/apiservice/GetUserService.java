package com.jimi_wu.sample.apiservice;


import com.jimi_wu.sample.Constants;
import com.jimi_wu.sample.model.ResultBean;
import com.jimi_wu.sample.model.UserBean;

import io.reactivex.Observable;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2016/11/9.
 */
public interface GetUserService {

    @POST(Constants.GET_USER_URL)
    Observable<ResultBean<UserBean>> start();

}
