### 依赖

```
implementation 'com.jimi_wu:EasyRxRetrofit:2.0.0'
```

### 在你的application中注册
```java
@Override
public void onCreate() {
    super.onCreate();
//  在此处初始化Retrofit
    RetrofitManager.init(new YourRetrofitBuilder());
}
```

### 创建你的返回数据基类
`EasyRxRetrofit`封装了对返回数据的统一处理，你需要创建你自己的返回数据基类，并实现`BaseModel`接口的三个方法：

- isError(): 返回请求是否出错；
- getMsg()：返回错误信息；
- getResult()：返回请求成功的数据。

比如，如果你的返回格式为
```
{
    code: 200,
    errMsg: "success",
    data: {...}
}
```
那么，你的返回数据基类应该为
```Java
public class ResultBean<T> implements BaseModel<T> {

    private int code;
    private String errMsg;
    private T data;

    @Override
    public boolean isError() {return code != 200;}
    
    @Override
    public String getMsg() {return errMsg;}

    @Override
    public T getResult() {return data;}

    /** getter and setter*/
    ...
}
```
**提醒：基类记得使用泛型**

### 调用get/post请求

要调用`get/post`请求，你首先需要创建自己的`ApiService`，跟你使用`retrofit`时一样，只是**返回类型必须为`Observable<ResultBean<T>>`，其中ResultBean是你创建的返回数据基类**:

```java
public interface GetUserService {

    @POST("/getUser")
    Observable<ResultBean<UserBean>> start();

}
```


然后，执行你的请求，**调用`.compose(RetrofitManager.<T>handleResult())`来实现返回数据的处理，并使用BaseObserver来订阅结果**，如：
```java
RetrofitManager
        .createService(GetUserService.class)
        .start()
        .compose(RetrofitManager.<UserBean>handleResult())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new BaseObserver<UserBean>(this) {

            @Override
            protected void _onNext(UserBean userBean) {
                Log.i("retrofit", "onNext=========>" + userBean.getName());
                tv.setText("请求成功:" + userBean.getName());
            }

            @Override
            protected void _onError(Throwable e) {
                Log.i("retrofit", "onError=========>" + e.getMessage());
                tv.setText("请求失败:" + e.getMessage());
            }
        });
```

### 上传单个/多个文件，监听上传进度

上传文件不需自己创建`ApiService`，只需新建`UploadParam`对象，然后调用`RetrofitManager.uploadFile`方法，传入接口地址和`UploadParam`对象即可。

`uploadFile`方法有两个定义:

```java
uploadFile(String url, UploadParam... params);
uploadFile(String url, List<UploadParam> params);
```

因此，上传单个文件与上传多个文件的方法是相同的：

```java
// 上传单个文件
RetrofitManager.uploadFile(Constants.UPLOAD_URL, new UploadParam("upload", file))
// 上传多个文件
RetrofitManager.uploadFile(Constants.UPLOAD_URL, 
                           new UploadParam("upload", file1), 
                           new UploadParam("upload", file2));
// 使用List上传多个文件
ArrayList<UploadParam> uploadParams = new ArrayList<>(files.size());
for (File file : files) {
    uploadParams.add(new UploadParam("upload", file));
}
RetrofitManager.uploadFile(Constants.UPLOADS_URL, uploadParams);
```

需要注意的是，上传文件需要使用`UploadObserver`来订阅：

```java
ArrayList<UploadParam> uploadParams = new ArrayList<>(files.size());
for (File file : files) {
    uploadParams.add(new UploadParam("upload", file));
}
RetrofitManager
        .uploadFile(Constants.UPLOADS_URL, uploadParams)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new UploadObserver<ResultBean<ArrayList<FileBean>>>() {

            @Override
            public void _onNext(ResultBean<ArrayList<FileBean>> arrayListResultBean) {
                ArrayList<FileBean> fileBeans = arrayListResultBean.getData();
                StringBuilder stringBuilder = new StringBuilder();
                for (FileBean fileBean : fileBeans) {
                    stringBuilder.append("上传成功:" + fileBean.getUrl() + "\n");
                }
                Log.d("retrofit", "onNext=======>" + stringBuilder);
                tv.setText("上传成功:" + stringBuilder);
            }

            @Override
            public void _onProgress(Integer percent) {
                Log.i("retrofit", "onProgress=======>" + percent);
                tv.setText("上传中:" + percent);
            }
            
            @Override
            public void _onProgress(long uploaded, long sumLength) {
                tvActualProgress.setText("下载中:" + uploaded + "/" + sumLength);
            }

            @Override
            public void _onError(Throwable e) {
                Log.d("retrofit", "onError======>" + e.getMessage());
                tv.setText("上传失败:" + e.getMessage());
            }
        });
```

