package com.zero.customview.view.bottombar;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/28 0028 11:21
 */

public class NormTab extends BottomTab{
    public NormTab(Context context) {
        super(context);
    }

    public NormTab(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NormTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void updateColor(int color) {
        this.setColorFilter(color);
    }

    @Override
    public void updateAnimation(float scale) {

    }
}
