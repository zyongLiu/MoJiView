package com.liu.library.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * Created by Liu on 2017/1/16.
 */
public class ForecaseScrollView extends HorizontalScrollView {
    public ForecaseScrollView(Context context) {
        this(context, null);
    }

    public ForecaseScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ForecaseScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        View view=getChildAt(0);
        if (view instanceof ForecaseView){
            ((ForecaseView)view).onScrollChanged(l,oldl,getWidth());
        }
    }
}
