package com.zero.customview.adapter.banner;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

/**
 * Created by zero on 2017/12/25.
 */

public class ScalePageTransformer implements ViewPager.PageTransformer {
    private static final String TAG = ScalePageTransformer.class.getSimpleName() + "wumin";
    private static final float DEFAULT_SCALE_MAX = 0.8f;
    private static final float DEFAULT_ALPHA_MAX = 0.4f;
    @Override
    public void transformPage(View page, float position) {
        Log.d(TAG, "transformPage: " + position);
        float scale = (position < 0)
                ? ((1 - DEFAULT_SCALE_MAX) * position + 1)
                : ((DEFAULT_SCALE_MAX - 1) * position + 1);
        float alpha = (position < 0)
                ? ((1 - DEFAULT_ALPHA_MAX) * position + 1)
                : ((DEFAULT_ALPHA_MAX - 1) * position + 1);
        if (position < 0) {
            page.setPivotX(page.getWidth());
            page.setPivotY(page.getHeight()/2);
        } else {
            page.setPivotX(0);
            page.setPivotY(page.getHeight()/2);
        }
        page.setScaleX(scale);
        page.setScaleY(scale);
        page.setAlpha(Math.abs(alpha));
    }
}
