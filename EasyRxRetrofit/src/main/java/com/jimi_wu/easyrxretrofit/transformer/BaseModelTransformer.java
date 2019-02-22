package com.jimi_wu.easyrxretrofit.transformer;


import com.jimi_wu.easyrxretrofit.exception.ServerException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;

/**
 * created by wuzhiming on 2019/2/21
 *
 * 转换器
 * from BaseModel<T>
 * to T
 */
public class BaseModelTransformer<T> implements ObservableTransformer<BaseModel<T>, T> {


    @Override
    public ObservableSource<T> apply(Observable<BaseModel<T>> upstream) {
        return upstream.flatMap(new Function<BaseModel<T>, ObservableSource<T>>() {
            @Override
            public ObservableSource<T> apply(BaseModel<T> tBaseModel) throws Exception {
                if (!tBaseModel.isError()) {
                    return createData(tBaseModel.getResult());
                }
                return Observable.error(new ServerException(tBaseModel.getMsg()));
            }
        });
    }

    /**
     * 创建Flowable<T>
     */
    private static <T> Observable<T> createData(final T result) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) {
                try {
                    emitter.onNext(result);
                    emitter.onComplete();
                } catch (Exception exception) {
                    emitter.onError(exception);
                }
            }
        });
    }



}
