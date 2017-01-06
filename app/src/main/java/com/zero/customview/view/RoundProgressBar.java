package com.zero.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaRecorder;
import android.util.AttributeSet;
import android.webkit.WebIconDatabase;
import android.widget.CursorAdapter;
import android.widget.MediaController;
import android.widget.ProgressBar;

import com.zero.customview.R;

/**
 * Created by Administrator on 2017/1/5.
 */

public class RoundProgressBar extends HorizontalProgressBar {
    private int mRadius = 100;

    public RoundProgressBar(Context context) {
        super(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mReachedBarHeight = (int)(mUnreachedBarHeight*1.5f);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.HorizontalProgressBar);
        mRadius = (int)ta.getDimension(R.styleable.RoundProgressBar_radius, mRadius);
        ta.recycle();
        mTextSize = 16;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        String text = getProgress() + "%";
        float textWidth = mPaint.measureText(text);
        float textHeight = (mPaint.descent() + mPaint.ascent())/2;

        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        mPaint.setStyle(Paint.Style.STROKE);

        mPaint.setColor(mUnreachedBarColor);
        mPaint.setStrokeWidth(mUnreachedBarHeight);
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);

        mPaint.setColor(mReachedBarColor);
        mPaint.setStrokeWidth(mReachedBarHeight);
        float sweepAngle = getProgress()*1.0f / getMax() * 360;
        canvas.drawArc(new RectF(0, 0, mRadius*2, mRadius*2), 0, sweepAngle, false, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(text, mRadius - textWidth/2, mRadius - textHeight, mPaint);
        canvas.restore();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int paintWidth = Math.max(mReachedBarHeight, mUnreachedBarHeight);
        if (heightMode != MeasureSpec.EXACTLY) {
            int exceptHeight = (int)(getPaddingTop() + getPaddingBottom() + mRadius*2 + paintWidth);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(exceptHeight, MeasureSpec.EXACTLY);
        }
        if (widthMode != MeasureSpec.EXACTLY) {
            int exceptWidth = (int) (getPaddingLeft() + getPaddingRight() + mRadius*2 + paintWidth);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(exceptWidth, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
