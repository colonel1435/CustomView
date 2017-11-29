package com.zero.customview.view.ruler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.github.hellocharts.animation.ChartAnimationListener;
import com.squareup.leakcanary.internal.DisplayLeakActivity;
import com.zero.customview.R;
import com.zero.customview.utils.DisplayUtils;
import com.zero.customview.view.bottombar.MessageTab;

/**
 * Description
 * Author : Mr.wuming
 * Email  : fusu1435@163.com
 * Date   : 2017/11/28 0028 16:17
 */

public class RulerView extends View {
    private static final String TAG = "RulerView@wumin";
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 640;
    private static final float DEFAULT_SCALE_SIZE = 16;
    private static final float DEFAULT_SCALE_WIDTH = 4;
    private static final float DEFAULT_BORDER_LINE_WIDTH = 2;
    private static final float DEFAULT_CURRENT_SCALE_WIDTH = 6;
    private static final float DEFAULT_PADDING = 2;
    private static final float DEFAULT_CURRENT_LINE_EHIGHT = 16;
    private static final float DEFAULT_SCALE_BOLD_LINE_HEIGHT = 12;
    private static final float DEFAULT_SCALE_LINE_HEIGHT = 6;
    private static final int DEFAULT_SCALE_LINE_MAX = 30;
    private static final int DEFAULT_SCALE_LINE_INT = 10;
    private static final int DEFAULT_SCALE_RATIO = 1;
    private static final int DEFAULT_CURRENT_NUMBER = 0;
    private static final int DEFAULT_SCALE_NUMBER_PADDING = 8;
    private static final float DEFAULT_FING_DISTANCE = 5;
    private static final float DEFAULT_FING_VELOCITY = 5;
    private Context mContext;
    private boolean enableTopBorder;
    private boolean enableBottomBorder;
    private int mBackgoundColor;
    private Paint mBorderPaint;
    private Paint mScalePaint;
    private Paint mCurrentPaint;
    private int mBorderColor;
    private int mScaleColor;
    private int mScaleNumberColor;
    private int mCurrentColor;
    private float mCurrentLineHeight;
    private float mBoarderLineWidth;
    private float mScaleLineHeight;
    private float mScaleLineWidth;
    private float mScaleBoldLineHeight;
    private float mScaleSize;
    private float mScaleNumberPadding;
    private float mCurrentSize;
    private boolean isBoundary;
    private int mStartNumber;
    private int mEndNumber;
    private float mCurrentNumber;
    private int mScaleRatio;
    private float minFingDistance;
    private float minFingVelocity;
    private int mFingStart;

    private int mWidth;
    private int mHeight;
    private float mCenterX;
    private float mLeft;
    private float mTop;
    private float mRight;
    private float mBottom;
    private float mScaleStepDist;
    private float mScaleStepNumber;

    private GestureDetector mGestureDetector;
    private Scroller mScroller;

