package com.zero.customview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.orhanobut.logger.Logger;
import com.zero.customview.CanvasViewActivity;
import com.zero.customview.R;
import com.zero.customview.utils.DisplayUtils;

import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/7/17 0017 14:46
 */

public class MiClockView extends View {
    private Canvas mCanvas;
    private Paint mTextPaint;
    private Rect mTextRect;
    private Paint mCirclePaint;
    private float mCircleWidth = 2;
    private RectF mCircleRectF;
    private RectF mScaleRectF;
    private Paint mScaleLinePaint;
    private Paint mScaleArcPaint;
    private Paint mHourHandPaint;
    private Paint mMinuteHandPaint;
    private Paint mScondHandPaint;
    private Path mHoutHandPath;
    private Path mMinuteHandPath;
    private Path mSecondHandPath;

    private int mLightColor;
    private int mDarkColor;
    private int mBgColor;
    private float mTextSize;
    private float mRadius;
    private float mScaleLength;

    private float mHourAngle;
    private float mMinuteAngle;
    private float mSecondAngle;

    private float mDefaultPadding;
    private float mPaddingLeft;
    private float mPaddingRight;
    private float mPaddingTop;
    private float mPaddingBottom;

    private SweepGradient mSweepGradient;
    private Matrix mGradientMatrix;
    private Matrix mCameraMatrix;
    private Camera mCamera;
    private float mCameraRotateX;
    private float mCameraRotateY;
    private float mCameraRotateMax = 10;
    private float mCanvasTranslateX;
    private float mCanvasTranslateY;
    private float mCanvasTranslateMax;
    private ValueAnimator mShakeAnim;

    public MiClockView(Context context) {
        this(context, null);
    }

    public MiClockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MiClockView, defStyleAttr, 0);
        mBgColor = ta.getColor(R.styleable.MiClockView_backgroundColor, Color.parseColor("#237EAD"));
        mLightColor = ta.getColor(R.styleable.MiClockView_lightColor, Color.parseColor("#FFFFFF"));
        mDarkColor = ta.getColor(R.styleable.MiClockView_darkColor, Color.parseColor("#80FFFFFF"));
        mTextSize = ta.getDimension(R.styleable.MiClockView_textSize, DisplayUtils.sp2px(context, 14));
        ta.recycle();

        initView();
    }

    private void initView() {
        setBackgroundColor(mBgColor);

        mHourHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHourHandPaint.setStyle(Paint.Style.FILL);
        mHourHandPaint.setColor(mDarkColor);

        mMinuteHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMinuteHandPaint.setColor(mLightColor);

        mScondHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mScondHandPaint.setStyle(Paint.Style.FILL);
        mScondHandPaint.setColor(mBgColor);

        mScaleLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mScaleLinePaint.setStyle(Paint.Style.STROKE);
        mScaleLinePaint.setColor(mBgColor);

        mScaleArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mScaleArcPaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(mDarkColor);
        mTextPaint.setTextSize(mTextSize);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mCircleWidth);
        mCirclePaint.setColor(mDarkColor);

        mTextRect = new Rect();
        mCircleRectF = new RectF();
        mScaleRectF = new RectF();
        mHoutHandPath = new Path();
        mMinuteHandPath = new Path();
        mSecondHandPath = new Path();

        mGradientMatrix = new Matrix();
        mCameraMatrix = new Matrix();
        mCamera = new Camera();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureDimension(widthMeasureSpec), measureDimension(heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = Math.min(w - getPaddingLeft() - getPaddingRight(),
                            h - getPaddingTop() - getPaddingBottom()) / 2;
        mDefaultPadding = 0.15f * mRadius;
        mPaddingLeft = mDefaultPadding + w/ 2 - mRadius + getPaddingLeft();
        mPaddingTop = mDefaultPadding + h / 2 - mRadius + getPaddingTop();
        mPaddingRight = mPaddingLeft;
        mPaddingBottom = mPaddingTop;
        mScaleLength = 0.12f * mRadius;
        mScaleArcPaint.setStrokeWidth(mScaleLength);
        mScaleLinePaint.setStrokeWidth(0.012f*mRadius);
        mCanvasTranslateMax = 0.02f * mRadius;
        mSweepGradient = new SweepGradient(w/2, h/2,
                new int[]{mDarkColor, mLightColor},
                new float[]{0.75f, 1});


    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;
        setCameraRotate();
        getTimeAngle();
        drawTimeText();
        drawScaleLine();
        drawSecondHand();
        drawMinuteHand();
        drawHourHand();
        invalidate();
    }

    private int measureDimension(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = 800;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }

        return result;
    }

    private void setCameraRotate() {}

    private void getTimeAngle() {
        Calendar calendar = Calendar.getInstance();
        float millionSecond = calendar.get(Calendar.MILLISECOND);
        float second = calendar.get(Calendar.SECOND) + millionSecond / 1000;
        float minute = calendar.get(Calendar.MINUTE) + second / 60;
        float hour = calendar.get(Calendar.HOUR) + minute / 60;
        mSecondAngle = second / 60 * 360;
        mMinuteAngle = minute / 60 * 360;
        mHourAngle = hour / 12 * 360;

    }

    private void drawTimeText() {
        String timeText = "12";
        mTextPaint.getTextBounds(timeText, 0, timeText.length(), mTextRect);
        int textLargeWidth = mTextRect.width();
        mCanvas.drawText("12", getWidth()/2 - textLargeWidth/2, mPaddingTop + mTextRect.height(), mTextPaint);
        timeText = "3";
        mTextPaint.getTextBounds(timeText, 0, timeText.length(), mTextRect);
        int textSingleWidth = mTextRect.width();
        mCanvas.drawText("3", getWidth() - mPaddingRight - mTextRect.height()/2 - textSingleWidth/2,
                        getHeight() / 2 + mTextRect.height() / 2, mTextPaint);
        mCanvas.drawText("6", getWidth() / 2 - textSingleWidth / 2, getHeight() - mPaddingBottom, mTextPaint);
        mCanvas.drawText("9", mPaddingLeft + mTextRect.height() / 2 - textSingleWidth / 2,
                        getHeight() / 2 + mTextRect.height() / 2, mTextPaint);

        mCircleRectF.set(mPaddingLeft + mTextRect.height() / 2 + mCircleWidth / 2,
                        mPaddingTop + mTextRect.height() / 2 + mCircleWidth / 2,
                        getWidth() - mPaddingRight - mTextRect.height() / 2 + mCircleWidth / 2,
                        getHeight() - mPaddingBottom - mTextRect.height() / 2 + mCircleWidth / 2);

        for (int i = 0; i < 4; i++) {
            mCanvas.drawArc(mCircleRectF, 5+ 90 * i, 80, false, mCirclePaint);
        }
    }

    private void drawScaleLine() {
        mCanvas.save();


        for (int i = 0; i < 200; i++) {
            mCanvas.drawLine(getWidth()/2, mPaddingTop + mScaleLength + mTextRect.height() / 2,
                    getWidth() / 2,
                    mPaddingTop + 2 * mScaleLength + mTextRect.height() / 2,
                    mScaleLinePaint);
            mCanvas.rotate(1.8f, getWidth() / 2, getHeight() / 2);
        }
        mCanvas.restore();
    }

    private void drawSecondHand() {

    }

    private void drawMinuteHand() {}

    private void drawHourHand() {}

}
