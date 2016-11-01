package com.kwsoft.kehuhua.wechatPicture.andio;

/**
 * Created by Administrator on 2016/11/1 0001.
 */

public class Recorder {
    public float time;
    public String filePathString;

    public Recorder(float time, String filePathString) {
        super();
        this.time = time;
        this.filePathString = filePathString;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public String getFilePathString() {
        return filePathString;
    }

    public void setFilePathString(String filePathString) {
        this.filePathString = filePathString;
    }
}
