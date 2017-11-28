package com.zero.customview.view.ruler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.zero.customview.R;
import com.zero.customview.view.bottombar.MessageTab;

/**
 * Description
 * Author : Mr.wuming
 * Email  : fusu1435@163.com
 * Date   : 2017/11/28 0028 16:17
 */

public class RulerView extends View {
    private static final String TAG = "RulerView@wuming";
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 640;
    private static final float DEFAULT_SCALE_SIZE = 24;
    private static final float DEFAULT_SCALE_WIDTH = 4;
    private static final float DEFAULT_CURRENT_SCALE_WIDTH = 6;
    private static final float DEFAULT_PADDING = 2;
    private static final float DEFAULT_CURRENT_LINE_EHIGHT = 16;
    private static final float DEFAULT_SCALE_BOLD_LINE_HEIGHT = 12;
    private static final float DEFAULT_SCALE_LINE_HEIGHT = 6;
    private static final int DEFAULT_SCALE_LINE_MAX = 30;
    private static final int DEFAULT_SCALE_LINE_INT = 10;
    private static final int DEFAULT_SCALE_RATIO = 1;
    private static final int DEFAULT_CURRENT_NUMBER = 0;
    private static final int DEFAULT_SCALE_NUMBER_PADDING = 20;
    private Paint mBorderPaint;
    private Paint mScalePaint;
    private Paint mCurrentPaint;
    private int mBorderColor;
    private int mScaleColor;
    private int mScaleNumberColor;
    private int mCurrentColor;
    private float mCurrentLineHeight;
    private float mBoarderSize;
    private float mScaleLineHeight;
    private float mScaleLineWidth;
    private float mScaleBoldLineHeight;
    private float mScaleSize;
    private float mCurrentSize;
    private boolean isBoundary;
    private int mStartNumber;
    private int mEndNumber;
    private int mCurrentNumber;
    private int mScaleRatio;

    private int mWidth;
    private int mHeight;
    private float mCenterX;
    private float mLeft;
    private float mTop;
    private float mRight;
    private float mBottom;

    private OnCurrentChangeListener mListener;
    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RulerView);
        isBoundary = ta.getBoolean(R.styleable.RulerView_ruler_is_boundary, false);
        mBorderColor = ta.getColor(R.styleable.RulerView_ruler_border_line_color, Color.GRAY);
        mScaleColor = ta.getColor(R.styleable.RulerView_ruler_scale_line_color, Color.GRAY);
        mScaleNumberColor = ta.getColor(R.styleable.RulerView_ruler_scale_number_color, Color.BLACK);
        mCurrentColor = ta.getColor(R.styleable.RulerView_ruler_current_line_color, Color.RED);
        ta.recycle();
        initView();
    }

    private void initView() {
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);

        mScaleLineWidth = DEFAULT_SCALE_WIDTH;
        mScaleLineHeight = DEFAULT_SCALE_LINE_HEIGHT;
        mScaleBoldLineHeight = DEFAULT_SCALE_BOLD_LINE_HEIGHT;
        mScalePaint = new Paint();
        mScalePaint.setStyle(Paint.Style.FILL);
        mScalePaint.setAntiAlias(true);
        mScalePaint.setStrokeWidth(mScaleLineWidth);
        mScalePaint.setColor(mScaleColor);
        mScalePaint.setStrokeCap(Paint.Cap.ROUND);
        mScalePaint.setTextAlign(Paint.Align.CENTER);

        mCurrentLineHeight = DEFAULT_CURRENT_LINE_EHIGHT;
        mCurrentPaint = new Paint();
        mCurrentPaint.setAntiAlias(true);
        mCurrentPaint.setStyle(Paint.Style.FILL);
        mCurrentPaint.setStrokeWidth(mScaleLineWidth);
        mCurrentPaint.setColor(mCurrentColor);
        mCurrentPaint.setStrokeCap(Paint.Cap.ROUND);

        mCurrentNumber = DEFAULT_CURRENT_NUMBER;
        mScaleRatio = DEFAULT_SCALE_RATIO;
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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScale(canvas);
        drawCurrentLine(canvas);
    }

    private void drawScale(Canvas canvas) {
        /***    Draw baseline   ***/
        canvas.drawLine(mLeft, mTop, mRight, mTop, mBorderPaint);
        /***    Draw scale  line    ***/
        float step = (mCenterX - mLeft) / (DEFAULT_SCALE_LINE_MAX * 0.5f);
        float stepNumber = mScaleRatio / DEFAULT_SCALE_LINE_INT;
        float stepPos;
        float scaleNumber = mCurrentNumber - (int)(DEFAULT_SCALE_LINE_MAX * 0.5f * stepNumber);
        for (int i = 0; i < DEFAULT_SCALE_LINE_MAX; i++) {
            stepPos = mLeft + i * step;
            scaleNumber += stepNumber * step;
            if (0 == i % DEFAULT_SCALE_LINE_INT) {
                canvas.drawLine(stepPos, mTop, stepPos, mTop + mScaleBoldLineHeight, mScalePaint);
                canvas.drawText(String.valueOf(scaleNumber), stepPos,
                        mTop + mScaleBoldLineHeight + DEFAULT_SCALE_NUMBER_PADDING, mScalePaint);
            } else {
                canvas.drawLine(stepPos, mTop, stepPos, mTop + mScaleLineHeight, mScalePaint);
            }
        }
    }

    private void drawCurrentLine(Canvas canvas) {
        /***    Draw current line   ***/
        canvas.drawLine(mCenterX, mTop, mCenterX, mTop + mCurrentLineHeight, mCurrentPaint);
    }


    public void setCurrentNumber(int number) {
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

    public interface OnCurrentChangeListener {
        void onChange(float current);
    }
}
