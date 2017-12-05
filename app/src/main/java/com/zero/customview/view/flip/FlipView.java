package com.zero.customview.view.flip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.zero.customview.R;

/**
 * Description
 *
 * @author : Mr.wuming
 * @email : fusu1435@163.com
 * @date : 2017/12/5 0005 11:37
 */

public class FlipView extends View {
    private final String TAG = this.getClass().getSimpleName() + "@wuming";
    private static final int DEFAULT_WIDTH = 320;
    private static final int DEFAULT_HEIGHT = 320;
    private static final int DEFAULT_ANIMATION_DURATION_MAX = 1000;
    private static final int DEFAULT_ANIMATION_DURATION_NORMAL = 800;
    private static final int DEFAULT_ANIMATION_DURATION_MIN = 600;
    private Context mContext;
    private Paint mSrcPaint;
    private Bitmap mSrcBmp;
    private Drawable mSrcDrawable;
    private Camera mCamera;

    private int mWidth;
    private int mHeight;
    private float mCenterX;
    private float mCenterY;
    private int mBmpWidth;
    private int mBmpHeight;

    private float degreeX;
    private float degreeY;
    private float degreeZ;

    public FlipView(Context context) {
        this(context, null);
    }

    public FlipView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlipView);
        mSrcDrawable = ta.getDrawable(R.styleable.FlipView_flip_backgound);
        ta.recycle();
        mContext = context;
        initView();
    }

    private void initView() {
        mSrcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (mSrcDrawable != null) {
            mSrcBmp = ((BitmapDrawable) mSrcDrawable).getBitmap();
        } else {
            mSrcBmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_google_map);
        }
        mBmpWidth = mSrcBmp.getWidth();
        mBmpHeight = mSrcBmp.getHeight();

        mCamera = new Camera();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float newZ = -displayMetrics.density * 6;
        mCamera.setLocation(0, 0, newZ);

        resetView();
    }

    private void resetView() {
        degreeX = 0.0f;
        degreeY = 0.0f;
        degreeZ = 0.0f;
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
        mCenterX = getPaddingLeft() + w * 0.5f - getPaddingRight();
        mCenterY = getPaddingTop()  + h * 0.5f - getPaddingBottom();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawSrc(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent: down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onTouchEvent: move");
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchEvent: up");
                onAnimatorStart();
                break;
            default:
                break;
        }
        return true;
    }

    private void drawSrc(Canvas canvas) {
        int x = (int)(mCenterX - mBmpWidth * 0.5f);
        int y = (int)(mCenterY - mBmpHeight * 0.5f);

        /**
         *  Draw half of transform page
         */
        /***    1, transform    ***/
        canvas.save();
        mCamera.save();
        canvas.translate(mCenterX, mCenterY);
        canvas.rotate(-degreeZ);
        mCamera.rotateY(degreeY);
        mCamera.applyToCanvas(canvas);
        /***    2, Clip canvas rect    ***/
        canvas.clipRect(0, - mCenterY, mCenterX, mCenterY);
        canvas.rotate(degreeZ);
        canvas.translate(-mCenterX, -mCenterY);
        mCamera.restore();
        canvas.drawBitmap(mSrcBmp, x, y, mSrcPaint);
        canvas.restore();
        /***    Draw half of const page    ***/
        canvas.save();
        mCamera.save();
        canvas.translate(mCenterX, mCenterY);
        canvas.rotate(-degreeZ);
        mCamera.rotateY(degreeX);
        mCamera.applyToCanvas(canvas);
        canvas.clipRect(-mCenterX, -mCenterY, 0, mCenterY);
        canvas.rotate(degreeZ);
        canvas.translate(-mCenterX, -mCenterY);
        mCamera.restore();
        canvas.drawBitmap(mSrcBmp, x, y, mSrcPaint);
        canvas.restore();
    }

    private void onAnimatorStart() {
        ObjectAnimator rotateYAnimator =
                ObjectAnimator.ofFloat(this, "degreeY", 0f, -45f);
        rotateYAnimator.setDuration(DEFAULT_ANIMATION_DURATION_MAX);
        ObjectAnimator rotateXAnimator =
                ObjectAnimator.ofFloat(this, "degreeX", 0f, 30);
        rotateXAnimator.setDuration(DEFAULT_ANIMATION_DURATION_MIN);

        ObjectAnimator rotateZAnimator =
                ObjectAnimator.ofFloat(this, "degreeZ", 0, 270);
        rotateZAnimator.setDuration(DEFAULT_ANIMATION_DURATION_NORMAL);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(rotateYAnimator, rotateZAnimator, rotateXAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                resetView();
            }
        });
        animatorSet.start();
    }

    public float getDegreeX() {
        return degreeX;
    }

    public void setDegreeX(float degreeX) {
        this.degreeX = degreeX;
        invalidate();
    }

    public float getDegreeY() {
        return degreeY;
    }

    public void setDegreeY(float degreeY) {
        this.degreeY = degreeY;
        invalidate();
    }

    public float getDegreeZ() {
        return degreeZ;
    }

    public void setDegreeZ(float degreeZ) {
        this.degreeZ = degreeZ;
        invalidate();
    }
}
