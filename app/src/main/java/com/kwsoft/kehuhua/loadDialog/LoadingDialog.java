package com.kwsoft.kehuhua.loadDialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;

/**
 * Created by Administrator on 2016/10/20 0020.
 *
 */

public class LoadingDialog {
    private EdusCircularRing mLoadingView;
    private Dialog mLoadingDialog;

    public LoadingDialog(Context context, String msg) {
        // 首先得到整个View
        View view = LayoutInflater.from(context).inflate(
                R.layout.loading_dialog_view, null);
        // 获取整个布局
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.dialog_view);
        // 页面中的LoadingView
        mLoadingView = (EdusCircularRing) view.findViewById(R.id.lv_circularring);
        // 页面中显示文本
        TextView loadingText = (TextView) view.findViewById(R.id.loading_text);
        // 显示文本
        loadingText.setText(msg);
        // 创建自定义样式的Dialog
        mLoadingDialog = new Dialog(context, R.style.load_dialog);
        // 设置返回键无效
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
    }

    public void show(){
        if (mLoadingDialog!=null) {
            mLoadingDialog.show();
            mLoadingView.startAnim();
        }

    }

    public void dismiss(){
        if (mLoadingDialog!=null) {
            mLoadingView.stopAnim();
            mLoadingDialog.dismiss();
//            mLoadingDialog=null;//原本类中带=null，将其删除，否则show方法无法执行第二次
        }
    }
}
