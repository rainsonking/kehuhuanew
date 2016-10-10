package com.kwsoft.version.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Administrator on 2016/10/8 0008.
 *
 */

public class StudyGridView extends GridView {
    public StudyGridView(Context context) {
        super(context);
    }

    public StudyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StudyGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
