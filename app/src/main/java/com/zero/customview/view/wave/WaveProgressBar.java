package com.zero.customview.view.wave;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.zero.customview.R;
import com.zero.customview.utils.DisplayUtils;

import static android.animation.ValueAnimator.INFINITE;

/**
 * Description
 *
 * @author : Mr.wuming
 * @email : fusu1435@163.com
 * @date : 2017/12/8 0008 14:58
 */

public class WaveProgressBar extends View implements IWaveView{
    private final String TAG = this.getClass().getSimpleName()+"@wuming";
    private final static float DEFAULT_TEXT_SIZE = 12;
    private final static int DEFAULT_WIDTH = 256;
    private final static int DEFAULT_HEIGHT = 256;
    private final static int DEFAULT_PADDING = 2;
    private final static int DEFAULT_PROGRESS_MAX = 100;
    private final static int DEFAULT_PROGRESS_VALUE = 0;
    private final static float DEFAULT_WAVE_VELOCITY = 30f;
    private final static long DEFAULT_ANIMATION_DURATION = 1000;
    private final static String DEFAULT_PROGRESS_POSTFIX = "%";
    private Context mContext;

    private float mTextSize;
    private int mTextColor;
    private int mDropColor;
    private int mBackgroundColor;
    private int mFillColor;
    private Paint mTextPaint;
    private Paint mFillPaint;
    private Paint mDropPaint;
    private Drawable mDrawable;
    private Bitmap mBitmapSrc;
    private Path mFillPath;
    private PorterDuffXfermode xfermode;
    private ColorFilter colorFilter;
    private RectF srcRect;

    private int mWidth;
    private int mHeight;
    private int mCenterX;
    private int mCenterY;
    private float mRadius;
    private int mDefaultPadding;
    private float currentY;
    private float mWaveAmplitude = 15;
    private float mHalfWaveWidth;

    private int mMaxProgress;
    private int mProgressValue;
    private String mProgressText;

    private ValueAnimator mWaveAnimator;
    private float mWaveVelocity;
    private float mWaveOffsetX;

    public WaveProgressBar(Context context) {
        this(context, null);
    }

