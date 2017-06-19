### 依赖

```
compile 'com.jimi_wu:EasyRxRetrofit:1.0.0'
```

### 在你的application中注册
```java
@Override
public void onCreate() {
    super.onCreate();
//  在此处初始化Retrofit
    RetrofitManager
            .getInstance()
            .setAgent("agent")//设置agent
            .init(this);
}
```

### 创建你的返回数据基类
EasyRxRetrofit封装了对返回数据的统一处理，你需要创建你自己的返回数据基类，并实现BaseModel接口的四个方法：

- isError(): 返回请求是否出错；
- getErrorCode()：返回错误代码；
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
    public int getErrorCode() {return code;}

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

要调用get/post请求，你首先需要创建自己的ApiService，
跟你使用retrofit时一样，只是**返回类型必须为“Flowable<ResultBean<T>>”，其中ResultBean是你创建的返回数据基类**。

```java
public interface GetUserService {

    @POST("/getUser")
    Flowable<ResultBean<UserBean>> start();

}
```


然后，执行你的请求，**调用.compose(RetrofitUtils.<T>handleResult())来实现返回数据的处理，以及线程的切换，并使用EasySubscriber来订阅结果**，如：
```java
RetrofitUtils
        .createService(GetUserService.class)
        .start()
        .compose(RetrofitUtils.<UserBean>handleResult())
        .subscribe(new EasySubscriber<UserBean>(this) {

            @Override
            protected void _onNext(UserBean userBean) {
                Log.i("retrofit", "onNext=========>" + userBean.getName());
                tv.setText("请求成功:"+userBean.getName());
            }

            @Override
            protected void _onError(int errorCode, String msg) {
                Log.i("retrofit", "onError=========>" + msg);
                tv.setText("请求失败:"+msg);
            }
        });
```

### 上传单个文件，监听上传进度

上传文件与发送get/post请求类似，首先，需要创建你的ApiService，如：
```java
public interface FileUploadService {

    @Multipart
    @POST("/upload")
    Flowable<ResultBean<FileBean>> upload(@Part MultipartBody.Part file);

}
```
**注意，必须声明Multipart格式，同时，第一个参数必须为“@Part MultipartBody.Part”**。

然后，调用上传方法uploadFile()，**将ApiService与ApiService内上传请求的方法名传入**，这是因为uploadFile方法内部使用了反射来创建真正的上传请求。

最后，使用UploadSubscriber来监听上传的进度及结果。

如：

```java
RetrofitUtils
    .uploadFile(file, FileUploadService.class, "upload")
    .safeSubscribe(new UploadSubscriber<FileBean>(this) {
        @Override
        protected void _onNext(FileBean result) {
            Log.i("retrofit", "onNext=======>url:"+result.getUrl());
            tv.setText("上传成功:"+result.getUrl());
        }

        @Override
        protected void _onProgress(Integer percent) {
            Log.i("retrofit", "onProgress======>"+percent);
            tv.setText("上传中:"+percent);
        }

        @Override
        protected void _onError(int errorCode, String msg) {
            Log.i("retrofit", "onError======>"+msg);
            tv.setText("上传失败:"+msg);
        }
    });
```

### 上传多个文件，监听上传进度
多文件上传与单文件上传是类似的，只是ApiService稍有不同：
```java
public interface FilesUploadService {

    @Multipart
    @POST("/uploads")
    Flowable<ResultBean<ArrayList<FileBean>>> uploads(@Part ArrayList<MultipartBody.Part> files);

}
```

可以看到，**多文件上传需要定义参数为“@Part ArrayList<MultipartBody.Part>”**。

然后调用uploadFiles方法，同样传入ApiService与ApiService内上传请求的方法名，并使用UploadSubscriber来监听上传进度与上传结果。

如：
```java
RetrofitUtils
    .uploadFiles(files, FilesUploadService.class, "uploads")
    .safeSubscribe(new UploadSubscriber<ArrayList<FileBean>>(this) {
        @Override
        protected void _onNext(ArrayList<FileBean> result) {
            StringBuilder stringBuilder = new StringBuilder();
            for (FileBean fileBean : result) {
                stringBuilder.append("上传成功:"+fileBean.getUrl()+"\n");
            }
            Log.i("retrofit", "onNext=======>"+stringBuilder);
            tv.setText("上传成功:"+stringBuilder);
        }

        @Override
        protected void _onProgress(Integer percent) {
            Log.i("retrofit", "onProgress=======>"+percent);
            tv.setText("上传中:"+percent);
        }

        @Override
        protected void _onError(int errorCode, String msg) {
            Log.i("retrofit", "onError======>"+msg);
            tv.setText("上传失败:"+msg);
        }
    });
```


### 文件下载，监听下载进度

文件下载并不需要你创建自己的ApiService，你只需要**调用downLoadFile方法，传入文件地址，并使用DownLoadSubscriber监听下载进度与下载结果**就可以了，如：

```java
RetrofitUtils
    .downLoadFile("/uploads/VID_20170616_122618.mp4")
    .safeSubscribe(new DownLoadSubscriber(this) {
        @Override
        protected void _onNext(String result) {
            Log.i("retrofit", "onNext=======>"+result);
            tv.setText("下载成功:"+result);
        }

        @Override
        protected void _onProgress(Integer percent) {
            Log.i("retrofit", "onProgress=======>"+percent);
            tv.setText("下载中:"+percent);
        }

        @Override
        protected void _onError(int errorCode, String msg) {
            Log.i("retrofit", "onProgress=======>"+msg);
            tv.setText("下载失败:"+msg);
        }
    });
```

当然，你也可以使用
```java
downLoadFile(String url, String savePath, String fileName)
```
来设置下载的保存路径和文件名。


博客地址：[http://blog.csdn.net/anyfive/article/details/73469365](http://blog.csdn.net/anyfive/article/details/73469365)