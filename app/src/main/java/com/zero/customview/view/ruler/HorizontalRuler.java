package com.zero.customview.view.ruler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import com.zero.customview.R;
import com.zero.customview.utils.DisplayUtils;

import java.math.BigDecimal;

/**
 * Description
 * @author : Mr.wuming
 * @email  : fusu1435@163.com
 * @date   : 2017/12/4 0004 13:41
 */

public class HorizontalRuler extends BaseRuler {
    protected static final int HORIZONTAL_DEFAULT_WIDTH = 640;
    protected static final int HORIZONTAL_DEFAULT_HEIGHT = 96;

    public HorizontalRuler(Context context) {
        this(context, null);
    }

    public HorizontalRuler(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalRuler(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RulerView);
        enableTopBorder = ta.getBoolean(R.styleable.RulerView_ruler_enable_top_border, true);
        enableBottomBorder = ta.getBoolean(R.styleable.RulerView_ruler_enable_bottom_border, true);
        mBackgoundColor = ta.getColor(R.styleable.RulerView_ruler_background, Color.WHITE);
        mEnableEdge = ta.getBoolean(R.styleable.RulerView_ruler_enable_edge, false);
        mBorderColor = ta.getColor(R.styleable.RulerView_ruler_border_line_color, Color.GRAY);
        mScaleLineColor = ta.getColor(R.styleable.RulerView_ruler_scale_line_color, Color.GRAY);
        mScaleNumberColor = ta.getColor(R.styleable.RulerView_ruler_scale_number_color, Color.BLACK);
        mEdgeColor = ta.getColor(R.styleable.RulerView_ruler_edge_color, Color.GRAY);
        mCurrentColor = ta.getColor(R.styleable.RulerView_ruler_current_line_color, Color.RED);
        mScaleSize = ta.getDimension(R.styleable.RulerView_ruler_scale_number_size,
                DisplayUtils.sp2px(mContext, DEFAULT_SCALE_SIZE));
        mScaleNumberPadding = ta.getDimension(R.styleable.RulerView_ruler_scale_number_padding,
                DisplayUtils.dip2px(mContext, DEFAULT_SCALE_NUMBER_PADDING));
        mNumberMin = ta.getInt(R.styleable.RulerView_ruler_number_min, DEFAULT_NUMBER_MIN);
        mNumberMax = ta.getInt(R.styleable.RulerView_ruler_number_max, DEFAULT_NUMBER_MAX);
        mScaleSpace = ta.getInt(R.styleable.RulerView_ruler_scale_space, DEFAULT_SCALE_LINE_INT);
        mScaleRatio = ta.getFloat(R.styleable.RulerView_ruler_scale_ratio, DEFAULT_SCALE_RATIO);
        enableNearest = ta.getBoolean(R.styleable.RulerView_ruler_enable_nearest, true);
        ta.recycle();
        defaultWidth = DisplayUtils.dip2px(mContext, HORIZONTAL_DEFAULT_WIDTH);
        defaultHeight = DisplayUtils.dip2px(mContext, HORIZONTAL_DEFAULT_HEIGHT);
        initRuler();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w * 0.5f;
        mCenterY = h * 0.5f;
        mScaleStepDist = (mWidth) / (DEFAULT_SCALE_LINE_MAX);
        mRulerLength = (mNumberMax - mNumberMin) * mScaleStepDist;
        minPostion = ((mNumberMin - mCurrentNumber) / mScaleStepNumber) * mScaleStepDist;
        maxPosition = ((mNumberMax - mCurrentNumber) / mScaleStepNumber) * mScaleStepDist;
    }

    @Override
    public void drawBorder(Canvas canvas) {
        /***    Draw top border   ***/
        if (enableTopBorder) {
            canvas.drawLine(mLeft + getScrollX(), mTop, mRight + getScrollX(), mTop, mBorderPaint);
        }
        if (enableBottomBorder) {
            canvas.drawLine(mLeft + getScrollX(), mBottom, mRight + getScrollX(), mBottom, mBorderPaint);
        }
    }

