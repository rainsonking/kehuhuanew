package com.kwsoft.kehuhua.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;

/**
 * Created by Administrator on 2016/10/21 0021.
 *
 */

public class EmptyViewHolder extends RecyclerView.ViewHolder {
    public TextView mTextView;
    public EmptyViewHolder(View itemView) {
        super(itemView);
        mTextView = (TextView)itemView.findViewById(R.id.textView_null);
    }
}
