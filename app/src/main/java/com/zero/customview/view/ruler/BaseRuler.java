package com.zero.customview.view.ruler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
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
    protected static final int DEFAULT_WIDTH = 800;
    protected static final int DEFAULT_HEIGHT = 640;
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
    protected Context mContext;
    protected boolean enableTopBorder;
    protected boolean enableBottomBorder;
    protected int mBackgoundColor;
    protected Paint mBorderPaint;
    protected Paint mScalePaint;
    protected Paint mCurrentPaint;
    protected int mBorderColor;
    protected int mScaleLineColor;
    protected int mScaleNumberColor;
    protected int mCurrentColor;
    protected float mCurrentLineHeight;
    protected float mCurrentLineWidth;
    protected float mBoarderLineWidth;
    protected float mScaleLineHeight;
    protected float mScaleLineWidth;
    protected float mScaleBoldLineHeight;
    protected float mScaleSize;
    protected float mScaleNumberPadding;
    protected boolean isBoundary;
    protected int mNumberMin;
    protected int mNumberMax;
    protected float mRulerLength;
    protected float mCurrentNumber;
    protected int mScaleRatio;
    protected float minFingDistance;
    protected float minFingVelocity;
    protected float minPostion;
    protected float maxPosition;
    protected float stepMin;
    protected float stepMax;
    protected boolean leftToRight;

    protected int mWidth;
    protected int mHeight;
    protected float mCenterX;
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

        mCurrentNumber = DEFAULT_CURRENT_NUMBER;
        mScaleRatio = DEFAULT_SCALE_RATIO;
        mScaleStepNumber = (float) mScaleRatio / DEFAULT_SCALE_LINE_INT;

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
        setMeasuredDimension(measureDimension(widthMeasureSpec, DEFAULT_WIDTH),
                measureDimension(heightMeasureSpec, DEFAULT_HEIGHT));
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
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int currX = mScroller.getCurrX();
            boolean lower = currX < minPostion && leftToRight;
            boolean upper = currX > maxPosition && !leftToRight;
            if (lower || upper) {
                return;
            }

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
            if (Math.abs(deltaX) > minFingDistance && Math.abs(velocityX) > minFingVelocity) {
                startFling(velocityX, velocityY);
            }
            return super.onFling(e1, e2, velocityX, velocityY);
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