    public WaveProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WaveProgressBar);
        mTextSize = ta.getDimension(R.styleable.WaveProgressBar_wave_progress_text_size,
                DisplayUtils.sp2px(mContext, DEFAULT_TEXT_SIZE));
        mTextColor = ta.getColor(R.styleable.WaveProgressBar_wave_progress_text_color,
                Color.BLACK);
        mDropColor = ta.getColor(R.styleable.WaveProgressBar_wave_progress_drop_color,
                Color.WHITE);
        mBackgroundColor = ta.getColor(R.styleable.WaveProgressBar_wave_progress_background,
                Color.GRAY);
        mFillColor = ta.getColor(R.styleable.WaveProgressBar_wave_progress_fill_color,
                Color.BLUE);
        mWaveVelocity = ta.getFloat(R.styleable.WaveProgressBar_wave_progress_wave_velocity,
                DEFAULT_WAVE_VELOCITY);
        mDrawable = ta.getDrawable(R.styleable.WaveProgressBar_wave_progress_src);
        mMaxProgress = ta.getInt(R.styleable.WaveProgressBar_wave_process_max,
                DEFAULT_PROGRESS_MAX);
        mProgressValue = ta.getInt(R.styleable.WaveProgressBar_wave_progress,
                DEFAULT_PROGRESS_VALUE);
        ta.recycle();
        initView();
    }

    private void initView() {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPath = new Path();

        mDropPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDropPaint.setColor(mDropColor);

        srcRect = new RectF();

        if (null != mDrawable) {
            mDrawable.setColorFilter(mBackgroundColor, PorterDuff.Mode.SRC_IN);
            mBitmapSrc = ((BitmapDrawable)mDrawable).getBitmap();
        }
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);
        colorFilter = new PorterDuffColorFilter(mBackgroundColor, PorterDuff.Mode.SRC_ATOP);

        mDefaultPadding = DisplayUtils.dip2px(mContext, DEFAULT_PADDING);
        mProgressText = String.valueOf(mProgressValue) + DEFAULT_PROGRESS_POSTFIX;

        mWaveAnimator = ValueAnimator.ofFloat(0.0f);
        mWaveAnimator.setInterpolator(new LinearInterpolator());
        mWaveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = (float)animation.getAnimatedValue();
                if (fraction > 0) {
                    mWaveOffsetX += mHalfWaveWidth / mWaveVelocity;
                    mWaveOffsetX = mWaveOffsetX % (mHalfWaveWidth * 4);
                    invalidate();
                    Log.d(TAG, "onAnimationUpdate: value -> " + fraction + " offset -> " + mWaveOffsetX);
                }
            }
        });
        mWaveAnimator.setDuration(DEFAULT_ANIMATION_DURATION);
        mWaveAnimator.setRepeatCount(INFINITE);
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
        mCenterX = (int)((w + getPaddingLeft() - getPaddingRight()) * 0.5f);
        mCenterY = (int)((h + getPaddingTop() - getPaddingBottom()) * 0.5f);
        mRadius = Math.min(w - getPaddingLeft() - getPaddingRight() - 2 * mDefaultPadding,
                h - getPaddingTop() - getPaddingBottom() - 2 * mDefaultPadding) * 0.5f;
        mHalfWaveWidth = mRadius / 2;
        mWaveAnimator.setFloatValues(mHalfWaveWidth*4);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawFacade(canvas);
        drawText(canvas);
        if (!mWaveAnimator.isRunning()) {
            Log.d(TAG, "onDraw: start animation!");
            startAnimator(mWaveAnimator);
        }
    }

    @Override
    public void drawFacade(Canvas canvas) {
        int count = canvas.saveLayer(null, mFillPaint, Canvas.ALL_SAVE_FLAG);
        currentY = mHeight * (mMaxProgress - mProgressValue) / mMaxProgress;
        canvas.translate(0, currentY);
        /***    Draw backgound    ***/
        mFillPaint.setColorFilter(colorFilter);
        if (null != mDrawable) {
            srcRect.set(getPaddingLeft(), mCenterY - currentY - mRadius,
                    getPaddingLeft() + 2 * mRadius, mCenterY - currentY + mRadius);
            canvas.drawBitmap(mBitmapSrc, null, srcRect, mFillPaint);
        } else {
            canvas.drawCircle(mCenterX, mCenterY - currentY, mRadius, mFillPaint);
        }
        /***    Draw progress   ***/
        mFillPaint.setColorFilter(null);
        mFillPaint.setColor(mFillColor);
        mFillPaint.setXfermode(xfermode);
        mFillPath.reset();
        mFillPath.moveTo(-mWaveOffsetX, 0);
        int waveNum = mWidth / ((int) mHalfWaveWidth * 4) + 1;
        for (int i = 0; i < waveNum; i++) {
            mFillPath.rQuadTo(mHalfWaveWidth,  - mWaveAmplitude,
                    2 * mHalfWaveWidth, 0);
            mFillPath.rQuadTo(mHalfWaveWidth, mWaveAmplitude,
                    2 * mHalfWaveWidth, 0);
        }

        mFillPath.lineTo(mWidth, mHeight);
        mFillPath.lineTo(0, mHeight);
        mFillPath.close();
        canvas.drawPath(mFillPath, mFillPaint);
        mFillPaint.setXfermode(null);
        canvas.restoreToCount(count);
    }

    @Override
    public void drawText(Canvas canvas) {
        canvas.save();
        canvas.translate(mCenterX, mCenterY);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        canvas.drawText(mProgressText, 0, -(fontMetrics.descent + fontMetrics.ascent)*0.5f ,
                mTextPaint);
        canvas.restore();
    }

    @Override
    public void startAnimator(Animator animator) {
        animator.start();
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public void setMaxProgress(int mMaxProgress) {
        this.mMaxProgress = mMaxProgress;
    }

    public int getProgressValue() {
        return mProgressValue;
    }

    public void setProgressValue(int mProgressValue) {
        this.mProgressValue = mProgressValue;
        this.mProgressText = String.valueOf(mProgressValue) + DEFAULT_PROGRESS_POSTFIX;
        invalidate();
    }

    public void setProgressValue(int mProgressValue, String text) {
        this.mProgressValue = mProgressValue;
        this.mProgressText = text;
        invalidate();
    }
}
