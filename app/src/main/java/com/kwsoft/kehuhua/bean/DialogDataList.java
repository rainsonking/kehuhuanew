package com.kwsoft.kehuhua.bean;

import java.util.List;

/**
 * Created by Administrator on 2015/12/5 0005.
 */
public class DialogDataList {
    private int total;
    private List<DialogDataListRows> rows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DialogDataListRows> getRows() {
        return rows;
    }

    public void setRows(List<DialogDataListRows> rows) {
        this.rows = rows;
    }
}
