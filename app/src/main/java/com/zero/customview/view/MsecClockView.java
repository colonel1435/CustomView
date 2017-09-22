package com.zero.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.zero.customview.R;
import com.zero.customview.utils.DisplayUtils;

import java.util.Calendar;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/7/18 0018 15:21
 */

public class MsecClockView extends View {
    private int mType;
    private int bgColor;
    private int handColor;
    private float handWidth;
    private float mRadius;
    private float mDefaultPadding;
    private float mAngle;
    private Canvas mCanvas;
    private Paint handPaint;
    private Path handPath;
    private Paint circlePaint;

    private final int TYPE_MSEC = 1;
    private final int TYPE_SEC = 2;
    private final int TYPE_MIN = 3;
    private final int TYPE_HOUR = 4;

    public MsecClockView(Context context) {
        this(context, null);
    }

    public MsecClockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MsecClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MsecClockView, defStyleAttr, 0);
        mType = ta.getInt(R.styleable.MsecClockView_msecType, TYPE_MSEC);
        handColor = ta.getColor(R.styleable.MsecClockView_handColor, Color.parseColor("#ffffff"));
        handWidth = ta.getDimension(R.styleable.MsecClockView_handSize, DisplayUtils.sp2px(context, 4));
        bgColor = ta.getColor(R.styleable.MsecClockView_coverColor, Color.parseColor("#237EAD"));
        mDefaultPadding = ta.getDimension(R.styleable.MsecClockView_msecPadding, DisplayUtils.sp2px(context, 5));
        mRadius = ta.getDimension(R.styleable.MsecClockView_msecRadius, 0);
        ta.recycle();

        initView();

    }

    private void initView() {
        setBackgroundColor(bgColor);
        handPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        handPaint.setStyle(Paint.Style.FILL);
        handPaint.setColor(handColor);

        handPath = new Path();

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(handWidth);
        circlePaint.setColor(handColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureDimension(widthMeasureSpec), measureDimension(heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mRadius == 0) {
            mRadius = Math.min(w - getPaddingLeft() - getPaddingRight() - mDefaultPadding,
                    h - getPaddingTop() - getPaddingBottom() - mDefaultPadding) / 2;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;
        getMsec();
        drawCircle();
        drawHand();
        drawCover();
        invalidate();
    }

    private void getMsec() {
        Calendar calendar = Calendar.getInstance();
        float millionSecond = calendar.get(Calendar.MILLISECOND);
        float second = calendar.get(Calendar.SECOND) + millionSecond / 1000;
        float minute = calendar.get(Calendar.MINUTE) + second / 60;
        float hour = calendar.get(Calendar.HOUR) + minute / 60;
        switch (mType) {
            case TYPE_MSEC:
                mAngle = millionSecond / 1000 * 360;
                break;
            case TYPE_SEC:
                mAngle = second / 60 * 360;
                break;
            case TYPE_MIN:
                mAngle = minute / 30 * 360;
                break;
            case TYPE_HOUR:
                mAngle = hour / 24 * 360;
                break;
            default:
                break;
        }

    }

    private void drawCircle() {
        mCanvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius, circlePaint);
    }

    private void drawHand() {
        mCanvas.save();
        mCanvas.rotate(mAngle, getWidth() / 2, getHeight() / 2);
        handPath.reset();
        float offset = 10;
        handPath.moveTo(getWidth() / 2 - 0.01f * mRadius, getHeight() / 2 - 0.03f * mRadius);
        handPath.lineTo(getWidth() / 2 - 0.008f * mRadius, offset + 0.365f * mRadius);
        handPath.quadTo(getWidth() / 2, offset + 0.345f * mRadius,
                getWidth() / 2 + 0.008f * mRadius, offset + 0.365f * mRadius);
        handPath.lineTo(getWidth() / 2 + 0.01f * mRadius, getHeight() / 2 - 0.03f * mRadius);
        handPath.close();
        handPaint.setColor(handColor);
        handPaint.setStyle(Paint.Style.FILL);
        mCanvas.drawPath(handPath, handPaint);
        mCanvas.restore();
    }

    private void drawCover() {
        mCanvas.save();
        mCanvas.drawCircle(getWidth() / 2, getHeight() / 2, 0.05f * mRadius, handPaint);
        handPaint.setColor(bgColor);
        mCanvas.drawCircle(getWidth() / 2, getHeight() / 2, 0.025f * mRadius, handPaint);
        mCanvas.restore();
    }

    private int measureDimension(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = 480;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

}
