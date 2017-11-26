package com.zero.customview.view.likeview;

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

public class ThumbUpView extends View {
    private static final String TAG = "ThumbLikeView@wumin";
    private static final int DEFAULT_WIDTH = 128;
    private static final int DEFAULT_HEIGHT = 128;
    private static final float DEFAULT_NUMBER_SIZE = 24;
    private static final int DEFAULT_PADDING = 5;
    private static final int DEFAULT_NUMBER = 10;
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
    private int mPreNumber = DEFAULT_NUMBER;
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
    private Drawable mThumbUpLightDrawable;
    private ValueAnimator lightAnimator;
    private ObjectAnimator scaleUpAnimator;
    private ObjectAnimator textTransAnimator;
    private boolean isTouched = false;
    private boolean isLiked = false;
    private float scaleDownIndex;
    private float scaleUpIndex;
    private Matrix scaleDownMatrix;
    private Matrix scaleUpMxtrix;

    private onLikeListener mClickListener;
    public ThumbUpView(Context context) {
        this(context, null);
    }

    public ThumbUpView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThumbUpView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ThumbUpView);
        mLikeColor = ta.getColor(R.styleable.ThumbUpView_thumb_like_color, Color.RED);
        mNumberColor = ta.getColor(R.styleable.ThumbUpView_thumb_number_color, Color.GRAY);
        mNumberSize = ta.getDimension(R.styleable.ThumbUpView_thumb_number_size,
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
        scaleDownMatrix = new Matrix();
        scaleUpMxtrix = new Matrix();
        mThumbDrawable = mContext.getResources().getDrawable(R.mipmap.ic_thumb_up_normal);
        mThumbUpDrawable = mContext.getResources().getDrawable(R.mipmap.ic_thumb_up_selected);
        mThumbUpLightDrawable = mContext.getResources().getDrawable(R.mipmap.ic_thumb_up_selected_shining);

        mLightRadius = 0.0f;
        lightAnimator = ValueAnimator.ofFloat(DEFAULT_ANIM_SCALE_MIN, DEFAULT_ANIM_SCALE_MAX);
        lightAnimator.setDuration(DEFAULT_ANIM_DURATION);
        lightAnimator.setInterpolator(new AccelerateInterpolator());
        lightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float scaleValue = (float) animation.getAnimatedValue();
                mLightRadius = mLikeRadius * scaleValue;
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

        scaleUpAnimator = ObjectAnimator.ofFloat(this,
                "scaleUpIndex", DEFAULT_ANIM_SCALE_MIN, DEFAULT_ANIM_SCALE_NORMAL);
        scaleUpAnimator.setInterpolator(new OvershootInterpolator());
        scaleUpAnimator.setDuration(DEFAULT_ANIM_DURATION);

//        scaleAnimator = ValueAnimator.ofFloat(DEFAULT_ANIM_SCALE_NORMAL,
//                DEFAULT_ANIM_SCALE_MAX);
//        scaleAnimator.setDuration(DEFAULT_ANIM_DURATION);
//        scaleAnimator.setInterpolator(new OvershootInterpolator());
//        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float scaleValue = (float)animation.getAnimatedValue();
//                mLikeMaxRadius = mLikeRadius * scaleValue;
//                RectF rectF = new RectF(mLikeRect.centerX() - mLikeMaxRadius, mLikeRect.centerY() - mLikeMaxRadius,
//                        mLikeRect.centerX() + mLikeMaxRadius, mLikeRect.centerY() + mLikeMaxRadius);
//                mLikeRect.set(rectF);
//                invalidate();
//            }
//        });

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
        mLikeMaxRadius = mLikeRadius;
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
        drawLightCircle(canvas);
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
            Log.d(TAG, "drawIcon: bmp width -> " + bmp.getWidth() + " height -> " + bmp.getHeight()
                + " rect width -> " + dst.width() + " height -> " + dst.height());
            Bitmap scaleBmp = Bitmap.createBitmap(bmp, 0, 0,
                    bmp.getWidth(), bmp.getHeight(), scaleUpMxtrix, true);

            canvas.drawBitmap(scaleBmp, null, dst, null);
        } else {
            bmp = ((BitmapDrawable) mThumbDrawable).getBitmap();
            canvas.drawBitmap(bmp, null, dst, null);
            canvas.drawRect(mLikeRect, mLightPaint);
            canvas.drawPoint(mLikeRect.left, mLikeRect.top, mLightPaint);
        }
    }

    private void drawLightCircle(Canvas canvas) {
        if (isLiked) {
            canvas.drawCircle(mLikeRect.centerX(), mLikeRect.centerY(), mLightRadius, mLightPaint);
        }
        canvas.drawCircle(mLikeRect.centerX(), mLikeRect.centerY(), mLikeRadius, mLikePaint);
    }

    private void onClicked() {
        if (isAnimatorRunning()) {
            return;
        }
        isLiked = isLiked ? false:true;
        startLikeAnimation(isLiked);
        mClickListener.onLike(isLiked);
    }

    private boolean isAnimatorRunning() {
        return lightAnimator.isRunning() || scaleUpAnimator.isRunning();
    }

    private void startLikeAnimation(boolean liked) {
        if (liked) {
            startLightAnimation();
        }
        startScaleAnimation();
    }


    private void startScaleAnimation() {
        scaleUpAnimator.start();
    }

    private void startLightAnimation() {
        lightAnimator.start();
    }


    public float getScaleDownIndex() {
        return scaleDownIndex;
    }

    public void setScaleDownIndex(float scaleDownIndex) {
        this.scaleDownIndex = scaleDownIndex;
        scaleDownMatrix.postScale(scaleDownIndex, scaleDownIndex);
        postInvalidate();
    }

    public float getScaleUpIndex() {
        return scaleUpIndex;
    }

    public void setScaleUpIndex(float scaleUpIndex) {
        this.scaleUpIndex = scaleUpIndex;
        scaleUpMxtrix.postScale(scaleUpIndex, scaleUpIndex);
        postInvalidate();
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
