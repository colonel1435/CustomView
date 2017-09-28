package com.zero.customview.view.bottombar;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by zero on 2017/9/24.
 */

public abstract class BottomTab extends AppCompatImageView {
    public BottomTab(Context context) {
        super(context);
    }

    public BottomTab(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    abstract void updateColor(int color);
    abstract void updateAnimation(float scale);
}
