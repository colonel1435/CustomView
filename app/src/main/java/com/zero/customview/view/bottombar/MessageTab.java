package com.zero.customview.view.bottombar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/27 0027 8:48
 */

public class MessageTab extends AppCompatImageView {
    private final String TAG = this.getClass().getSimpleName() + "@wumin";
    public enum MessageAlign {TOP, CENTER}
    private final int DOT_CIRCLE = 4;
    private Context mContext;
    private Paint mPaint;
    private int mViewColor = Color.WHITE;
    private float topOffset = 12f;
    private float leftOffset = 2f;
    private float rightOffset = 10f;
    private float bottomOffset = 12f;
    private float mRadius;
    private float mRoundRadius = 10f;
    private int mWidth;
    private int mHeight;
    private float centerX;
    private float centerY;
    private MessageAlign mAlign;



    public MessageTab(Context context) {
        this(context, null);
    }

    public MessageTab(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageTab(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mViewColor);
        mPaint.setDither(true);

        mRadius = 0f;
        mAlign = MessageAlign.CENTER;
    }

    private void setMessageAlign(MessageAlign align) {
        this.mAlign = align;
        invalidate();
    }

    public void updateColor(int color) {
        this.mViewColor = color;
        invalidate();
    }

    public void updateRadius(float scaleValue) {
        this.mRadius = (1-scaleValue)*DOT_CIRCLE;
        Log.d(TAG, "updateRadius: mRadius -> " + mRadius + " Scale -> " + scaleValue);
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
        centerX = w / 2;
        centerY = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float triangleWidth = (mWidth-2*mRoundRadius) * 0.4f;
        float triangleHeight = triangleWidth*0.4f;
        float left = leftOffset + getPaddingLeft();
        float top = topOffset + getPaddingTop();
        float right = mWidth - rightOffset - getPaddingRight();
        float bottom = mHeight - bottomOffset - getPaddingBottom();
        mPaint.setColor(mViewColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(DOT_CIRCLE);
        Path path = new Path();
        if (mAlign == MessageAlign.TOP) {
            path.moveTo(left, top);
            path.lineTo(right, top);
            path.lineTo(right, bottom);
            path.lineTo(right/2, bottom);
            path.lineTo(right/2 - triangleWidth/3*2, bottom + triangleHeight);
            path.lineTo(right/2 - triangleWidth, bottom);
            path.lineTo(left, bottom);
            path.close();
            canvas.drawPath(path, mPaint);

            if (mRadius > 0) {
                mPaint.setStrokeWidth(1);
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                canvas.drawCircle(right, top, mRadius*2f, mPaint);
            }
        } else if (mAlign == MessageAlign.CENTER) {
            triangleWidth *= 0.8f;
            path.moveTo(left, top+mRoundRadius);
            RectF ltRect = new RectF(left, top, left + 2*mRoundRadius, top + 2*mRoundRadius);
            path.arcTo(ltRect, 180, 90);
            path.lineTo(right - mRoundRadius, top);
            RectF rtRect = new RectF(right - 2*mRoundRadius, top, right, top + 2*mRoundRadius);
            path.arcTo(rtRect, 270, 90);
            path.lineTo(right, bottom - mRoundRadius);
            RectF rbRect = new RectF(right - 2*mRoundRadius, bottom - 2*mRoundRadius, right, bottom);
            path.arcTo(rbRect, 0, 90);
            path.lineTo(right/2, bottom);
            path.lineTo(right/2 - triangleWidth/3*2, bottom + triangleHeight);
            path.lineTo(right/2 - triangleWidth, bottom);
            path.lineTo(left+mRoundRadius, bottom);
            RectF lbRect = new RectF(left, bottom - 2*mRoundRadius, left + 2*mRoundRadius, bottom);
            path.arcTo(lbRect, 90, 90);
            path.close();
            canvas.drawPath(path, mPaint);

            if (mRadius > 0) {
                mPaint.setStrokeWidth(1);
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                float space = (right - left)/3;
                canvas.drawCircle(space, centerY, mRadius, mPaint);
                canvas.drawCircle(right - space, centerY, mRadius, mPaint);
            }
        }

    }


}