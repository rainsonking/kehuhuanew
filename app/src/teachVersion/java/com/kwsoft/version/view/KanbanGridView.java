package com.kwsoft.version.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Administrator on 2016/9/20 0020.
 */

public class KanbanGridView extends GridView {
    public KanbanGridView(Context context) {
        super(context);
    }

    public KanbanGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KanbanGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
