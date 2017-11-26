package com.zero.customview.view.likeview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.zero.customview.R;

import org.w3c.dom.ProcessingInstruction;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/11/25 0025 8:58
 */

public class ShiftNumberView extends View {
    private static final String TAG = "ShiftTextView@wuming";
    private static final float DEFAULT_TEXT_SIZE = 16f;
    private static final int DEFAULT_WIDTH = 128;
    private static final int DEFAULT_HEIGHT = 128;
    private static final long DEFAULT_ANIM_DURATION = 300;
    private int mWidth;
    private int mHeight;
    private float centerX;
    private float centerY;
    private String mPreviousText;
    private String mText;
    private Paint mTextPaint;
    private int mTextColor;
    private float mTextSize;
    private String[] textValues = new String[3];
    private float shiftOffsetY;
    private ObjectAnimator mShiftAnimator;
    private long mAnimationDuration;
    private boolean isUpper = false;

    public ShiftNumberView(Context context) {
        this(context, null);
    }

    public ShiftNumberView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShiftNumberView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ShiftNumberView);
        mText = ta.getString(R.styleable.ShiftNumberView_shift_textview);
        mTextColor = ta.getColor(R.styleable.ShiftNumberView_shift_textview_color, Color.GRAY);
        mTextSize = ta.getDimension(R.styleable.ShiftNumberView_shift_textview_size,
                DEFAULT_TEXT_SIZE);
        ta.recycle();
        initView();
    }

    private void initView() {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);

        if ("".equals(mText)) {
            mText = String.valueOf(0);
        }
        mPreviousText = mText;
        mAnimationDuration = DEFAULT_ANIM_DURATION;
    }

    private int measureDimension(int measureSpec, int defaultValue) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (MeasureSpec.EXACTLY == mode) {
            result = size;
        } else {
            result = Math.min(size, defaultValue);
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
        centerX = w / 2;
        centerY = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        if (!"".equals(mText)) {
            parseText(mPreviousText, mText);
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            float textWidth = mTextPaint.measureText(mText);
            float textHeight = (fontMetrics.descent - fontMetrics.ascent);
            float baseline_x = (mWidth - textWidth) / 2
                            + getPaddingLeft() - getPaddingRight();
            float baseline_y = centerY - (fontMetrics.descent + fontMetrics.ascent) / 2
                            + getPaddingTop() - getPaddingBottom();
//            canvas.drawLine(0, baseline_y, mWidth, baseline_y, mTextPaint);
//            canvas.drawLine(centerX, 0, centerX, mHeight, mTextPaint);

            /**
             *  Draw const text
             */
            float constWidth = 0.0f;
            if (!"".equals(textValues[0])) {
                constWidth = mTextPaint.measureText(textValues[0]);
                canvas.drawText(textValues[0], baseline_x, baseline_y, mTextPaint);
            }

            float variantX = baseline_x + constWidth;
            float variantY;
            float halfHeight = 0.5f * (mHeight + textHeight);

            /**
             *  Current text offset
             */
            if (isUpper) {
                variantY = baseline_y + halfHeight * (1 - shiftOffsetY);
            } else {
                variantY = baseline_y - halfHeight * (1 + shiftOffsetY);
            }

            /***    Draw last text      ***/
            canvas.drawText(textValues[1], variantX,
                    baseline_y - halfHeight * shiftOffsetY, mTextPaint);
            /***    Draw current text   ***/
            canvas.drawText(textValues[2], variantX, variantY, mTextPaint);
        }
    }

    /**
     *  Parse text to const, last variant, current variant
     */
    private void parseText(String previous, String current) {
        if (current.equals(previous)) {
            textValues[0] = current;
            textValues[1] = "";
            textValues[2] = "";
        }
        if (previous.length() != current.length()) {
            textValues[0] = "";
            textValues[1] = previous;
            textValues[2] = current;
        } else {
            for (int i = 0; i < previous.length(); i++) {
                if (previous.charAt(i) != current.charAt(i)) {
                    if (i == 0) {
                        textValues[0] = "";
                    } else {
                        textValues[0] = previous.substring(0, i);
                    }
                    textValues[1] = previous.substring(i);
                    textValues[2] = current.substring(i);
                    break;
                }
            }
        }
        Log.d(TAG, "parseText: const -> " + textValues[0] +
                " last -> " + textValues[1] + " Current -> " + textValues[2]);
    }

    private void upper() {
        isUpper = true;
        mShiftAnimator = ObjectAnimator.ofFloat(this, "shiftOffsetY",
                0, 1);
        mShiftAnimator.setInterpolator(new AccelerateInterpolator());
        mShiftAnimator.setDuration(mAnimationDuration);
        mShiftAnimator.start();
    }

    private void lower() {
        isUpper = false;
        mShiftAnimator = ObjectAnimator.ofFloat(this, "shiftOffsetY",
                0, -1);
        mShiftAnimator.setInterpolator(new AccelerateInterpolator());
        mShiftAnimator.setDuration(mAnimationDuration);
        mShiftAnimator.start();
    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mPreviousText = this.mText;
        this.mText = mText;
        Log.d(TAG, "setText: preview -> "  +mPreviousText + " current -> " + mText);
        float current = Float.parseFloat(mText);
        float previous = Float.parseFloat(mPreviousText);
        if (current < previous) {
            lower();
        } else {
            upper();
        }
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
        this.mTextPaint.setColor(mTextColor);
        invalidate();
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float mTextSize) {
        this.mTextSize = mTextSize;
        this.mTextPaint.setTextSize(mTextSize);
        invalidate();
    }

    public float getShiftOffsetY() {
        return shiftOffsetY;
    }

    public void setShiftOffsetY(float shiftOffsetY) {
        this.shiftOffsetY = shiftOffsetY;
        invalidate();
    }
}
