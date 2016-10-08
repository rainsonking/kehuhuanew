package com.kwsoft.kehuhua.adcustom.config;

/**
 * Created by Administrator on 2016/5/17 0017.
 */
public class Url {
    //基本地址
    public final static String baseUrl = "http://192.168.6.150:8081/edus_auto/";
    //登陆接口
    public final static String loginUrl = "login_interfaceLogin.do?proIdName=5704e45c7cf6c0b2d9873da6&alterTime=&source=1";

    //请求消息列表
    public final static String getMsgUrl = "mongoModel_interfaceSysMessList.do?";

    //改变阅读状态
    public final static String changeReadState="msgSet_toUpdateMsgIfSeeInterface.do?";
}
