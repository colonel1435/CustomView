package com.zero.customview.view.captcha;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Description
 *
 * @author : Mr.wuming
 * @email : fusu1435@163.com
 * @date : 2018/2/7 0007 16:14
 */

public class CaptchaSlideView extends View implements CaptchaSlideCall {
    public CaptchaSlideView(Context context) {
        super(context);
    }

    public CaptchaSlideView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CaptchaSlideView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onSlide(float value) {

    }
}
