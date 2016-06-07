package com.kwsoft.kehuhua.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/25 0025.
 */
public class ListMapBean implements Serializable{
    List<List<Map<String, Object>>> mapList;

    public List<List<Map<String, Object>>> getMapList() {
        return mapList;
    }

    public void setMapList(List<List<Map<String, Object>>> mapList) {
        this.mapList = mapList;
    }
}
