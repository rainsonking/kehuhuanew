package com.kwsoft.kehuhua.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.TintTypedArray;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;


/**
 * Created by Administrator on 2016/9/27 0027.
 *
 */

public class CommonToolbar extends Toolbar {


    private View mView;
    private TextView mTextTitle;
    private ImageButton mRightImageButton,mLeftImageButton;
    private ImageView isChildIv;

    public CommonToolbar(Context context) {
        this(context,null);
    }

    public CommonToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);



        initView();
        setContentInsetsRelative(10,10);




        if(attrs !=null) {
            final TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs,
                    R.styleable.CommonToolbar, defStyleAttr, 0);


            boolean isShowRightButton = a.getBoolean(R.styleable.CommonToolbar_isShowRightButton,false);
            boolean isHideLeftButton = a.getBoolean(R.styleable.CommonToolbar_isHideLeftButton,false);
            boolean isShowTitle = a.getBoolean(R.styleable.CommonToolbar_isShowTitle,false);
            if(isHideLeftButton){

                hideLeftImageButton();

            }

            if(isShowRightButton){

                showRightImageButton();

            }
            if(isShowTitle){

                showTitle();

            }

            a.recycle();
        }

    }

    public void hideLeftImageButton() {

        if(mLeftImageButton !=null)
            mLeftImageButton.setVisibility(GONE);


    }


    private void initView() {
        if(mView == null) {
            LayoutInflater mInflater = LayoutInflater.from(getContext());
            mView = mInflater.inflate(R.layout.common_toolbar_child_page, null);
            mTextTitle = (TextView) mView.findViewById(R.id.child_toolbar_title);
            isChildIv= (ImageView) mView.findViewById(R.id.is_child_iv);
            mRightImageButton = (ImageButton) mView.findViewById(R.id.child_toolbar_rightButton);
            mLeftImageButton = (ImageButton) mView.findViewById(R.id.child_toolbar_leftButton);
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);
            addView(mView, lp);
        }
    }


    public  void setRightButtonOnClickListener(OnClickListener li){

        mRightImageButton.setOnClickListener(li);
    }
    public  void setTextTitleOnClickListener(OnClickListener li){

        mTextTitle.setOnClickListener(li);
    }
    public  void setLeftButtonOnClickListener(OnClickListener li){

        mLeftImageButton.setOnClickListener(li);
    }

    public void showRightImageButton(){
        if(mRightImageButton !=null)
            mRightImageButton.setVisibility(VISIBLE);
    }

    public void showTitle() {
        if(mTextTitle !=null)
            mTextTitle.setVisibility(VISIBLE);
    }

    public void showChildIv() {
        if(isChildIv !=null)
            isChildIv.setVisibility(VISIBLE);
    }

    @Override
    public void setTitle(int resId) {

        setTitle(getContext().getText(resId));
    }

    @Override
    public void setTitle(CharSequence title) {

        initView();
        if(mTextTitle !=null) {
            mTextTitle.setText(title);
        }
    }

    public View getRightButton(){

        return mRightImageButton;

    }

        public void  setRightButtonIcon(Drawable icon){

        if(mRightImageButton !=null){

            mRightImageButton.setImageDrawable(icon);
//            mRightImageButton.setVisibility(VISIBLE);
        }

    }


        public void  setLeftButtonIcon(Drawable icon){

        if(mLeftImageButton !=null){

            mLeftImageButton.setImageDrawable(icon);
//            mLeftImageButton.setVisibility(VISIBLE);
        }

    }
}
