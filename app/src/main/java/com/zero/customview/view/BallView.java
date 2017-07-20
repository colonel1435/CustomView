package com.zero.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.CorrectionInfo;

import com.zero.customview.R;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/7/20 0020 17:56
 */

public class BallView extends View {
    private int bgColor;
    private int circleColor;
    private float radius;

    private Paint circlePaint;
    public BallView(Context context) {
        this(context, null);
    }

    public BallView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BallView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BallView, defStyleAttr, 0);
        bgColor = ta.getColor(R.styleable.BallView_ballColor, Color.parseColor("#ffffff"));
        circleColor = ta.getColor(R.styleable.BallView_circleColor, Color.parseColor("#ffffff"));
        radius = ta.getDimension(R.styleable.BallView_ballRadius, 96);
        ta.recycle();

        initView();
    }

    private void initView() {
        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(circleColor);
    }
}
