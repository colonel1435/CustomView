package com.zero.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.CorrectionInfo;

import com.zero.customview.R;
import com.zero.customview.utils.DisplayUtils;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/7/20 0020 17:56
 */

public class BallView extends View {
    private int bgColor;
    private int circleColor;
    private float circleWidth;
    private float radius;

    private float defaultPadding;
    private Paint circlePaint;
    private Canvas mCanvas;
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
        radius = ta.getDimension(R.styleable.BallView_ballRadius, DisplayUtils.sp2px(context,32));
        ta.recycle();

        initView();
    }

    private void initView() {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(circleColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureDimension(widthMeasureSpec), measureDimension(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;
        mCanvas.drawCircle(getWidth()/2, getHeight()/2, radius, circlePaint);
    }


    private int measureDimension(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = 30;
            if (mode == MeasureSpec.AT_MOST) {
                result = (int)radius * 2;
            }
        }
        return result;
    }

}
