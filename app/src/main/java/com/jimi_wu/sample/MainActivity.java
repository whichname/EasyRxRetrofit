package com.jimi_wu.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jimi_wu.easyrxretrofit.RetrofitUtils;
import com.jimi_wu.easyrxretrofit.subscriber.DownLoadSubscriber;
import com.jimi_wu.easyrxretrofit.subscriber.UploadSubscriber;
import com.jimi_wu.easyrxretrofit.subscriber.EasySubscriber;
import com.jimi_wu.sample.apiservice.FileUploadService;
import com.jimi_wu.sample.apiservice.FilesUploadService;
import com.jimi_wu.sample.apiservice.GetUserService;
import com.jimi_wu.sample.model.FileBean;
import com.jimi_wu.sample.model.UserBean;

import org.reactivestreams.Publisher;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static int REQUEST_CODE_UPLOAD = 1;
    private static int REQUEST_CODE_UPLOADS = 2;

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(this);
        findViewById(R.id.btn_upload).setOnClickListener(this);
        findViewById(R.id.btn_uploads).setOnClickListener(this);
        findViewById(R.id.btn_download).setOnClickListener(this);
        tv = (TextView) findViewById(R.id.tv);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
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
                break;
            case R.id.btn_upload:
                Intent uploadIntent = new Intent();
                uploadIntent.setType("*/*");
                uploadIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(uploadIntent, "单文件选择"), REQUEST_CODE_UPLOAD);
                break;
            case R.id.btn_uploads:
                Intent uploadsIntent = new Intent();
                uploadsIntent.setType("*/*");
                uploadsIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(uploadsIntent, "单文件选择"), REQUEST_CODE_UPLOADS);
                break;
            case R.id.btn_download:
                downLoad();
                break;
        }
    }

    private ArrayList<Uri> uris = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == REQUEST_CODE_UPLOAD) {
                Uri uri = data.getData();
                File file = FileUtils.getFile(this, uri);
                upload(file);
                return;
            }
            if(requestCode == REQUEST_CODE_UPLOADS) {
                uris.add(data.getData());
                if(uris.size() < 3) {
                    tv.setText("当前选择 "+uris.size()+" 个文件\n请继续选择\n3 个文件时将上传");
                    return;
                }
                ArrayList<File> files = new ArrayList<>();
                for (Uri uri: uris) {
                    File file = FileUtils.getFile(this, uri);
                    files.add(file);
                }
                uploads(files);
                uris.clear();
                return;
            }
        }
    }

    /**
     * 单图上传
     */
    public void upload(File file) {
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
    }


    /**
     * 多图上传
     */
    public void uploads(ArrayList<File> files) {
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
    }

    /**
     * 文件下载
     */
    public void downLoad() {
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
    }


}
