package com.zero.customview.view.captcha;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;

import com.zero.customview.R;
import com.zero.customview.utils.DisplayUtils;

/**
 * Description
 *
 * @author : Mr.wuming
 * @email : fusu1435@163.com
 * @date : 2018/2/7 0007 16:14
 */

public class CaptchaSlideView extends AppCompatSeekBar{
    private final static String DEFAULT_TIPS = "向左滑动完成验证";
    private final static float DEFAULT_TIP_SIZE = 12;
    private String tips;
    private int tipColor;
    private float tipSize;
    private Paint mTipsPaint;
    protected CaptchaSlideCall slideCall;

    public CaptchaSlideView(Context context) {
        this(context, null);
    }

    public CaptchaSlideView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptchaSlideView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CaptchaView);
        tips = ta.getString(R.styleable.CaptchaView_captcha_tips);
        tipColor = ta.getColor(R.styleable.CaptchaView_captcha_tip_color, Color.BLACK);
        tipSize = ta.getDimension(R.styleable.CaptchaView_captcha_tip_size,
                DisplayUtils.sp2px(context, DEFAULT_TIP_SIZE));
        ta.recycle();
        initViews();
    }

    private void initViews() {
        mTipsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTipsPaint.setTextAlign(Paint.Align.CENTER);
        mTipsPaint.setColor(tipColor);
        mTipsPaint.setTextSize(tipSize);

        if(null == tips || "".equals(tips)) {
            tips = DEFAULT_TIPS;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint.FontMetrics fontMetrics = mTipsPaint.getFontMetrics();
        float offset = -(fontMetrics.descent + fontMetrics.ascent)/2f;
        canvas.drawText(tips, getWidth() / 2f, getHeight() / 2f + offset,
                mTipsPaint);
    }
}
