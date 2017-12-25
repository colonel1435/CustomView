package com.zero.customview.adapter.banner;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by zero on 2017/12/25.
 */

public class ScalePageTransformer implements ViewPager.PageTransformer {
    private static final float DEFAULT_SCALE_MAX = 0.8f;
    private static final float DEFAULT_ALPHA_MAX = 0.5f;
    @Override
    public void transformPage(View page, float position) {
        float scale = position < 0 ?
                (1 + position) * DEFAULT_SCALE_MAX
                : (position - 1) * DEFAULT_SCALE_MAX;

        page.setScaleX(scale);
        page.setScaleY(scale);
    }
}
