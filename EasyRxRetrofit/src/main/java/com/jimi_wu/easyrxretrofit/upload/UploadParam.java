package com.jimi_wu.easyrxretrofit.upload;

import java.io.File;

import io.reactivex.annotations.NonNull;

/**
 * created by wuzhiming on 2019/2/21
 */
public class UploadParam {

    public static final String TYPE_STRING = "string";
    public static final String TYPE_FILE = "file";

    private String type;

    private String name;
    private String value;

    private File file;
    private String fileName;

    public UploadParam(@NonNull String name, @NonNull String value) {
        this.name = name;
        this.value = value;
        this.type = TYPE_STRING;
    }

    public UploadParam(@NonNull String name, @NonNull File file) {
        this.name = name;
        this.file = file;
        this.fileName = file.getName();
        this.type = TYPE_FILE;
    }

    public UploadParam(@NonNull String name, @NonNull File file, @NonNull String fileName) {
        this.name = name;
        this.file = file;
        this.fileName = fileName;
        this.type = TYPE_FILE;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
