package com.zero.customview.view.pregressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.zero.customview.R;
import com.zero.customview.utils.DisplayUtils;

/**
 * Description
 *
 * @author : Mr.wuming
 * @email : fusu1435@163.com
 * @date : 2018/3/15 13:47
 */

public class LineProgressBar extends View{
    private static final String TAG = LineProgressBar.class.getSimpleName()+"@wumin";
    private static final int DEFAULT_MIN = 0;
    private static final int DEFAULT_MAX = 100;
    private static final int DEFAULT_SIZE = 12;
    private static final int DEFAULT_TAG_SIZE = 8;
    private static final float DEFAULT_BAR_HEIGHT = 8;
    private static final int DEFAULT_HEIGHT = 48;
    private static final int DEFAULT_WIDTH = 144;
    private static final int DEFAULT_PADDING = 8;
    private Context mContext;
    private int mReachedColor;
    private int mUnReachedColor;
    private int mTextColor;
    private int mProgressMin;
    private int mProgressMax;
    private int mProgress;
    private String mReachedTag;
    private String mUnReachedTag;
    private float mValueSize;
    private float mTagSize;
    private int mTagColor;
    private int mTagBackground;
    private int mTagPadding;
    private float mBarHeight;
    private float mBarWidth;

    private Paint mBarPaint;
    private Paint mProgressPaint;
    private Paint mTagPaint;
    private Paint.FontMetrics mProgressMetrics;
    private Paint.FontMetrics mTagMetrics;
    private float mCenterX;
    private float mCenterY;
    private float mSpace;
    private float mReachedPosition;
    private int mDefaultPadding;

    public LineProgressBar(Context context) {
        this(context, null);
    }

