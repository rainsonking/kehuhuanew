package com.kwsoft.kehuhua.bean;

/**
 * Created by Administrator on 2015/12/5 0005.
 */
public class DialogFieldSet_id {
    private long inc;
    private long machine;
   // private boolean new;  //用的关键字，如果需要该字段，后台该字段名称
    private long time;
    private long timeSecond;


    public long getInc() {
        return inc;
    }

    public void setInc(long inc) {
        this.inc = inc;
    }

    public long getMachine() {
        return machine;
    }

    public void setMachine(long machine) {
        this.machine = machine;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTimeSecond() {
        return timeSecond;
    }

    public void setTimeSecond(long timeSecond) {
        this.timeSecond = timeSecond;
    }
}
