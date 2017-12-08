package com.zero.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.method.SingleLineTransformationMethod;
import android.util.AttributeSet;
import android.view.View;

import com.zero.customview.R;
import com.zero.customview.utils.DisplayUtils;

/**
 * Description
 *
 * @author : Mr.wuming
 * @email : fusu1435@163.com
 * @date : 2017/12/8 0008 14:58
 */

public class WaveProgressBar extends View {
    private final String TAG = this.getClass().getSimpleName()+"@wuming";
    private final static float DEFAULT_TEXT_SIZE = 12;
    private final static int DEFAULT_WIDTH = 256;
    private final static int DEFAULT_HEIGHT = 256;
    private final static int DEFAULT_PADDING = 2;
    private final static int DEFAULT_PROGRESS_MAX = 100;
    private Context mContext;

    private float mTextSize;
    private int mTextColor;
    private int mDropColor;
    private int mBackgroundColor;
    private int mFillColor;
    private Paint mTextPaint;
    private Paint mBackgroundPaint;
    private Paint mFillPaint;
    private Paint mDropPaint;
    private Drawable mDrawable;
    private Path mFillPath;

    private int mWidth;
    private int mHeight;
    private int mCenterX;
    private int mCenterY;
    private float mRadius;
    private int mDefaultPadding;
    private float lastY;
    private float currentY;
    private float mFillHeight;
    private float mFillWidth;
    private float mHalfWaveWidth;
    private int mWaveSpeed = 30;
    private float mDistance;

    private int mMaxProgress;
    private int progressValue;
    private String progressText;

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
        ta.recycle();
        initView();
    }

    private void initView() {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFillPaint.setColor(mFillColor);
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(mBackgroundColor);
        mFillPath = new Path();

        mDropPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDropPaint.setColor(mDropColor);

        mDrawable = getBackground();
        if (null == mDrawable) {
            mDrawable = new ShapeDrawable(new OvalShape());
        }
        mDrawable.setColorFilter(mBackgroundColor, PorterDuff.Mode.SRC_IN);

        mDefaultPadding = DisplayUtils.dip2px(mContext, DEFAULT_PADDING);
        mMaxProgress = DEFAULT_PROGRESS_MAX;
        progressValue = 50;
        progressText = String.valueOf(progressValue) + "%";
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
        mHalfWaveWidth = mRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWave(canvas);
        drawText(canvas);
        drawDrop(canvas);
    }

    private void drawWave(Canvas canvas) {
        canvas.save();
        canvas.translate(mCenterX, mCenterY);
        /***    Draw backgound    ***/
        mDrawable.setBounds(-mCenterX, -mCenterY, mCenterX, mCenterY);
        mDrawable.draw(canvas);
        canvas.restore();
        /***    Draw progress   ***/
        mFillPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        currentY = mHeight * (mMaxProgress - progressValue) / mMaxProgress;
        mFillPath.reset();
        mFillPath.moveTo(-mDistance, currentY);
        int waveNum = mWidth / ((int) mHalfWaveWidth * 4) + 1;
        int mult = 0;
        for (int i = 0; i < waveNum; i++) {
            mFillPath.quadTo(mHalfWaveWidth * (mult + 1) - mDistance, currentY - mFillHeight,
                                mHalfWaveWidth * (mult + 2)-mDistance, currentY);
            mFillPath.quadTo(mHalfWaveWidth * (mult + 3) - mDistance, currentY + mFillHeight,
                    mHalfWaveWidth * (mult + 4) - mDistance, currentY);
            mult += 4;
        }
        mDistance += mHalfWaveWidth / mWaveSpeed;
        mDistance = mDistance % (mHalfWaveWidth * 4);

        mFillPath.lineTo(mWidth, mHeight);
        mFillPath.lineTo(0, mHeight);
        mFillPath.close();
        canvas.drawPath(mFillPath, mFillPaint);
    }

    private void drawText(Canvas canvas) {
        canvas.save();
        canvas.translate(mCenterX, mCenterY);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        canvas.drawText(progressText, 0, -(fontMetrics.descent + fontMetrics.ascent)*0.5f ,
                mTextPaint);
        canvas.drawLine(-mCenterX, 0, mCenterX, 0, mTextPaint);
        canvas.drawLine(0, -mCenterY, 0, mCenterY, mTextPaint);
        canvas.restore();
    }

    private void drawDrop(Canvas canvas) {

    }
}
