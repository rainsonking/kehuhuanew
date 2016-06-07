package com.kwsoft.kehuhua.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/11/27 0027.
 */
public class FunctionMenuBean implements Serializable {
    private int img;
    private  String tvName;

    public FunctionMenuBean(int img, String tvName) {
        this.img = img;
        this.tvName = tvName;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTvName() {
        return tvName;
    }

    public void setTvName(String tvName) {
        this.tvName = tvName;
    }
}
