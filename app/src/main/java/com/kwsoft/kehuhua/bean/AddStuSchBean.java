package com.kwsoft.kehuhua.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/12/14 0014.
 */
public class AddStuSchBean implements Serializable {
    private String name;
    private Boolean isCheck;
    private String id;
    private String dicafm;

    public String getDicafm() {
        return dicafm;
    }

    public void setDicafm(String dicafm) {
        this.dicafm = dicafm;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(Boolean isCheck) {
        this.isCheck = isCheck;
    }
}
