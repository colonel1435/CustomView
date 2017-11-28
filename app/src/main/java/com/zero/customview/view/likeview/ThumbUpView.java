package com.zero.customview.view.likeview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.EventLog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.zero.customview.R;


/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/11/20 0020 15:09
 */

public class ThumbUpView extends View {
    private static final String TAG = "ThumbLikeView@wumin";
    private static final int DEFAULT_WIDTH = 128;
    private static final int DEFAULT_HEIGHT = 128;
    private static final int DEFAULT_PADDING = 5;
    private static final int DEFAULT_ANIM_DURATION = 300;
    private static final int DEFAULT_ANIM_DURATION_SHORT = 150;
    private static final float DEFAULT_ANIM_SCALE_MAX = 1.2f;
    private static final float DEFAULT_ANIM_SCALE_NORMAL = 1.0f;
    private static final float DEFAULT_ANIM_SCALE_MIN = 0.8f;
    private static final float DEFAULT_LIGHT_PAINT_WIDTH = 2f;
    private enum SCALE_TYPE{SCALE_UP, SCALE_DOWN}

    private Context mContext;
    private Paint mLightPaint;
    private Region mLikeRegion;
    private RectF mLikeRect;
    private Path mLikePath;
    private Matrix mMatrix;
    private float mLikeRadius;
    private float mLightRadius;
    private int mLightStartColor;
    private int mLightEndColor;
    private int mWidth;
    private int mHeight;
    private float baseX;
    private float baseY;
    private ArgbEvaluator argbEvaluator;
    private Bitmap mThumbUpBmp;
    private Bitmap mThumbNormalBmp;
    private Bitmap mThumnLightBmp;
    private ValueAnimator lightAnimator;
    private ObjectAnimator scaleUpAnimator;
    private ObjectAnimator scaleDownAnimator;
    private boolean isTouched = false;
    private boolean isLiked = false;
    private float scaleIndex;
    private Matrix lightMatrix;
    private Matrix scaleMatrix;
    private SCALE_TYPE scaleType = SCALE_TYPE.SCALE_UP;

    private OnThumbUpListener mClickListener;
    public ThumbUpView(Context context) {
        this(context, null);
    }

    public ThumbUpView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThumbUpView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        this.setClickable(true);
        mLightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLightPaint.setColor(Color.GRAY);
        mLightPaint.setStyle(Paint.Style.STROKE);
        mLightPaint.setStrokeWidth(DEFAULT_LIGHT_PAINT_WIDTH);

        mLightStartColor = Color.parseColor("#BFBFBF");
        mLightEndColor = Color.parseColor("#E4583E");

        mLikePath = new Path();
        mLikeRegion = new Region();
        mMatrix = new Matrix();
        scaleMatrix = new Matrix();
        lightMatrix = new Matrix();

