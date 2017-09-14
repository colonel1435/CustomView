package com.zero.customview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.LabeledIntent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.renderscript.Sampler;
import android.util.AttributeSet;
import android.util.EventLog;
import android.view.MotionEvent;

import uk.co.senab.photoview.PhotoView;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/14 0014 8:55
 */

public class DragPhotoView extends PhotoView {

    private Paint mPaint;
    private float mDownX;
    private float mDownY;

    private float mTranslateY;
    private float mTranslateX;
    private float mScale = 1;
    private int mWidth;
    private int mHeight;
    private float mMinScale = 0.5f;
    private int mAlpha = 255;
    private final static int MAX_TRANSLATE_Y = 500;

    private final static long DURATION = 300;
    private boolean canFinish = false;
    private boolean isAnimate = false;

    //is event on PhotoView
    private boolean isTouchEvent = false;
    private OnTapListener mTapListener;
    private OnExitListener mExitListener;

    public DragPhotoView(Context context) {
        this(context, null);
    }

    public DragPhotoView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public DragPhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        initView();
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setAlpha(mAlpha);
        canvas.drawRect(0, 0, mWidth, mHeight, mPaint);
        canvas.translate(mTranslateX, mTranslateY);
        canvas.scale(mScale, mScale, mWidth / 2, mHeight / 2);
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (getScale() == 1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    onActionDown(event);
                    canFinish = !canFinish;

                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mTranslateY == 0 && mTranslateX != 0) {
                        //如果不消费事件，则不作操作
                        if (!isTouchEvent) {
                            mScale = 1;
                            return super.dispatchTouchEvent(event);
                        }
                    }

                    //single finger drag  down
                    if (mTranslateY >= 0 && event.getPointerCount() == 1) {
                        onActionMove(event);
                        //如果有上下位移 则不交给viewpager
                        if (mTranslateY != 0) {
                            isTouchEvent = true;
                        }
                        return true;
                    }

                    //防止下拉的时候双手缩放
                    if (mTranslateY >= 0 && mScale < 0.95) {
                        return true;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    //防止下拉的时候双手缩放
                    if (event.getPointerCount() == 1) {
                        onActionUp(event);
                        isTouchEvent = false;
                        //judge finish or not
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mTranslateX == 0 && mTranslateY == 0 && canFinish) {

                                    if (mTapListener != null) {
                                        mTapListener.onTap(DragPhotoView.this);
                                    }
                                }
                                canFinish = false;
                            }
                        }, 300);
                    }
            }
        }

        return super.dispatchTouchEvent(event);
    }

    private void onActionDown(MotionEvent event) {
        mDownX = event.getX();
        mDownY = event.getY();
    }

    private void onActionMove(MotionEvent event) {
        float moveX = event.getX();
        float moveY = event.getY();
        mTranslateX = mDownX - moveX;
        mTranslateY = mDownY - moveY;

        if (mTranslateY < 0) {
            mTranslateY = 0;
        }

        float percent = mTranslateY / MAX_TRANSLATE_Y;
        if (mScale > mMinScale && mScale <= 1f) {
            mScale = 1- percent;
            mAlpha = (int)(255*(1-percent));
            if (mAlpha > 255) {
                mAlpha = 255;
            } else if(mAlpha < 0) {
                mAlpha = 0;
            }
        }

        if (mScale < mMinScale) {
            mScale = mMinScale;
        } else if (mScale > 1f){
            mScale = 1;
        }

        invalidate();
    }

    private void onActionUp(MotionEvent event) {
        if (mTranslateY > MAX_TRANSLATE_Y) {
            if (mExitListener != null) {
                mExitListener.onExit(this, mTranslateX, mTranslateY, mWidth, mHeight);
            } else {
                throw new RuntimeException("Drag Photo View OnExitListener can't be null!");
            }
        } else {
            performAnimation();
        }
    }

    private void performAnimation() {
        getScaleAinm().start();
        getTranslateXAnim().start();
        getTranslateYAinm().start();
        getAlphaAnim().start();
    }

    private ValueAnimator getScaleAinm() {
        final ValueAnimator animator = ValueAnimator.ofFloat(mScale, 1);
        animator.setDuration(DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mScale = (float)animation.getAnimatedValue();
                invalidate();
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimate = false;
                animator.removeAllUpdateListeners();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                isAnimate = true;
            }
        });

        return animator;
    }

    private ValueAnimator getTranslateXAnim() {
        final ValueAnimator animator = ValueAnimator.ofFloat(mTranslateX, 0);
        animator.setDuration(DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mTranslateY = (float)animation.getAnimatedValue();
            }
        });

        return animator;
    }

    private ValueAnimator getTranslateYAinm() {
        final ValueAnimator animator = ValueAnimator.ofFloat(mTranslateY, 0);
        animator.setDuration(DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mTranslateY = (float)animation.getAnimatedValue();
            }
        });

        return animator;
    }

    private ValueAnimator getAlphaAnim() {
        final ValueAnimator animator = ValueAnimator.ofInt(mAlpha, 255);
        animator.setDuration(DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAlpha = (int)animation.getAnimatedValue();
            }
        });

        return animator;
    }


    public void onFinishAnim() {
        mTranslateX = -mWidth / 2 + mWidth * mScale / 2;
        mTranslateY = -mHeight / 2 + mHeight * mScale / 2;
        invalidate();
    }

    public DragPhotoView setMinmumScale(float scale) {
        mMinScale = scale;
        return this;
    }


    public DragPhotoView setOnTapListener(OnTapListener listener) {
        mTapListener = listener;
        return this;
    }

    public DragPhotoView setOnExitListener(OnExitListener listener) {
        mExitListener = listener;
        return this;
    }

    public interface OnTapListener {
        void onTap(DragPhotoView view);
    }

    public interface OnExitListener {
        void onExit(DragPhotoView view, float translateX, float translateY, float w, float h);
    }
}
