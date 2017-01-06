package com.zero.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.zero.customview.R;

/**
 * Created by Administrator on 2017/1/5.
 */

public class HorizontalProgressBar extends ProgressBar {
    public final String TAG = this.getClass().getSimpleName();
    private static final int DEFAULT_TEXT_SIZE = 16;
    private static final int DEFAULT_TEXT_COLOR = 0xFFFC00D1;
    private static final int DEFAULT_TEXT_OFFSET = 5;
    private static final int DEFAULT_UNREACHED_COLOR = 0xFFd3d6da;
    private static final int DEFAULT_HEIGHT_REACHED = 5;
    private static final int DEFAULT_HEIGHT_UNREACHED = 5 ;

    protected Paint mPaint = new Paint();
    protected int mTextColor = DEFAULT_TEXT_COLOR;
    protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
    protected int mTextOffset = dp2px(DEFAULT_TEXT_OFFSET);

    protected int mReachedBarHeight = dp2px(DEFAULT_HEIGHT_REACHED);
    protected int mReachedBarColor = DEFAULT_TEXT_COLOR;
    protected int mUnreachedBarHeight = dp2px(DEFAULT_HEIGHT_UNREACHED);
    protected int mUnreachedBarColor = DEFAULT_UNREACHED_COLOR;

    protected int mRealWidth;
    protected boolean mDrawText = true;
    protected static final int VISIABLE = 0;

    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initSetup(attrs);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSetup(attrs);
    }

    private void initSetup(AttributeSet attrs) {
        setHorizontalScrollBarEnabled(true);
        obtainStyleAttributes(attrs);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);
    }
    private void obtainStyleAttributes(AttributeSet attrs) {
        final TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalProgressBar);
        mTextColor = attributes.getColor(R.styleable.HorizontalProgressBar_progress_text_color, DEFAULT_TEXT_COLOR);
        mTextSize = (int)attributes.getDimension(R.styleable.HorizontalProgressBar_progress_text_size, DEFAULT_TEXT_SIZE);
        mTextOffset = (int)attributes.getDimension(R.styleable.HorizontalProgressBar_progress_text_offset, mTextOffset);
        mReachedBarColor = attributes.getColor(R.styleable.HorizontalProgressBar_progress_reached_color, mTextColor);
        mReachedBarHeight = (int)attributes.getDimension(R.styleable.HorizontalProgressBar_progress_reached_height, DEFAULT_HEIGHT_REACHED);
        mUnreachedBarColor = attributes.getColor(R.styleable.HorizontalProgressBar_progress_unreached_color, DEFAULT_UNREACHED_COLOR);
        mUnreachedBarHeight = (int)attributes.getDimension(R.styleable.HorizontalProgressBar_progress_unreached_height, DEFAULT_HEIGHT_UNREACHED);
        int textVisiable = attributes.getInt(R.styleable.HorizontalProgressBar_progress_text_visiable, VISIABLE);
        if (textVisiable != VISIABLE) {
            mDrawText = false;
        }
        attributes.recycle();

        Log.i(TAG, "obtainStyleAttributes...");
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            float textHeight = (mPaint.descent() + mPaint.ascent());
            int exceptHeight = (int)(getPaddingTop() + getPaddingBottom() + Math.max(Math.max(mReachedBarHeight, mUnreachedBarHeight), Math.abs(textHeight)));
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(exceptHeight, MeasureSpec.EXACTLY);
        }
        mRealWidth = getMeasuredWidth() - getPaddingRight() - getPaddingLeft();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getHeight()/2);

        Log.i(TAG, "onDraw...");

        boolean noNeedBg = false;
        float radio = getProgress() * 1.0f / getMax();
        float progressPosX = (int) (mRealWidth * radio);
        String text = getProgress() + "%";

        float textWidth = mPaint.measureText(text);
        float textHeight = (mPaint.descent() + mPaint.ascent())/2;

        if (progressPosX + textWidth > mRealWidth) {
            progressPosX = mRealWidth - textWidth;
            noNeedBg = true;
        }

        float endX = progressPosX - mTextOffset/2;
        if (endX > 0) {
            mPaint.setColor(mReachedBarColor);
            mPaint.setStrokeWidth(mReachedBarHeight);
            canvas.drawLine(0, 0, endX, 0, mPaint);
        }

        if (mDrawText) {
            mPaint.setColor(mTextColor);
            canvas.drawText(text, progressPosX, -textHeight, mPaint );
        }

        if (!noNeedBg) {
            float start = progressPosX + mTextOffset / 2 + textWidth;
            mPaint.setColor(mUnreachedBarColor);
            mPaint.setStrokeWidth(mUnreachedBarHeight);
            canvas.drawLine(start, 0, mRealWidth, 0, mPaint);
        }
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRealWidth = w - getPaddingRight() - getPaddingLeft();
    }

    protected int dp2px(int val) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                val, getResources().getDisplayMetrics());
    }

    protected int sp2px(int val) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                val, getResources().getDisplayMetrics());
    }
}
