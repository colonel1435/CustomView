package com.zero.customview.view.captcha;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.zero.customview.R;

/**
 * Description
 *
 * @author : Mr.wuming
 * @email : fusu1435@163.com
 * @date : 2018/2/7 0007 8:59
 */

public class CaptchaView extends LinearLayout {

    private CaptchaImageView captchaImage;
    private CaptchaSlideView captchaSlide;
    public CaptchaView(Context context) {
        this(context, null);
    }

    public CaptchaView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptchaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setOrientation(VERTICAL);
        View view = findViewById(R.id.captcha);
        captchaImage = (CaptchaImageView) view.findViewById(R.id.captcha_image);
        captchaSlide = (CaptchaSlideView) view.findViewById(R.id.captcha_slide);
        captchaSlide.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                captchaImage.setCurrentPosition(progress/captchaSlide.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
}
