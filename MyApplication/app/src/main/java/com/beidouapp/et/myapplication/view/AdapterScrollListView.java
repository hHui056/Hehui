package com.beidouapp.et.myapplication.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 项目名称：MyApplication
 * 创建人：huzy
 * 创建时间:2017/3/2 14:41
 */
public class AdapterScrollListView extends ListView {
    public AdapterScrollListView(Context context) {
        super(context);
    }

    public AdapterScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdapterScrollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
