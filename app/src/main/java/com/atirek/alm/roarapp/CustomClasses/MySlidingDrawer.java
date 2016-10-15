package com.atirek.alm.roarapp.CustomClasses;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SlidingDrawer;

/**
 * Created by Alm on 6/29/2016.
 */
public class MySlidingDrawer extends SlidingDrawer {
    public MySlidingDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySlidingDrawer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
