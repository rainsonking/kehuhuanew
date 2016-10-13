package com.kwsoft.kehuhua.wechatPicture;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.animation.DecelerateInterpolator;

import com.kwsoft.kehuhua.adcustom.R;

import butterknife.Bind;

/**
 * 带Toolbar的基础Activity
 */
public abstract class ToolBarActivity extends BaseActivity {
    protected ActionBar actionBar;
    protected boolean isToolBarHiding = false;
    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.app_bar)
    protected AppBarLayout appBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolBar();
    }
    protected void initToolBar() {
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            //设置ToolBar返回
            actionBar.setDisplayHomeAsUpEnabled(canBack());
        }
    }
    /**
     * 设置 home icon 是否可见
     */
    protected boolean canBack() {
        return true;
    }
    protected void hideOrShowToolBar() {
        appBar.animate()
                .translationY(isToolBarHiding ? 0 : -appBar.getHeight())
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
        isToolBarHiding = !isToolBarHiding;
    }
}