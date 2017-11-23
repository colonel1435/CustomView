package com.zero.customview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.zero.customview.R;


/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/11/20 0020 15:09
 */

public class ThumbLikeView extends View {
    private static final String TAG = "ThumbLikeView@wumin";
    private static final int DEFAULT_WIDTH = 256;
    private static final int DEFAULT_HEIGHT = 128;
    private static final float DEFAULT_NUMBER_SIZE = 24;
    private static final int DEFAULT_PADDING = 10;
    private static final int DEFAULT_NUMBER = 0;
    private static final int DEFAULT_ANIM_DURATION = 300;
    private static final float DEFAULT_ANIM_SCALE_MAX = 1.2f;
    private static final float DEFAULT_ANIM_SCALE_NORMAL = 1.0f;
    private static final float DEFAULT_ANIM_SCALE_MIN = 0.8f;
    private static final float DEFAULT_LIGHT_PAINT_WIDTH = 2f;


    private Context mContext;
    private Paint mLikePaint;
    private Paint mLightPaint;
    private Region mLikeRegion;
    private RectF mLikeRect;
    private Path mLikePath;
    private Matrix mMatrix;
    private Paint mNumberPaint;
    private int mNumber = DEFAULT_NUMBER;
    private int mNumberColor;
    private float mNumberSize;
    private int mLikeColor;
    private float mLikeRadius;
    private float mLikeMaxRadius;
    private float mLightRadius;
    private int mWidth;
    private int mHeight;
    private float baseX;
    private float baseY;
    private Drawable mThumbDrawable;
    private Drawable mThumbUpDrawable;
    private ValueAnimator lightAnimator;
    private ValueAnimator scaleAnimator;
    private ObjectAnimator textTransAnimator;
    private boolean isTouched = false;
    private boolean isLiked = false;
    private String[] textValues = new String[3];
    private float textTransY = 0.0f;

    private onLikeListener mClickListener;
    public ThumbLikeView(Context context) {
        this(context, null);
    }

