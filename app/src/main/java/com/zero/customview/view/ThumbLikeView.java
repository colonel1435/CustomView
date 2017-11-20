package com.zero.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.zero.customview.R;

import org.w3c.dom.ProcessingInstruction;

import uk.co.senab.photoview.log.LoggerDefault;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/11/20 0020 15:09
 */

public class ThumbLikeView extends View {
    private static final String TAG = "ThumbLikeView@wumin";
    private static final int DEFAULT_WIDTH = 192;
    private static final int DEFAULT_HEIGHT = 96;
    private static final float DEFAULT_NUMBER_SIZE = 24;
    private static final int DEFAULT_PADDING = 2;
    private static final int DEFAULT_NUMBER = 0;

    private Context mContext;
    private Paint mLikePaint;
    private Region mLikeRegion;
    private RectF mLikeRect;
    private Path mLikePath;
    private Matrix mMatrix;
    private Paint mNumberPaint;
    private String mNumber = String.valueOf(DEFAULT_NUMBER);
    private int mNumberColor;
    private float mNumberSize;
    private int mLikeColor;
    private float mLikeRadius;
    private int mWidth;
    private int mHeight;
    private float baseX;
    private float baseY;
    private Drawable mThumbDrawable;
    private boolean isTouched = false;
    private boolean isLiked = false;
    private float animDurationTime;

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

        mNumberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNumberPaint.setColor(mNumberColor);
        mNumberPaint.setTextSize(mNumberSize);

        mLikePath = new Path();
        mLikeRegion = new Region();
        mMatrix = new Matrix();
        mThumbDrawable = mContext.getResources().getDrawable(R.mipmap.ic_thumb_up);
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
        baseX = (float)(mWidth / 2);
        baseY = (float)(mHeight / 2);

        mMatrix.reset();
        Region globalRegion = new Region(0, 0, w, h);
        float cx = (mWidth / 2 - getPaddingLeft() - DEFAULT_PADDING) / 2;
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

    private void onClicked() {
        isLiked = isLiked ? false:true;
        startLikeAnimation();
        mClickListener.onLike(isLiked);
    }

    private void startLikeAnimation() {
        if (isLiked) {
            startLightAnimation();
        }
        startScaleAnimation();
    }

    private void startScaleAnimation() {

    }

    private void startLightAnimation() {

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
        Bitmap bmp = ((BitmapDrawable)mThumbDrawable).getBitmap();
        canvas.drawCircle(mLikeRect.centerX(), mLikeRect.centerY(), mLikeRadius, mLikePaint);
        canvas.drawBitmap(bmp, null, dst, null);
    }

    private void drawNumber(Canvas canvas) {
        Paint.FontMetrics fontMetrics = mNumberPaint.getFontMetrics();

        float textWidth = mNumberPaint.measureText(mNumber);
        float baseline_x = baseX + (mWidth / 2 - textWidth) / 2;
        float baseline_y = baseY - (fontMetrics.descent + fontMetrics.ascent) / 2;
        canvas.drawText(mNumber, baseline_x, baseline_y, mNumberPaint);
        canvas.drawLine(0, baseY, mWidth, baseY, mNumberPaint);
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String mNumber) {
        this.mNumber = mNumber;
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
