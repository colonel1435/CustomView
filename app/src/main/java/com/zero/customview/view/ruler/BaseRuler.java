package com.zero.customview.view.ruler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EdgeEffect;
import android.widget.OverScroller;

import com.zero.customview.utils.DisplayUtils;

/**
 * Description
 * @author : Mr.wuming
 * @email  : fusu1435@163.com
 * @date   : 2017/12/4 0004 13:41
 */

public abstract class BaseRuler extends View implements IRuler{
    protected static final String TAG = "BaseRuler@wumin";
    protected static final float DEFAULT_SCALE_SIZE = 16;
    protected static final float DEFAULT_SCALE_WIDTH = 2;
    protected static final float DEFAULT_BORDER_LINE_WIDTH = 1;
    protected static final float DEFAULT_CURRENT_LINE_WIDTH = 4;
    protected static final float DEFAULT_PADDING = 2;
    protected static final float DEFAULT_CURRENT_LINE_EHIGHT = 28;
    protected static final float DEFAULT_SCALE_BOLD_LINE_HEIGHT = 24;
    protected static final float DEFAULT_SCALE_LINE_HEIGHT = 12;
    protected static final int DEFAULT_SCALE_LINE_MAX = 30;
    protected static final int DEFAULT_SCALE_LINE_INT = 10;
    protected static final int DEFAULT_SCALE_RATIO = 1;
    protected static final int DEFAULT_CURRENT_NUMBER = 0;
    protected static final int DEFAULT_SCALE_NUMBER_PADDING = 16;
    protected static final int DEFAULT_NUMBER_MIN = 0;
    protected static final int DEFAULT_NUMBER_MAX = 200;
    protected static final int SCROLL_ANIMATION_DURATION = 300;
    protected static final int DEFAULT_EDGE_WIDTH = 64;
    protected Context mContext;
    protected boolean enableTopBorder;
    protected boolean enableBottomBorder;
    protected boolean mEnableEdge;
    protected int mBackgoundColor;
    protected Paint mBorderPaint;
    protected Paint mScalePaint;
    protected Paint mCurrentPaint;
    protected EdgeEffect mMinEdge;
    protected EdgeEffect mMaxEdge;
    protected int mBorderColor;
    protected int mScaleLineColor;
    protected int mScaleNumberColor;
    protected int mCurrentColor;
    protected int mEdgeColor;

    protected float mCurrentLineHeight;
    protected float mCurrentLineWidth;
    protected float mBoarderLineWidth;
    protected float mScaleLineHeight;
    protected float mScaleLineWidth;
    protected float mScaleBoldLineHeight;
    protected float mScaleSize;
    protected float mScaleNumberPadding;
    protected int mNumberMin;
    protected int mNumberMax;
    protected float mRulerLength;
    protected float mCurrentNumber;
    protected int mScaleRatio;
    protected int mScaleSpace;
    protected float minFingDistance;
    protected float minFingVelocity;
    protected float minPostion;
    protected float maxPosition;
    protected float stepMin;
    protected float stepMax;
    protected boolean leftToRight;
    protected int mEdgeWidth;

    protected int defaultWidth;
    protected int defaultHeight;
    protected int mWidth;
    protected int mHeight;
    protected float mCenterX;
    protected float mCenterY;
    protected float mLeft;
    protected float mTop;
    protected float mRight;
    protected float mBottom;
    protected float mScaleStepDist;
    protected float mScaleStepNumber;

    protected GestureDetector mGestureDetector;
    protected OverScroller mScroller;
    protected OnCurrentChangeListener mListener;
    public BaseRuler(Context context) {
        this(context, null);
    }

    public BaseRuler(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseRuler(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void initRuler() {
        setBackgroundColor(mBackgoundColor);
        mBoarderLineWidth = DisplayUtils.dip2px(mContext, DEFAULT_BORDER_LINE_WIDTH);
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBoarderLineWidth);

        mScaleLineWidth = DisplayUtils.dip2px(mContext, DEFAULT_SCALE_WIDTH);
        mScaleLineHeight = DisplayUtils.dip2px(mContext, DEFAULT_SCALE_LINE_HEIGHT);
        mScaleBoldLineHeight = DisplayUtils.dip2px(mContext, DEFAULT_SCALE_BOLD_LINE_HEIGHT);
        mScalePaint = new Paint();
        mScalePaint.setStyle(Paint.Style.FILL);
        mScalePaint.setAntiAlias(true);
        mScalePaint.setStrokeCap(Paint.Cap.ROUND);
        mScalePaint.setTextAlign(Paint.Align.CENTER);
        mScalePaint.setTextSize(mScaleSize);

        mCurrentLineWidth = DisplayUtils.dip2px(mContext, DEFAULT_CURRENT_LINE_WIDTH);
        mCurrentLineHeight = DisplayUtils.dip2px(mContext, DEFAULT_CURRENT_LINE_EHIGHT);
        mCurrentPaint = new Paint();
        mCurrentPaint.setAntiAlias(true);
        mCurrentPaint.setStyle(Paint.Style.FILL);
        mCurrentPaint.setStrokeWidth(mCurrentLineWidth);
        mCurrentPaint.setColor(mCurrentColor);

        mMinEdge = new EdgeEffect(mContext);
        mMaxEdge = new EdgeEffect(mContext);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            mMinEdge.setColor(mEdgeColor);
            mMinEdge.setColor(mEdgeColor);
        }
        mEdgeWidth = DisplayUtils.dip2px(mContext, DEFAULT_EDGE_WIDTH);

        mCurrentNumber = DEFAULT_CURRENT_NUMBER;
        mScaleRatio = DEFAULT_SCALE_RATIO;
        mScaleStepNumber = (float) mScaleRatio / mScaleSpace;

        mGestureDetector = new GestureDetector(mContext, new DefaultGestureDector());
        mScroller = new OverScroller(mContext);
        minFingDistance = ViewConfiguration.get(mContext).getScaledTouchSlop();
        minFingVelocity = ViewConfiguration.get(mContext).getScaledMinimumFlingVelocity();
    }