    public ThumbLikeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThumbLikeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ThumbLikeView);
        mLikeColor = ta.getColor(R.styleable.ThumbLikeView_thumb_like_color, Color.RED);
        mNumberColor = ta.getColor(R.styleable.ThumbLikeView_thumb_number_color, Color.GRAY);
        mNumberSize = ta.getDimension(R.styleable.ThumbLikeView_thumb_number_size,
                DEFAULT_NUMBER_SIZE);
        ta.recycle();
        mContext = context;
        initView();
    }

    private void initView() {
        this.setClickable(true);
        mLikePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLikePaint.setColor(mLikeColor);
        mLikePaint.setStyle(Paint.Style.STROKE);

        mLightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLightPaint.setColor(Color.GRAY);
        mLightPaint.setStyle(Paint.Style.STROKE);
        mLightPaint.setStrokeWidth(DEFAULT_LIGHT_PAINT_WIDTH);

        mNumberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNumberPaint.setColor(mNumberColor);
        mNumberPaint.setTextSize(mNumberSize);

        mLikePath = new Path();
        mLikeRegion = new Region();
        mMatrix = new Matrix();
        mThumbDrawable = mContext.getResources().getDrawable(R.mipmap.ic_thumb_up);
        mThumbUpDrawable = mContext.getResources().getDrawable(R.mipmap.ic_thumb_up_like);

        mLightRadius = 0.0f;
        lightAnimator = ValueAnimator.ofFloat(DEFAULT_ANIM_SCALE_MIN, DEFAULT_ANIM_SCALE_MAX);
        lightAnimator.setDuration(DEFAULT_ANIM_DURATION);
        lightAnimator.setInterpolator(new AccelerateInterpolator());
        lightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float scaleValue = (float) animation.getAnimatedValue();
                mLightRadius = mLikeRadius * scaleValue;
                Log.d(TAG, "onAnimationUpdate: " + scaleValue);
                invalidate();
            }
        });
        lightAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLightRadius = 0.0f;
                invalidate();
            }
        });

        scaleAnimator = ValueAnimator.ofFloat(DEFAULT_ANIM_SCALE_NORMAL,
                DEFAULT_ANIM_SCALE_MAX);
        scaleAnimator.setDuration(DEFAULT_ANIM_DURATION);
        scaleAnimator.setInterpolator(new OvershootInterpolator());
        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float scaleValue = (float)animation.getAnimatedValue();
                mLikeMaxRadius = mLikeRadius * scaleValue;
                Log.d(TAG, "onAnimationUpdate: " + scaleValue);
                RectF rectF = new RectF(mLikeRect.centerX() - mLikeMaxRadius, mLikeRect.centerY() - mLikeMaxRadius,
                        mLikeRect.centerX() + mLikeMaxRadius, mLikeRect.centerY() + mLikeMaxRadius);
                mLikeRect.set(rectF);
                invalidate();
            }
        });

        parseText(0, 0);
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
        mLikeRadius = (w / 2 - DEFAULT_PADDING - getPaddingTop() - getPaddingBottom()) / 2;
        mLikeMaxRadius = mLikeRadius;
        baseX = (float)(mWidth / 2);
        baseY = (float)(mHeight / 2);

        mMatrix.reset();
        Region globalRegion = new Region(0, 0, w, h);
        float cx = mWidth / 4;
        float cy = baseY;
        mLikeRect = new RectF(cx - mLikeRadius, cy - mLikeRadius,
                cx + mLikeRadius, cy + mLikeRadius);
        mLikePath.addRect(mLikeRect, Path.Direction.CW);
        mLikeRegion.setPath(mLikePath, globalRegion);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMatrix.isIdentity()) {
            canvas.getMatrix().invert(mMatrix);
        }
        drawIcon(canvas);
        drawLightCircle(canvas);
        drawNumber(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float[] pts = new float[2];
        pts[0] = event.getRawX();
        pts[1] = event.getRawY();
        mMatrix.mapPoints(pts);

        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                isTouched = checkClicked(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (isTouched && checkClicked(x, y)) {
                    onClicked();
                }
                break;
            default:
                break;
        }

        invalidate();
        return super.onTouchEvent(event);
    }

    private boolean checkClicked(int x, int y) {
        return mLikeRegion.contains(x, y);
    }

    private int measureDimension(int measureSpec, int defaultSize) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = defaultSize;
        }
        return result;
    }

    private void drawIcon(Canvas canvas) {
        RectF dst = new RectF(mLikeRect);
        dst.inset(2*DEFAULT_PADDING, 2*DEFAULT_PADDING);
        Bitmap bmp;
        if (isLiked) {
            bmp = ((BitmapDrawable)mThumbUpDrawable).getBitmap();
        } else {
            bmp = ((BitmapDrawable) mThumbDrawable).getBitmap();
        }
        canvas.drawCircle(mLikeRect.centerX(), mLikeRect.centerY(), mLikeMaxRadius, mLikePaint);
        canvas.drawBitmap(bmp, null, dst, null);
    }

    private void drawLightCircle(Canvas canvas) {
        if (isLiked) {
            canvas.drawCircle(mLikeRect.centerX(), mLikeRect.centerY(), mLightRadius, mLightPaint);
            Log.d(TAG, "drawLightCircle: " + mLightRadius);
        }
        canvas.drawCircle(mLikeRect.centerX(), mLikeRect.centerY(), mLikeRadius, mLikePaint);
    }

    private void drawNumber(Canvas canvas) {
        String numStr = String.valueOf(mNumber);
        Paint.FontMetrics fontMetrics = mNumberPaint.getFontMetrics();

        float textWidth = mNumberPaint.measureText(numStr);
        float baseline_x = baseX + (mWidth / 2 - textWidth) / 2;
        float baseline_y = baseY - (fontMetrics.descent + fontMetrics.ascent) / 2;
        canvas.drawLine(0, baseY, mWidth, baseY, mNumberPaint);
//        canvas.drawText(numStr, baseline_x, baseline_y, mNumberPaint);
        float constTextWidth = 0;
        float variantTextWidth = mNumberPaint.measureText(textValues[1]);
        float cX = baseX + (mWidth / 2 - variantTextWidth) / 2;
        if (!"".equals(textValues[0])) {
            constTextWidth = mNumberPaint.measureText(textValues[0]);
            cX = baseX + (mWidth / 2 - constTextWidth - variantTextWidth) / 2 + constTextWidth / 2;
            canvas.drawText(textValues[0], cX, baseline_y, mNumberPaint);
        }

        float vX = cX + constTextWidth / 2 + constTextWidth / 2;
        /*** Draw last text ***/
        canvas.drawText(textValues[1], vX, baseline_y - (mHeight / 2) * textTransY, mNumberPaint);
        /*** Draw current text ***/
        canvas.drawText(textValues[2], vX, baseline_y + (mHeight / 2) * (1 - textTransY), mNumberPaint);
    }

    private void onLike() {
        textTransAnimator = ObjectAnimator.ofFloat(this, "textTransY", 0, 1);
        textTransAnimator.setInterpolator(new AccelerateInterpolator());
        textTransAnimator.setDuration(DEFAULT_ANIM_DURATION);
        textTransAnimator.start();
    }

    private void onDislike() {
        textTransAnimator = ObjectAnimator.ofFloat(this, "textTransY", 0, -1);
        textTransAnimator.setInterpolator(new AccelerateInterpolator());
        textTransAnimator.setDuration(DEFAULT_ANIM_DURATION);
        textTransAnimator.start();
    }

    private void onClicked() {
        if (isAnimatorRunning()) {
            return;
        }
        isLiked = isLiked ? false:true;
        startTextChanged();
        startLikeAnimation();
        mClickListener.onLike(isLiked);
    }

    private boolean isAnimatorRunning() {
        return lightAnimator.isRunning() || scaleAnimator.isRunning();
    }

    private void parseText(int previous, int current) {
        String previewText = String.valueOf(previous);
        String currentText = String.valueOf(current);
        if (previewText.length() != currentText.length()) {
            textValues[0] = "";
            textValues[1] = previewText;
            textValues[2] = currentText;
        } else {
            for (int i = 0; i < previewText.length(); i++) {
                if (previewText.charAt(i) != currentText.charAt(i)) {
                    if (i != 0) {
                        textValues[0] = "";
                    } else {
                        textValues[0] = previewText.substring(0, i);
                    }
                    textValues[1] = previewText.substring(i);
                    textValues[2] = currentText.substring(i);
                    break;
                }
            }
        }
    }
    private void  startTextChanged() {
        int previousNumber = mNumber;
        parseText(previousNumber, mNumber);
        if (isLiked) {
            onLike();
        } else {
            if (mNumber > 0) {
                mNumber --;
                onDislike();
            }
        }
    }

    private void startLikeAnimation() {
        if (isLiked) {
            startLightAnimation();
        }
        startScaleAnimation();
    }

    private void startScaleAnimation() {
        scaleAnimator.start();
    }

    private void startLightAnimation() {
        lightAnimator.start();
    }

    public int getNumber() {
        return mNumber;
    }

    public void setNumber(int mNumber) {
        this.mNumber = mNumber;
        invalidate();
    }

    public float getTextTransY() {
        return textTransY;
    }

    public void setTextTransY(float mTextTransY) {
        this.textTransY = mTextTransY;
        invalidate();
    }

    public onLikeListener getClickListener() {
        return mClickListener;
    }

    public void setClickListener(onLikeListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public interface onLikeListener {
        void onLike(boolean isLike);
    }
}
