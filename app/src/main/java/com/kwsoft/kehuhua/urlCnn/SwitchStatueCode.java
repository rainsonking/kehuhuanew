package com.kwsoft.kehuhua.urlCnn;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/10/27 0027.
 *
 */

public class SwitchStatueCode {

    static int statusCode=0;//

    public static void netToast(Activity activity) {

        switch(statusCode){
            case 500:Toast.makeText(activity, "服务器内部错误",Toast.LENGTH_SHORT).show();break;
            case 501:Toast.makeText(activity, "服务器无法识别请求",Toast.LENGTH_SHORT).show();break;
            case 502:Toast.makeText(activity, "服务器网关错误",Toast.LENGTH_SHORT).show();break;
            case 503:Toast.makeText(activity, "服务器暂时不可用",Toast.LENGTH_SHORT).show();break;
            case 504:Toast.makeText(activity, "服务器网关超时",Toast.LENGTH_SHORT).show();break;
            case 505:Toast.makeText(activity, "服务器不支持的HTTP版本",Toast.LENGTH_SHORT).show();break;

            case 400:Toast.makeText(activity, "错误请求",Toast.LENGTH_SHORT).show();break;
            case 401:Toast.makeText(activity, "身份验证错误",Toast.LENGTH_SHORT).show();break;
            case 403:Toast.makeText(activity, "服务器拒绝",Toast.LENGTH_SHORT).show();break;
            case 404:Toast.makeText(activity, "服务器找不到请求的数据",Toast.LENGTH_SHORT).show();break;
            case 405:Toast.makeText(activity, "服务器方法禁用",Toast.LENGTH_SHORT).show();break;
            case 406:Toast.makeText(activity, "服务器不接受",Toast.LENGTH_SHORT).show();break;
            case 407:Toast.makeText(activity, "需要代理授权",Toast.LENGTH_SHORT).show();break;
            case 408:Toast.makeText(activity, "请求超时",Toast.LENGTH_SHORT).show();break;
            case 409:Toast.makeText(activity, "服务端请求冲突",Toast.LENGTH_SHORT).show();break;
            case 410:Toast.makeText(activity, "服务器资源已删除",Toast.LENGTH_SHORT).show();break;
            case 411:Toast.makeText(activity, "请求未达有效长度",Toast.LENGTH_SHORT).show();break;
            case 412:Toast.makeText(activity, "未满足服务器前提条件",Toast.LENGTH_SHORT).show();break;
            case 413:Toast.makeText(activity, "请求超出服务器的处理能力",Toast.LENGTH_SHORT).show();break;
            case 414:Toast.makeText(activity, " URI 过长服务器无法处理",Toast.LENGTH_SHORT).show();break;
            case 415:Toast.makeText(activity, "不支持的媒体类型",Toast.LENGTH_SHORT).show();break;
            case 416:Toast.makeText(activity, "请求范围不符合要求",Toast.LENGTH_SHORT).show();break;
            case 417:Toast.makeText(activity, "服务器未符合给定预期值",Toast.LENGTH_SHORT).show();break;
            default:
                break;
        }


    }
}
