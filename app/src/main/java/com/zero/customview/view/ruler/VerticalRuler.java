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
 *
 * @author : Mr.wuming
 * @email : fusu1435@163.com
 * @date : 2017/12/6 0006 14:28
 */

public class VerticalRuler extends BaseRuler {
    protected static final int VERTICAL_DEFAULT_WIDTH = 96;
    protected static final int VERTICAL_DEFAULT_HEIGHT = 640;
    public VerticalRuler(Context context) {
        this(context, null);
    }

    public VerticalRuler(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalRuler(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        defaultWidth = DisplayUtils.dip2px(mContext, VERTICAL_DEFAULT_WIDTH);
        defaultHeight = DisplayUtils.dip2px(mContext, VERTICAL_DEFAULT_HEIGHT);
        initRuler();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w * 0.5f;
        mCenterY = h * 0.5f;
        mScaleStepDist = (mHeight) / (DEFAULT_SCALE_LINE_MAX);
        mRulerLength = (mNumberMax - mNumberMin) * mScaleStepDist;
        minPostion = ((mNumberMin - mCurrentNumber) / mScaleStepNumber) * mScaleStepDist;
        maxPosition = ((mNumberMax - mCurrentNumber) / mScaleStepNumber) * mScaleStepDist;
    }

    @Override
    public void drawBorder(Canvas canvas) {
        /***    Draw top border   ***/
        if (enableTopBorder) {
            canvas.drawLine(mLeft, mTop + getScrollY(), mLeft, mBottom + getScrollY(), mBorderPaint);
        }
        if (enableBottomBorder) {
            canvas.drawLine(mRight, mTop + getScrollY(), mRight, mBottom + getScrollY(), mBorderPaint);
        }
    }

    @Override
    public void drawScale(Canvas canvas) {
        canvas.save();
        canvas.translate(0, mCenterY);
        Paint.FontMetrics fontMetrics = mScalePaint.getFontMetrics();
        /***    Init left scale number  ***/
        float scrollY = getScrollY();
        Log.d(TAG, "drawScale: getScroll -> " + getScrollY());
        float scale = (float) Math.pow(10, mScaleDecimalPlace);
        float scaleNumber;
        float coordY;
        float leftHalf = mNumberMin - mCurrentNumber;
        float rightHalf = mNumberMax - mCurrentNumber;
        leftToRight = Math.abs(leftHalf) < Math.abs(rightHalf);
        if (leftToRight) {
            stepMin = (leftHalf / mScaleStepNumber) * mScaleStepDist + scrollY;
            if (stepMin < minPostion) {
                minPostion = stepMin;
            }
            stepMax = mHeight*0.5f + scrollY;
            scaleNumber = mNumberMin;
            coordY = stepMin;
        } else {
            stepMin = getLeft() + scrollY - mHeight*0.5f;
            stepMax = (rightHalf / mScaleStepNumber) * mScaleStepDist + scrollY;
            if (stepMax > maxPosition) {
                maxPosition = stepMax;
            }
            scaleNumber = mNumberMax;
            coordY = stepMax;
        }
        Log.d(TAG, "drawScale: stepmin -> " + stepMin + " stepMax -> " + stepMax +
                " scaleNumber -> " + scaleNumber + (leftToRight?"Left -> Right":"RIght -> Left"));
        while (stepMin < stepMax) {
            if (0 == (scaleNumber % mScaleRatio)) {
                mScalePaint.setStrokeWidth(mScaleLineWidth);
                mScalePaint.setColor(mScaleLineColor);
                canvas.drawLine(mLeft + mBoarderLineWidth, coordY,
                        mLeft + mBoarderLineWidth + mScaleBoldLineHeight, coordY, mScalePaint);
                mScalePaint.setColor(mScaleNumberColor);
                canvas.drawText(String.valueOf(Math.round(scaleNumber)),
                        mLeft + mScaleBoldLineHeight + mScaleNumberPadding,
                        coordY - (fontMetrics.descent + fontMetrics.ascent)/2,
                        mScalePaint);
            } else {
                mScalePaint.setStrokeWidth(mBoarderLineWidth);
                mScalePaint.setColor(mScaleLineColor);
                canvas.drawLine(mLeft, coordY, mLeft + mScaleLineHeight, coordY, mScalePaint);
            }

            if (leftToRight) {
                stepMin += mScaleStepDist;
                /***    Keep a decimal place    ***/
                scaleNumber = (float) (Math.round((scaleNumber + mScaleStepNumber) * scale)) / scale;
                coordY = stepMin;
            } else {
                stepMax -= mScaleStepDist;
                /***    Keep a decimal place    ***/
                scaleNumber = (float) (Math.round((scaleNumber - mScaleStepNumber) * scale)) / scale;
                coordY = stepMax;
            }
        }
        canvas.restore();
    }

    @Override
    public void drawCurrentLine(Canvas canvas) {
        float center = (mTop + getScrollY() + mBottom + getScrollY()) / 2;
        float bridge = mLeft + mCurrentLineHeight * 0.5f;
        mCurrentPaint.setStrokeCap(Paint.Cap.BUTT);
        canvas.drawLine(mLeft + mBoarderLineWidth * 0.5f, center,
                bridge, center, mCurrentPaint);
        mCurrentPaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(bridge, center,
                mLeft + mCurrentLineHeight, center, mCurrentPaint);
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y < Math.round(minPostion)) {
            startMinEdge(y);
            y = Math.round(minPostion);
        }

        if ( y > Math.round(maxPosition)) {
            startMaxEdge(y);
            y = Math.round(maxPosition);
        }

        if (y != getScrollY()) {
            super.scrollTo(x, y);
        }
    }

    @Override
    public void startScroll(float distX, float distY) {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), 0, (int)distY,
                SCROLL_ANIMATION_DURATION);
        invalidate();
    }

    @Override
    public void startFling(float velocityX, float velocityY) {
        mScroller.fling(0, getScrollY(), 0, (int)(-0.5f * velocityY),
                0, 0, (int)minPostion, (int)maxPosition);
        invalidate();
    }

    @Override
    public void scrollToNearest() {
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
        mScroller.startScroll(0, mScroller.getCurrY(),
                0, scaleToPosition(scale) - mScroller.getCurrY(),
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
        int lastY = getScrollY();
        int currY = mScroller.getCurrY();
        boolean lower = currY < minPostion && leftToRight;
        boolean upper = currY > maxPosition && !leftToRight;
        if (lower || upper) {
            Log.d(TAG, "computeScroll: currx -> " + currY + "position -> "
                    + (leftToRight? minPostion + "left2right":maxPosition + "right2left"));
            return;
        }
        float deltaY = currY - lastY;
        float step = deltaY / mScaleStepDist;
        mCurrentNumber += step * mScaleStepNumber;
        float formatNumber = BigDecimal.valueOf(mCurrentNumber)
                .setScale(mScaleDecimalPlace, BigDecimal.ROUND_HALF_UP)
                .floatValue();
        if (mListener != null) {
            mListener.onChange(formatNumber);
        }
    }
}
