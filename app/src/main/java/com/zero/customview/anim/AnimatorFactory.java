package com.zero.customview.anim;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/11/11 0011 9:12
 */

public class AnimatorFactory {
    private static final int CLICK_ANIMATOR_DURATION = 250;

    public static ObjectAnimator getClickScaleAnimtor(View view) {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.2f, 1.0f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.2f, 1.0f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY);
        animator.setDuration(CLICK_ANIMATOR_DURATION);

        return animator;
    }
}