    private OnCurrentChangeListener mListener;
    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RulerView);
        enableTopBorder = ta.getBoolean(R.styleable.RulerView_ruler_enable_top_border, true);
        enableBottomBorder = ta.getBoolean(R.styleable.RulerView_ruler_enable_bottom_border, true);
        mBackgoundColor = ta.getColor(R.styleable.RulerView_ruler_background, Color.WHITE);
        isBoundary = ta.getBoolean(R.styleable.RulerView_ruler_is_boundary, false);
        mBorderColor = ta.getColor(R.styleable.RulerView_ruler_border_line_color, Color.GRAY);
        mScaleColor = ta.getColor(R.styleable.RulerView_ruler_scale_line_color, Color.GRAY);
        mScaleNumberColor = ta.getColor(R.styleable.RulerView_ruler_scale_number_color, Color.BLACK);
        mCurrentColor = ta.getColor(R.styleable.RulerView_ruler_current_line_color, Color.RED);
        mScaleSize = ta.getDimension(R.styleable.RulerView_ruler_scale_number_size,
                DisplayUtils.sp2px(mContext, DEFAULT_SCALE_SIZE));
        mScaleNumberPadding = ta.getDimension(R.styleable.RulerView_ruler_scale_number_padding,
                DisplayUtils.dip2px(mContext, DEFAULT_SCALE_NUMBER_PADDING));
        ta.recycle();
        initView();
    }

    private void initView() {
        setBackgroundColor(mBackgoundColor);
        mBoarderLineWidth = DEFAULT_BORDER_LINE_WIDTH;
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBoarderLineWidth);

        mScaleLineWidth = DEFAULT_SCALE_WIDTH;
        mScaleLineHeight = DEFAULT_SCALE_LINE_HEIGHT;
        mScaleBoldLineHeight = DEFAULT_SCALE_BOLD_LINE_HEIGHT;
        mScalePaint = new Paint();
        mScalePaint.setStyle(Paint.Style.FILL);
        mScalePaint.setAntiAlias(true);
        mScalePaint.setColor(mScaleColor);
        mScalePaint.setStrokeCap(Paint.Cap.ROUND);
        mScalePaint.setTextAlign(Paint.Align.CENTER);
        mScalePaint.setTextSize(mScaleSize);

        mCurrentLineHeight = DEFAULT_CURRENT_LINE_EHIGHT;
        mCurrentPaint = new Paint();
        mCurrentPaint.setAntiAlias(true);
        mCurrentPaint.setStyle(Paint.Style.FILL);
        mCurrentPaint.setStrokeWidth(mScaleLineWidth);
        mCurrentPaint.setColor(mCurrentColor);
        mCurrentPaint.setStrokeCap(Paint.Cap.ROUND);

        mCurrentNumber = DEFAULT_CURRENT_NUMBER;
        mScaleRatio = DEFAULT_SCALE_RATIO;
        mScaleStepNumber = (float) mScaleRatio / DEFAULT_SCALE_LINE_INT;

        mGestureDetector = new GestureDetector(mContext, new DefaultGestureDector());
        mScroller = new Scroller(mContext, new AccelerateDecelerateInterpolator());
        minFingDistance = DEFAULT_FING_DISTANCE;
        minFingVelocity = DEFAULT_FING_VELOCITY;
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
        mCenterX = w * 0.5f;
        mLeft = getPaddingLeft();
        mTop = getPaddingTop() + DEFAULT_PADDING;
        mRight = mWidth - getPaddingRight();
        mBottom = mHeight - getPaddingBottom() - DEFAULT_PADDING;
        mScaleStepDist = (mCenterX - mLeft) / (DEFAULT_SCALE_LINE_MAX * 0.5f);
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
            float step = -(currX - mFingStart) / mScaleStepDist;
            Log.d(TAG, "computeScroll: start -> " + mFingStart + " curr -> " + currX + " step -> "  +step);
            mFingStart = currX;
            mCurrentNumber += step * mScaleStepNumber;
            if (mListener != null) {
                mListener.onChange(Math.round(mCurrentNumber * 10)/10.0f);
            }
            invalidate();
        }
    }

    private void drawBorder(Canvas canvas) {
        /***    Draw top border   ***/
        if (enableTopBorder) {
            canvas.drawLine(mLeft, mTop, mRight, mTop, mBorderPaint);
        }
        if (enableBottomBorder) {
            canvas.drawLine(mLeft, mBottom, mRight, mBottom, mBorderPaint);
        }

    }

    private void drawScale(Canvas canvas) {
        /***    Draw scale  line    ***/
        float stepPos;
        Paint.FontMetrics fontMetrics = mScalePaint.getFontMetrics();
        /***    Init left scale number  ***/
        float scaleNumber = mCurrentNumber - DEFAULT_SCALE_LINE_MAX * 0.5f * mScaleStepNumber;
        for (int i = 0; i < DEFAULT_SCALE_LINE_MAX; i++) {
            stepPos = mLeft + i * mScaleStepDist;
            /***    Keep a decimal place    ***/
            if (0 != i) {
                scaleNumber = (float) (Math.round((scaleNumber + mScaleStepNumber) * 10)) / 10;
            }
            if (0 == (scaleNumber % DEFAULT_SCALE_RATIO)) {
                mScalePaint.setStrokeWidth(mScaleLineWidth);
                canvas.drawLine(stepPos, mTop, stepPos, mTop + mScaleBoldLineHeight, mScalePaint);
                canvas.drawText(String.valueOf(Math.round(scaleNumber)),
                        stepPos,
                        mTop + mScaleBoldLineHeight + (-fontMetrics.ascent) + mScaleNumberPadding,
                        mScalePaint);
            } else {
                mScalePaint.setStrokeWidth(mBoarderLineWidth);
                canvas.drawLine(stepPos, mTop, stepPos, mTop + mScaleLineHeight, mScalePaint);
            }
        }
    }

    private void drawCurrentLine(Canvas canvas) {
        /***    Draw current line   ***/
        canvas.drawLine(mCenterX, mTop, mCenterX, mTop + mCurrentLineHeight, mCurrentPaint);
    }


    public void setCurrentNumber(float number) {
        this.mCurrentNumber = number;
        invalidate();
    }

    public void setScaleRatio(int ratio) {
        this.mScaleRatio = ratio;
        invalidate();
    }

    public void setOnCurrentChangeListener(OnCurrentChangeListener listener) {
        this.mListener = listener;
    }

    public class DefaultGestureDector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "onDown: ");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float step = distanceX / mScaleStepDist;
            mCurrentNumber = mCurrentNumber + step * mScaleStepNumber;
            if (mListener != null) {
                mListener.onChange(Math.round(mCurrentNumber * 10)/10.0f);
            }
            invalidate();
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            int deltaX = (int)(e1.getX() - e2.getX());
            if (Math.abs(deltaX) > minFingDistance && Math.abs(velocityX) > minFingVelocity) {
                Log.d(TAG, "onFling: success!");
                mFingStart = (int)e2.getX();
                mScroller.fling((int)e1.getX(), (int)e2.getX(), (int)velocityX, 0,
                        (int)e2.getX() - mWidth, (int)e2.getX() + mWidth, 0, 0);
                invalidate();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }


    public interface OnCurrentChangeListener {
        void onChange(float current);
    }
}
