package com.zero.customview.view.flip;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.zero.customview.R;

/**
 * Description
 *
 * @author : Mr.wuming
 * @email : fusu1435@163.com
 * @date : 2017/12/5 0005 16:39
 */

public class FlipLayout extends LinearLayout {
    private final String TAG = this.getClass().getSimpleName()+"@wuming";
    private static final int DEFAULT_FLIP_DURATION = 1000;
    private Context mContext;
    private Camera mCamera;
    private float rotateDegree;

    private int mWidth;
    private int mHeight;
    private float mCenterX;
    private float mCenterY;

    private int lastX;
    private int lastY;
    public FlipLayout(Context context) {
        this(context, null);
    }

    public FlipLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlipLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorLightBlue));
        mCamera = new Camera();
        rotateDegree = 0f;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mCenterX = getPaddingLeft() + w / 2 - getPaddingRight();
        mCenterY = getPaddingTop() + h / 2 - getPaddingBottom();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw: rotate -> " + rotateDegree);
        canvas.save();
        mCamera.save();
        canvas.translate(mCenterX, mCenterY);
        mCamera.rotateX(rotateDegree);
        mCamera.applyToCanvas(canvas);
        canvas.translate(-mCenterX, -mCenterY);
        mCamera.restore();
        super.onDraw(canvas);
        canvas.drawCircle(mCenterX, mCenterY, 50, new Paint());
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent: down");
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onTouchEvent: lastX -> " + lastY + " lastY -> " + lastY
                + " x -> " + x + " y -> " + y);
                int delta = y - lastY;
                rotateDegree = (delta/mCenterY)*180;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchEvent: up");
                break;
            default:
                break;
        }
        return true;
    }

    public float getRotateDegree() {
        return rotateDegree;
    }

    public void setRotateDegree(float mRotateDegree) {
        this.rotateDegree = mRotateDegree;
        invalidate();
    }
}
