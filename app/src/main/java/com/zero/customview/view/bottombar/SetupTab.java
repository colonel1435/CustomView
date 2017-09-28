package com.zero.customview.view.bottombar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/27 0027 16:56
 */

public class SetupTab extends BottomTab{
    private final String TAG = this.getClass().getSimpleName() + "@wumin";
    private final float MAX_ROTATION = 90;
    private final float DOT_MAX_RADIUS = 4;
    private Paint mPaint;
    private int mViewColor;
    private float mRoateAngle;
    private float mDotRadius;
    private float mLineWidth;
    private float mLineSpace;
    private int mWidth;
    private int mHeight;
    private float centerX;
    private float centerY;

    public SetupTab(Context context) {
        this(context, null);
    }

    public SetupTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SetupTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mViewColor = Color.WHITE;
        mRoateAngle = 0.0f;
        mDotRadius = DOT_MAX_RADIUS;
    }

    @Override
    public void updateColor(int color) {
        mViewColor = color;
        invalidate();
    }

    @Override
    public void updateAnimation(float scaleValue) {
        float rotate = (1 - scaleValue) * MAX_ROTATION;
        this.setRotation(rotate);
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
        mLineWidth = Math.min(mWidth-getPaddingTop()-getPaddingBottom(),
                            mHeight - getPaddingLeft() - getPaddingRight()) * 0.6f;
        mLineSpace = mLineWidth / 3.0f;
        mPaint.setColor(mViewColor);
        float pos_x = centerX - mLineWidth / 2.0f;
        for (int i = 0; i < 3; i++) {
            float pos_y = centerY + (i - 1) * (mLineSpace + mDotRadius);
            canvas.drawCircle(pos_x - 2.0f * mDotRadius, pos_y, mDotRadius, mPaint);
            RectF lineRectf = new RectF(pos_x, pos_y - mDotRadius,
                                centerX + mLineWidth / 2.0f, pos_y + mDotRadius);
            canvas.drawRoundRect(lineRectf, 3, 3, mPaint);
        }
    }
}