    @Override
    public void drawScale(Canvas canvas) {
        canvas.save();
        canvas.translate(mCenterX, 0);
        Paint.FontMetrics fontMetrics = mScalePaint.getFontMetrics();
        /***    Init left scale number  ***/
        float scrollX = getScrollX();
        Log.d(TAG, "drawScale: getScroll -> " + getScrollX());
        float scale = (float) Math.pow(10, mScaleDecimalPlace);
        float scaleNumber;
        float coordX;
        float leftHalf = mNumberMin - mCurrentNumber;
        float rightHalf = mNumberMax - mCurrentNumber;
        leftToRight = Math.abs(leftHalf) < Math.abs(rightHalf);
        if (leftToRight) {
            stepMin = (leftHalf / mScaleStepNumber) * mScaleStepDist + scrollX;
            if (stepMin < minPostion) {
                minPostion = stepMin;
            }
            stepMax = mWidth*0.5f + scrollX;
            scaleNumber = mNumberMin;
            coordX = stepMin;
        } else {
            stepMin = getLeft() + scrollX - mWidth*0.5f;
            stepMax = (rightHalf / mScaleStepNumber) * mScaleStepDist + scrollX;
            if (stepMax > maxPosition) {
                maxPosition = stepMax;
            }
            scaleNumber = mNumberMax;
            coordX = stepMax;
        }
        Log.d(TAG, "drawScale: stepmin -> " + stepMin + " stepMax -> " + stepMax +
                " scaleNumber -> " + scaleNumber + (leftToRight?"Left -> Right":"RIght -> Left"));
        while (stepMin < stepMax) {
            if (0 == (scaleNumber % mScaleRatio)) {
                mScalePaint.setStrokeWidth(mScaleLineWidth);
                mScalePaint.setColor(mScaleLineColor);
                canvas.drawLine(coordX, mTop + mBoarderLineWidth, coordX,
                        mTop + mScaleBoldLineHeight + mBoarderLineWidth, mScalePaint);
                mScalePaint.setColor(mScaleNumberColor);
                canvas.drawText(String.valueOf(Math.round(scaleNumber)),
                        coordX,
                        mTop + mScaleBoldLineHeight + (-fontMetrics.ascent) + mScaleNumberPadding,
                        mScalePaint);
            } else {
                mScalePaint.setStrokeWidth(mBoarderLineWidth);
                mScalePaint.setColor(mScaleLineColor);
                canvas.drawLine(coordX, mTop, coordX, mTop + mScaleLineHeight, mScalePaint);
            }

            if (leftToRight) {
                stepMin += mScaleStepDist;
                /***    Keep a decimal place    ***/
                scaleNumber = (float) (Math.round((scaleNumber + mScaleStepNumber) * scale)) / scale;
                coordX = stepMin;
            } else {
                stepMax -= mScaleStepDist;
                /***    Keep a decimal place    ***/
                scaleNumber = (float) (Math.round((scaleNumber - mScaleStepNumber) * scale)) / scale;
                coordX = stepMax;
            }
        }
        canvas.restore();
    }

    @Override
    public void drawCurrentLine(Canvas canvas) {
        float center = (mLeft + getScrollX() + mRight + getScrollX()) / 2;
        float bridge = mTop + mCurrentLineHeight * 0.5f;
        mCurrentPaint.setStrokeCap(Paint.Cap.BUTT);
        canvas.drawLine(center, mTop + mBoarderLineWidth*0.5f,
                center, bridge, mCurrentPaint);
        mCurrentPaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(center, bridge,
                center, mTop + mCurrentLineHeight, mCurrentPaint);
    }

    @Override
    public void scrollTo(int x, int y) {
        if (x < Math.round(minPostion)) {
            startMinEdge(x);
            x = Math.round(minPostion);
        }

        if ( x > Math.round(maxPosition)) {
            startMaxEdge(x);
            x = Math.round(maxPosition);
        }

        if (x != getScrollX()) {
            super.scrollTo(x, y);
        }
    }

    @Override
    public void startScroll(float distX, float distY) {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), (int)distX, 0,
                SCROLL_ANIMATION_DURATION);
        invalidate();
    }

    @Override
    public void startFling(float velocityX, float velocityY) {
        mScroller.fling(getScrollX(), 0, (int)(-0.5f * velocityX), 0,
                (int)minPostion, (int)maxPosition, 0, 0);
        invalidate();
    }

    @Override
    public void scrollToNearest() {
        Log.d(TAG, "scrollToNearest: currentNUmber -> " + mCurrentNumber
                + " offset ->" + scaleToPosition(Math.round(mCurrentNumber * 10)/10.0f) + " - " + mScroller.getCurrX());
        float target;
        if (mCurrentNumber - mScaleStepNumber * 0.5f < mCurrentNumber) {
            target = (float) Math.floor(mCurrentNumber / mScaleStepNumber) * mScaleStepNumber ;
        } else {
            target = (float) Math.floor(mCurrentNumber / mScaleStepNumber + 1) * mScaleStepNumber;
        }
        float scale = BigDecimal.valueOf(target)
                .setScale(mScaleDecimalPlace, BigDecimal.ROUND_HALF_UP)
                .floatValue();

        Log.d(TAG, "scrollToNearest: current -> " + mCurrentNumber + " target -> "  + target + " scale -> " + scale);
        mScroller.startScroll(mScroller.getCurrX(), 0,
                scaleToPosition(scale) - mScroller.getCurrX(), 0,
                SCROLL_ANIMATION_DURATION);
        invalidate();
    }

    @Override
    public int scaleToPosition(float scale) {
        if (leftToRight) {
            return (int) (minPostion + (scale - mNumberMin) / mScaleStepNumber * mScaleStepDist);
        } else {
            return (int) (maxPosition - (mNumberMax - scale) / mScaleStepNumber * mScaleStepDist);
        }
    }

    @Override
    public void scrollNumber() {
        int lastX = getScrollX();
        int currX = mScroller.getCurrX();
        boolean lower = currX < minPostion && leftToRight;
        boolean upper = currX > maxPosition && !leftToRight;
        if (lower || upper) {
            Log.d(TAG, "computeScroll: currx -> " + currX + "position -> "
                    + (leftToRight? minPostion + "left2right":maxPosition + "right2left"));
            return;
        }
        float deltaX = currX - lastX;
        float step = deltaX / mScaleStepDist;
        mCurrentNumber += step * mScaleStepNumber;
        float formatNumber = BigDecimal.valueOf(mCurrentNumber)
                .setScale(mScaleDecimalPlace, BigDecimal.ROUND_HALF_UP)
                .floatValue();
        if (mListener != null) {
            mListener.onChange(formatNumber);
        }
    }
}