其中`_onProgress(Integer percent)`方法回调百分比进度，`_onProgress(long uploaded, long sumLength)`方法回调文件长度进度，这两个方法并非抽象方法，因此可按需选择监听或不监听。

结果如下：

```
06-20 10:52:49.095 32377-32377/com.jimi_wu.sample I/retrofit: onProgress=======>95
06-20 10:52:49.095 32377-32377/com.jimi_wu.sample I/retrofit: onProgress=======>96
06-20 10:52:49.116 32377-32377/com.jimi_wu.sample I/retrofit: onProgress=======>97
06-20 10:52:49.117 32377-32377/com.jimi_wu.sample I/retrofit: onProgress=======>98
06-20 10:52:49.118 32377-32377/com.jimi_wu.sample I/retrofit: onProgress=======>99
06-20 10:52:49.118 32377-32377/com.jimi_wu.sample I/retrofit: onProgress=======>100
06-20 10:52:49.160 32377-32377/com.jimi_wu.sample I/retrofit: onNext=======>上传成功:http://192.168.5.127:3000/uploads/wx_camera_1497846596539.jpg
                                                              上传成功:http://192.168.5.127:3000/uploads/wx_camera_1497774775994.jpg
                                                              上传成功:http://192.168.5.127:3000/uploads/wx_camera_1497774700027.jpg
```


### 文件下载，监听下载进度

与上传文件类似，下载文件只需调用`RetrofitManager.download("")`方法，并使用`DownLoadObserver`订阅即可：

```java
RetrofitManager
        .download("/uploads/test.pdf")
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new DownLoadObserver() {
            @Override
            public void _onNext(String result) {
                Log.i("retrofit", "onNext=======>" + result);
                tv.setText("下载成功:" + result);
            }

            @Override
            public void _onProgress(Integer percent) {
                Log.i("retrofit", "onProgress=======>" + percent);
                tv.setText("下载中:" + percent);
            }

            @Override
            public void _onProgress(long uploaded, long sumLength) {
                tvActualProgress.setText("下载中:" + uploaded + "/" + sumLength);
            }

            @Override
            public void _onError(Throwable e) {
                Log.i("retrofit", "onProgress=======>" + e.getMessage());
                tv.setText("下载失败:" + e.getMessage());
            }
        });
```

当然，你也可以使用 `download(String url, String savePath, String fileName)` 来设置下载的保存路径和文件名。

`EasyRxRetrofit `内部在下载文件时，会使用.tmp后缀作为临时文件名，待下载完成后再重命名为真正的名字。与上传一样，`DownLoadObserver`内部也含有两个`_onProgress`回调，可按需监听或不监听。

结果如下:
```
06-20 10:54:01.590 32377-32377/com.jimi_wu.sample I/retrofit: onProgress=======>95
06-20 10:54:01.686 32377-32377/com.jimi_wu.sample I/retrofit: onProgress=======>96
06-20 10:54:01.793 32377-32377/com.jimi_wu.sample I/retrofit: onProgress=======>97
06-20 10:54:01.884 32377-32377/com.jimi_wu.sample I/retrofit: onProgress=======>98
06-20 10:54:01.985 32377-32377/com.jimi_wu.sample I/retrofit: onProgress=======>99
06-20 10:54:02.086 32377-32377/com.jimi_wu.sample I/retrofit: onProgress=======>100
06-20 10:54:02.100 32377-32377/com.jimi_wu.sample I/retrofit: onNext=======>/storage/emulated/0/Download/VID_20170616_122618.mp4
```

### 说明

- 可通过实现`RetrofitBuilder`接口返回`Retrofit`实例，管理各项配置。
- `EasyRxRetrofit`内部不会进行**线程切换**，需手动按需切换。
- `EasyRxRetrofit`内部未包含**生命周期类的管理**，需手动管理。

---

博客地址：[http://blog.csdn.net/anyfive/article/details/73469365](http://blog.csdn.net/anyfive/article/details/73469365)