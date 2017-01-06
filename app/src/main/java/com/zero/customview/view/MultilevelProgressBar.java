package com.zero.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.zero.customview.R;

/**
 * Created by Administrator on 2017/1/6.
 */

public class MultilevelProgressBar extends ProgressBar {
    private int mFirstColor;
    private int mSecondColor;
    private int mCircleWidth;
    private Paint mPaint;
    private int mProgress;
    private int mSpeed;
    private boolean isNext = false;

    public MultilevelProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initSetup(attrs);
    }

    public MultilevelProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSetup(attrs);
    }

    private void initSetup(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MultiLevelProgressBar);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.MultiLevelProgressBar_firstColor:
                    mFirstColor = a.getColor(attr, Color.GREEN);
                    break;
                case R.styleable.MultiLevelProgressBar_secondColor:
                    mSecondColor = a.getColor(attr, Color.RED);
                    break;
                case R.styleable.MultiLevelProgressBar_circleWidth:
                    mCircleWidth = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_PX, 20, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.MultiLevelProgressBar_speed:
                    mSpeed = a.getInt(attr, 20);// 默认20
                    break;
            }
        }
        a.recycle();
        mPaint = new Paint();
        // 绘图线程
        new Thread()
        {
            public void run()
            {
                while (true)
                {
                    mProgress++;
                    if (mProgress == 360)
                    {
                        mProgress = 0;
                        if (!isNext)
                            isNext = true;
                        else
                            isNext = false;
                    }
                    postInvalidate();
                    try
                    {
                        Thread.sleep(mSpeed);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            };
        }.start();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        int centre = getWidth() / 2;
        int radius = centre - mCircleWidth / 2;
        mPaint.setStrokeWidth(mCircleWidth);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        RectF oval = new RectF(centre - radius, centre - radius, centre + radius, centre + radius);
        if (!isNext)
        {
            mPaint.setColor(mFirstColor);
            canvas.drawCircle(centre, centre, radius, mPaint);
            mPaint.setColor(mSecondColor);
            canvas.drawArc(oval, -90, mProgress, false, mPaint);
        } else
        {
            mPaint.setColor(mSecondColor);
            canvas.drawCircle(centre, centre, radius, mPaint);
            mPaint.setColor(mFirstColor);
            canvas.drawArc(oval, -90, mProgress, false, mPaint); 
        }
    }
}
