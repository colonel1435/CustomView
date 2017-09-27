package com.zero.customview.view.bottombar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/27 0027 14:42
 */

public class MenuTab extends AppCompatImageView {
    private Context mContext;
    private final float DOT_MAX_POSITION = 17.0f;
    private final float DOT_MIN_POSITION = 16.0f;
    private final float DOT_RADIUS = 8.0f;
    private Paint mPaint;
    private int mViewColor;
    private float mDotRadius;
    private float mPosition;
    private int mWidth;
    private int mHeight;
    private float centerX;
    private float centerY;

    public MenuTab(Context context) {
        this(context, null);
    }

    public MenuTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mViewColor = Color.WHITE;
        mDotRadius = DOT_RADIUS;
        mPosition = DOT_MIN_POSITION;
    }

    public void updateColor(int color) {
        this.mViewColor = color;
        invalidate();
    }

    public void  updateAnimation(float scaleValue) {
        this.mPosition = DOT_MIN_POSITION + (1-scaleValue)*DOT_MAX_POSITION;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        centerX = w / 2.0f;
        centerY = h / 2.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(mViewColor);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        for (int i = 0; i < 4; i++) {
            float x = centerX + (float)(mPosition*Math.cos(i*(Math.PI/2) + (Math.PI/4)));
            float y = centerY + (float)(mPosition*Math.sin(i*(Math.PI/2) + (Math.PI/4)));
            canvas.drawCircle(x, y, mDotRadius, mPaint);
        }
    }
}