    private int measureDimension(int measureSpec, int defaultSize) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (MeasureSpec.EXACTLY == mode) {
            result = size;
        } else {
            result = Math.min(size, defaultSize);
        }

        return result;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureDimension(widthMeasureSpec, defaultWidth),
                measureDimension(heightMeasureSpec, defaultHeight));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mLeft = getPaddingLeft();
        mTop = getPaddingTop();
        mRight = mWidth - getPaddingRight();
        mBottom = mHeight - getPaddingBottom();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBorder(canvas);
        drawScale(canvas);
        drawCurrentLine(canvas);
        if (mEnableEdge) {
            drawEdge(canvas);
        }
    }

    @Override
    public void drawEdge(Canvas canvas) {
        if (mEnableEdge) {
            if (!mMinEdge.isFinished()) {
                int count = canvas.save();
                canvas.rotate(270);
                canvas.translate(-mHeight, minPostion + mWidth * 0.5f);
                if (mMinEdge.draw(canvas)) {
                    postInvalidateOnAnimation();
                }
                canvas.restoreToCount(count);
                Log.d(TAG, "drawEdge: min edge minPos -> " + minPostion);
            } else {
                Log.d(TAG, "drawEdge: finish");
                mMinEdge.finish();
            }
            if (!mMaxEdge.isFinished()) {
                int count = canvas.save();
                canvas.rotate(90);
                canvas.translate(0, -maxPosition - mWidth * 0.5f);
                if (mMaxEdge.draw(canvas)) {
                    postInvalidateOnAnimation();
                }
                canvas.restoreToCount(count);
                Log.d(TAG, "drawEdge: max edge");
            } else {
                mMaxEdge.finish();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_UP:
                releaseEdgeEffect();
                break;
            default:
                break;

        }
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollNumber();
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            float formatNumber = Math.round(mCurrentNumber * 10)/10.0f;
            if(!mScroller.computeScrollOffset() &&
                    Math.abs(Math.round(mCurrentNumber * 100)/100.0f - formatNumber) > 1e-4) {
                scrollToNearest();
            }
            invalidate();
        }
    }

    public class DefaultGestureDector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "onDown: ");
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            startScroll(distanceX, 0);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            int deltaX = (int)(e1.getX() - e2.getX());
            int deltaY = (int)(e1.getY() - e2.getY());
            if (Math.abs(deltaX) > minFingDistance && Math.abs(velocityX) > minFingVelocity ||
                    (Math.abs(deltaY) > minFingDistance && Math.abs(velocityY) > minFingVelocity)) {
                startFling(velocityX, velocityY);
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

    }

    @Override
    public void startMinEdge(int position) {
        if (mEnableEdge) {
            Log.d(TAG, "startMinEdge: ");
            if (!mScroller.isFinished()) {
                Log.d(TAG, "startMinEdge: onAbsorb");
                mMinEdge.onAbsorb((int) mScroller.getCurrVelocity());
                mScroller.abortAnimation();
            } else {
                Log.d(TAG, "startMinEdge: onPull position -> " + position + " minPostion -> " + minPostion);
                mMinEdge.onPull((minPostion - position) / (mEdgeWidth));
                mMinEdge.setSize(mEdgeWidth, mHeight);
            }
            postInvalidateOnAnimation();
        }
    }

    @Override
    public void startMaxEdge(int position) {
        if (mEnableEdge) {
            Log.d(TAG, "startMaxEdge: ");
            if (!mScroller.isFinished()) {
                Log.d(TAG, "startMaxEdge: onAbsorb");
                mMaxEdge.onAbsorb((int) mScroller.getCurrVelocity());
                mScroller.abortAnimation();
            } else {
                Log.d(TAG, "startMaxEdge: onPull");
                Log.d(TAG, "startMinEdge: onPull position -> " + position + " maxPostion -> " + maxPosition);
                mMaxEdge.onPull((position - maxPosition) / (mEdgeWidth));
                mMaxEdge.setSize(mEdgeWidth, mHeight);
            }
            postInvalidateOnAnimation();
        }
    }

    private void releaseEdgeEffect() {
        if (mEnableEdge) {
            Log.d(TAG, "releaseEdgeEffect: ");
            mMinEdge.onRelease();
            mMaxEdge.onRelease();
        }
    }

    public void setScaleRatio(int ratio) {
        this.mScaleRatio = ratio;
        invalidate();
    }

    public void setCurrentNumber(float number) {
        this.mCurrentNumber = number;
        invalidate();
    }

    public void setOnCurrentChangeListener(OnCurrentChangeListener listener) {
        this.mListener = listener;
    }

    public interface OnCurrentChangeListener {
        void onChange(float current);
    }

}
