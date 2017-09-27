package com.zero.customview.view.bottombar;

import android.content.Context;
import android.content.pm.ProviderInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/27 0027 16:05
 */

public class ContactTab extends AppCompatImageView {
    private final float MAX_POSITION = 4;
    private final int PAINT_SIZE = 4;
    private Context mContext;
    private Paint mPaint;
    private int mViewColor;
    private float mPosition;
    private int mWidth;
    private int mHeight;
    private float centerX;
    private float centerY;
    private Bitmap maskBmp;

    public ContactTab(Context context) {
        this(context, null);
    }

    public ContactTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContactTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(PAINT_SIZE);
        mViewColor = Color.WHITE;
        mPosition = 0.0f;
    }

    public void updateColor(int color) {
        this.mViewColor = color;
        invalidate();
    }

    public void updateAnimation(float scaleValue) {
        this.mPosition = MAX_POSITION * (1 - scaleValue);
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
        float headSize = mHeight / 3 / 2.0f;
        float bodySize = mHeight / 3;
        float offsetX = mPosition*2;
        float offsetY = mPosition;
//        mPaint.setXfermode(null);
        mPaint.setColor(mViewColor);
        /***    Draw head   ***/
        canvas.drawCircle(centerX-offsetX, centerY - headSize+offsetY, headSize, mPaint);
        /***    Draw body   ***/
        RectF bodyRect = new RectF(centerX - bodySize-offsetX, centerY+offsetY,
                                centerX + bodySize-offsetX, centerY + 2 * bodySize+offsetY);
        canvas.drawArc(bodyRect, 180, 180, true, mPaint);
        if (mPosition > 0) {
            /*
            int sc = canvas.saveLayer(0,0, mWidth, mHeight,  null,
                    Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG
                            | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                            | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                            | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
            */
            canvas.drawCircle(centerX+offsetX, centerY - headSize-offsetY, headSize, mPaint);
            RectF bodyCopyRect = new RectF(centerX - bodySize+offsetX, centerY-offsetY,
                    centerX + bodySize+offsetX, centerY + 2 * bodySize-offsetY);
            canvas.drawArc(bodyCopyRect, 180, 180, true, mPaint);
        }
    }
}
