package com.kwsoft.kehuhua.bean;

/**
 * Created by Administrator on 2015/12/3 0003.
 */
public class Time {
    /**
     * dataTime : 1449038989240
     * dataListUrl : http://192.168.6.46:8080/phone_edus_auto/model_ajaxList.do
     * FieldTime : 1449038989046
     * FieldUrl : http://192.168.6.46:8080/phone_edus_auto/phone_getField.do
     * buttonTime : 1449038989240
     * buttonUrl : http://192.168.6.46:8080/phone_edus_auto/phone_getButton.do
     * OperaButtonTime : 1449035263293
     * OperaButtonUrl : http://192.168.6.46:8080/phone_edus_auto/phone_getOperaButton.do
     */

    private long dataTime;
    private String dataListUrl;
    private long FieldTime;
    private String FieldUrl;
    private long buttonTime;
    private String buttonUrl;
    private long OperaButtonTime;
    private String OperaButtonUrl;
    private long searchSetTime;
    private String searchSetUrl;

    public long getSearchSetTime() {
        return searchSetTime;
    }

    public void setSearchSetTime(long searchSetTime) {
        this.searchSetTime = searchSetTime;
    }

    public String getSearchSetUrl() {
        return searchSetUrl;
    }

    public void setSearchSetUrl(String searchSetUrl) {
        this.searchSetUrl = searchSetUrl;
    }

    public void setDataTime(long dataTime) {
        this.dataTime = dataTime;
    }

    public void setDataListUrl(String dataListUrl) {
        this.dataListUrl = dataListUrl;
    }

    public void setFieldTime(long FieldTime) {
        this.FieldTime = FieldTime;
    }

    public void setFieldUrl(String FieldUrl) {
        this.FieldUrl = FieldUrl;
    }

    public void setButtonTime(long buttonTime) {
        this.buttonTime = buttonTime;
    }

    public void setButtonUrl(String buttonUrl) {
        this.buttonUrl = buttonUrl;
    }

    public void setOperaButtonTime(long OperaButtonTime) {
        this.OperaButtonTime = OperaButtonTime;
    }

    public void setOperaButtonUrl(String OperaButtonUrl) {
        this.OperaButtonUrl = OperaButtonUrl;
    }

    public long getDataTime() {
        return dataTime;
    }

    public String getDataListUrl() {
        return dataListUrl;
    }

    public long getFieldTime() {
        return FieldTime;
    }

    public String getFieldUrl() {
        return FieldUrl;
    }

    public long getButtonTime() {
        return buttonTime;
    }

    public String getButtonUrl() {
        return buttonUrl;
    }

    public long getOperaButtonTime() {
        return OperaButtonTime;
    }

    public String getOperaButtonUrl() {
        return OperaButtonUrl;
    }
}
