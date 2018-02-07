package com.zero.customview.view.captcha;

import android.content.Context;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Description
 *
 * @author : Mr.wuming
 * @email : fusu1435@163.com
 * @date : 2018/2/7 0007 16:13
 */

public class CaptchaImageView extends View implements CaptchaImageCall{
    public CaptchaImageView(Context context) {
        super(context);
    }

    public CaptchaImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CaptchaImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public Path onShape() {
        return null;
    }
}