        mLightRadius = 0.0f;
        argbEvaluator = new ArgbEvaluator();
        lightAnimator = ValueAnimator.ofFloat(DEFAULT_ANIM_SCALE_MIN, DEFAULT_ANIM_SCALE_MAX);
        lightAnimator.setDuration(DEFAULT_ANIM_DURATION);
        lightAnimator.setInterpolator(new AccelerateInterpolator());
        lightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float scaleValue = (float) animation.getAnimatedValue();
                mLightRadius = (mLikeRadius - 2 * DEFAULT_PADDING) * scaleValue;
                lightMatrix.setScale(scaleValue, scaleValue);
                mLightPaint.setColor((int)argbEvaluator.evaluate((scaleValue - 0.2f), mLightStartColor, mLightEndColor));
                invalidate();
            }
        });
        lightAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLightRadius = 0.0f;
            }
        });

        scaleIndex = DEFAULT_ANIM_SCALE_NORMAL;
        scaleUpAnimator = ObjectAnimator.ofFloat(this,
                "scaleIndex", DEFAULT_ANIM_SCALE_MIN, DEFAULT_ANIM_SCALE_NORMAL);
        scaleUpAnimator.setInterpolator(new OvershootInterpolator());
        scaleUpAnimator.setDuration(DEFAULT_ANIM_DURATION);
        scaleUpAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                Log.d(TAG, "onAnimationStart: scale up...");
                scaleMatrix.reset();
                scaleType = SCALE_TYPE.SCALE_UP;
            }
        });

        scaleDownAnimator = ObjectAnimator.ofFloat(this,
                "scaleIndex", DEFAULT_ANIM_SCALE_NORMAL, DEFAULT_ANIM_SCALE_MIN);
        scaleDownAnimator.setInterpolator(new AccelerateInterpolator());
        scaleDownAnimator.setDuration(DEFAULT_ANIM_DURATION_SHORT);
        scaleDownAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                Log.d(TAG, "onAnimationStart: scale down...");
                scaleMatrix.reset();
                scaleType = SCALE_TYPE.SCALE_DOWN;
            }
        });

        initBitmap();
    }

    private void initBitmap() {
        mThumbUpBmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_thumb_up_selected);
        mThumbNormalBmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_thumb_up_normal);
        mThumnLightBmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_thumb_up_selected_shining);
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
        mLikeRadius = (w - DEFAULT_PADDING - getPaddingTop() - getPaddingBottom()) / 2;
        baseX = (float)(mWidth / 2);
        baseY = (float)(mHeight / 2);

        mMatrix.reset();
        Region globalRegion = new Region(0, 0, w, h);
        mLikeRect = new RectF(baseX - mLikeRadius, baseY - mLikeRadius,
                baseX + mLikeRadius, baseY + mLikeRadius);
        mLikePath.addRect(mLikeRect, Path.Direction.CW);
        mLikeRegion.setPath(mLikePath, globalRegion);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMatrix.isIdentity()) {
            canvas.getMatrix().invert(mMatrix);
        }
        drawIcon(canvas);
        if (isLiked && (scaleType == SCALE_TYPE.SCALE_UP)) {
            Log.d(TAG, "onDraw: draw light & circle...");
            drawLight(canvas);
            drawLightCircle(canvas);
        }
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
        Bitmap scaleDownBmp;
        Bitmap scaleUpBmp;
        dst.inset(dst.width() * (1 - scaleIndex) + 2*DEFAULT_PADDING,
                dst.height() * (1 - scaleIndex) + 2*DEFAULT_PADDING);
        if (scaleType == SCALE_TYPE.SCALE_UP) {
            if (isLiked) {
                scaleUpBmp = Bitmap.createBitmap(mThumbUpBmp, 0, 0,
                        mThumbUpBmp.getWidth(), mThumbUpBmp.getHeight(), scaleMatrix, true);
            } else {
                scaleUpBmp = Bitmap.createBitmap(mThumbNormalBmp, 0, 0,
                        mThumbNormalBmp.getWidth(), mThumbNormalBmp.getHeight(), scaleMatrix, true);
            }
            canvas.drawBitmap(scaleUpBmp, null, dst, null);
        } else {
            if (isLiked) {
                scaleDownBmp = Bitmap.createBitmap(mThumbNormalBmp, 0, 0,
                        mThumbNormalBmp.getWidth(), mThumbNormalBmp.getHeight(), scaleMatrix, true);
            } else {
                scaleDownBmp = Bitmap.createBitmap(mThumbUpBmp, 0, 0,
                        mThumbUpBmp.getWidth(), mThumbUpBmp.getHeight(), scaleMatrix, true);
            }
            canvas.drawBitmap(scaleDownBmp, null, dst, null);
        }
    }

    private void drawLight(Canvas canvas) {
        Bitmap lightBmp = Bitmap.createBitmap(mThumnLightBmp, 0, 0,
                mThumnLightBmp.getWidth(), mThumnLightBmp.getWidth(), lightMatrix, true);
        RectF rectF = new RectF(baseX - lightBmp.getWidth() * 0.5f, baseY * 0.5f - lightBmp.getHeight() * 0.5f,
                baseX + lightBmp.getWidth() * 0.5f, baseY * 0.5f + lightBmp.getHeight() * 0.5f);
        canvas.drawBitmap(lightBmp, null, rectF, null);
    }

    private void drawLightCircle(Canvas canvas) {
        canvas.drawCircle(mLikeRect.centerX(), mLikeRect.centerY(), mLightRadius, mLightPaint);
    }

    private void onClicked() {
        if (isAnimatorRunning()) {
            return;
        }
        isLiked = isLiked ? false:true;
        startLikeAnimation(isLiked);
        if (mClickListener != null) {
            mClickListener.onLike(isLiked);
        }
    }

    private boolean isAnimatorRunning() {
        return lightAnimator.isRunning() || scaleUpAnimator.isRunning();
    }

    private void startLikeAnimation(boolean liked) {
        AnimatorSet animatorSet = new AnimatorSet();
        if (liked) {
            animatorSet.play(scaleUpAnimator).with(lightAnimator).after(scaleDownAnimator);
        } else {
            animatorSet.play(scaleUpAnimator).after(scaleDownAnimator);
        }
        animatorSet.start();
    }

    public int getLightStartColor() {
        return mLightStartColor;
    }

    public void setLightStartColor(int mLightStartColor) {
        this.mLightStartColor = mLightStartColor;
    }

    public int getLightEndColor() {
        return mLightEndColor;
    }

    public void setLightEndColor(int mLightEndColor) {
        this.mLightEndColor = mLightEndColor;
    }

    public float getScaleIndex() {
        return scaleIndex;
    }

    public void setScaleIndex(float scaleIndex) {
        this.scaleIndex = scaleIndex;
        scaleMatrix.postScale(scaleIndex, scaleIndex);
        invalidate();
    }

    public ArgbEvaluator getArgbEvaluator() {
        return argbEvaluator;
    }

    public void setArgbEvaluator(ArgbEvaluator evaluator) {
        this.argbEvaluator = evaluator;
        invalidate();
    }

    public OnThumbUpListener getClickListener() {
        return mClickListener;
    }

    public void setClickListener(OnThumbUpListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public interface OnThumbUpListener {
        void onLike(boolean isLike);
    }
}