    public LineProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LineProgressBar);
        try {
            mReachedTag = ta.getString(R.styleable.LineProgressBar_line_progress_reached_tag);
            mReachedColor = ta.getColor(R.styleable.LineProgressBar_line_progress_reached_color,
                    Color.GREEN);
            mUnReachedTag = ta.getString(R.styleable.LineProgressBar_line_progress_unreached_tag);
            mUnReachedColor = ta.getColor(R.styleable.LineProgressBar_line_progress_unreached_color,
                    Color.RED);
            mTextColor = ta.getColor(R.styleable.LineProgressBar_line_progress_text_color,
                    Color.BLACK);
            mBarHeight = ta.getDimension(R.styleable.LineProgressBar_line_progress_height,
                    DisplayUtils.dip2px(context, DEFAULT_BAR_HEIGHT));
            mValueSize = ta.getDimension(R.styleable.LineProgressBar_line_progress_text_size,
                    DisplayUtils.sp2px(context, DEFAULT_SIZE));
            mTagSize = ta.getDimension(R.styleable.LineProgressBar_line_progress_tag_size,
                    DisplayUtils.sp2px(context, DEFAULT_TAG_SIZE));
            mTagColor = ta.getColor(R.styleable.LineProgressBar_line_progress_tag_color,
                    Color.BLACK);
            mTagBackground = ta.getColor(R.styleable.LineProgressBar_line_progress_tag_background,
                    Color.WHITE);
            mProgressMax = ta.getInt(R.styleable.LineProgressBar_line_progress_max, DEFAULT_MAX);
            mProgressMin = ta.getInt(R.styleable.LineProgressBar_line_progress_min, DEFAULT_MIN);
            initViews();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ta.recycle();
        }
    }

    private void initViews() {
        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint.setStyle(Paint.Style.STROKE);
        mBarPaint.setStrokeWidth(mBarHeight);
        mBarPaint.setStrokeCap(Paint.Cap.ROUND);
        mBarPaint.setDither(true);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setTextSize(mValueSize);
        mProgressPaint.setTextAlign(Paint.Align.CENTER);

        mTagPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTagPaint.setTextSize(mTagSize);
        mTagPaint.setTextAlign(Paint.Align.CENTER);

        mProgressMetrics = mProgressPaint.getFontMetrics();
        mTagMetrics = mTagPaint.getFontMetrics();

        mProgress = 0;
        mDefaultPadding = DisplayUtils.dip2px(mContext, DEFAULT_PADDING);
        mTagPadding = mDefaultPadding / 2;
        setPadding(mDefaultPadding*2, mDefaultPadding, mDefaultPadding*2, mDefaultPadding);
    }

    private int measureSize(int measureSpec, int defaultVal) {
        int defaultSize = defaultVal;
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2 + getPaddingLeft() - getPaddingRight();
        mCenterY = h / 2 + getPaddingTop() - getPaddingBottom();
        mSpace = (float) (w - getPaddingLeft() - getPaddingRight()) / (mProgressMax - mProgressMin);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int defaultWidth = DEFAULT_WIDTH;
        int defaultHeight = (int)(DEFAULT_HEIGHT
                + (mProgressMetrics.bottom - mProgressMetrics.top)
                + (mTagMetrics.bottom - mTagMetrics.top));
        setMeasuredDimension(measureSize(widthMeasureSpec, defaultWidth),
                            measureSize(heightMeasureSpec, defaultHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mReachedPosition = mSpace * mProgress;
        drawBar(canvas);
        drawValue(canvas);
        drawTag(canvas);
    }

    private void drawBar(Canvas canvas) {
        /**
         * Draw whole progress bar
         */
        mBarPaint.setColor(mUnReachedColor);
        canvas.drawLine(getPaddingLeft(), mCenterY,
                getPaddingLeft() + mSpace*(mProgressMax - mProgressMin), mCenterY, mBarPaint);
        /**
         * Draw reached progress bar
         */
        mBarPaint.setColor(mReachedColor);
        canvas.drawLine(getPaddingLeft(), mCenterY,
                getPaddingLeft() + mReachedPosition, mCenterY, mBarPaint);
    }

    private void drawValue(Canvas canvas) {
        float baseY = mCenterY + mBarHeight - (mProgressMetrics.descent + mProgressMetrics.ascent);
        mProgressPaint.setColor(mTextColor);
        /**
         * Draw min progress
         */
        canvas.drawText(String.valueOf(mProgressMin), getPaddingLeft(), baseY, mProgressPaint);
        /**
         * Draw max progress
         */
        canvas.drawText(String.valueOf(mProgressMax), getPaddingLeft()
                + mSpace*(mProgressMax - mProgressMin), baseY, mProgressPaint);
    }

    private void drawTag(Canvas canvas) {
        float baselineX = getPaddingLeft() + mReachedPosition;
        float baselineY = mCenterY - mBarHeight - mTagPadding
                - (mTagMetrics.descent + mTagMetrics.ascent)/2;
        float tagWidth = mTagPaint.measureText(String.valueOf(mProgress));
        /**
         * Draw tag background
         */
        RectF bgRectf = new RectF(baselineX - tagWidth/2 - mTagPadding,
                                    baselineY + mTagMetrics.top,
                                    baselineX + tagWidth/2 + mTagPadding,
                                    baselineY + mTagMetrics.bottom);
        mTagPaint.setColor(mTagBackground);
        canvas.drawRoundRect(bgRectf, tagWidth/2, tagWidth/2, mTagPaint);
        /**
         * Draw current progress
         */
        mTagPaint.setColor(mTagColor);
        canvas.drawText(String.valueOf(mProgress), baselineX,
                baselineY, mTagPaint);
    }

    public void setProgress(int progress) {
        if (progress > mProgressMin && progress <= mProgressMax) {
            this.mProgress = progress;
            invalidate();
        }
    }

    public void setProgressMax(int max) {
        this.mProgressMax = max;
        invalidate();
    }

    public void setProgressMin(int min) {
        this.mProgressMin = min;
        invalidate();
    }

}
