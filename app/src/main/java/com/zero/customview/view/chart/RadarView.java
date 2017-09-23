package com.zero.customview.view.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Scroller;

import com.zero.customview.R;
import com.zero.customview.utils.DisplayUtils;
import com.zero.customview.utils.RotateUtils;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/22 0022 15:29
 */

public class RadarView extends View {
    private final String TAG = this.getClass().getSimpleName()+"@wumin";
    private Context mContext;
    private int edgeNum = 6;
    private float mAngle = (float)(Math.PI*2/edgeNum);
    private float mRadius;
    private float centerX;
    private float centerY;
    private PointF mCenterPoint;
    private String[] mLabels = {"A", "B", "C", "D", "E", "F"};
    private double[] mDatas = {30, 90, 70, 60, 50, 90};
    private float maxVal = 100;
    private Paint radarPaint;
    private Paint labelPaint;
    private Paint coverPaint;
    private float mLableOffset = 0;
    private boolean mEnableRotate = true;
    private double mRotateAngle = 0;
    private GestureDetector mGestureDetector;
    private Scroller mScroller;
    private float mFlingPoint;
    private double mRotateOrientation;
    private double mPerimeter;

    public RadarView(Context context) {
        this(context, null);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        radarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        radarPaint.setColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        radarPaint.setStrokeWidth(2);
        radarPaint.setStyle(Paint.Style.STROKE);

        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        labelPaint.setStyle(Paint.Style.STROKE);
        labelPaint.setTextSize(DisplayUtils.dip2px(mContext, 12));
        coverPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        coverPaint.setColor(ContextCompat.getColor(mContext, R.color.colorLightBlue));

        mGestureDetector = new GestureDetector(mContext,new DefaultGestureDectector());
        mGestureDetector.setIsLongpressEnabled(false);

        mScroller = new Scroller(mContext);
    }

    private int measureSize(int measureSpec) {
        int defaultSize = 480;
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            // Measure the text
            result = defaultSize + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureSize(widthMeasureSpec), measureSize(heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mRadius = Math.min(w, h)/2*0.9f;
        mPerimeter = 2 * Math.PI * mRadius;
        centerX = w/2;
        centerY = h /2;
        mCenterPoint = new PointF(centerX, centerY);
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawPolygen(canvas);
        drawCrossLine(canvas);
        drawOutLabel(canvas);
        drawValRegon(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEnableRotate) {
            return mGestureDetector.onTouchEvent(event);
        } else {
            return super.onTouchEvent(event);
        }
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            int max = Math.max(Math.abs(x), Math.abs(y));
            double rotateDis = RotateUtils.CIRCLE_ANGLE * (Math.abs(max - mFlingPoint) / mPerimeter);
            double rotate = mRotateAngle;
            if (mRotateOrientation > 0) {
                rotate += rotateDis;
            } else if (mRotateOrientation < 0) {
                rotate -= rotateDis;
            }
            mRotateAngle = RotateUtils.getNormalizedAngle(rotate);
            invalidate();
            mFlingPoint = max;
            invalidate();
        }
    }

    private void drawPolygen(Canvas canvas) {
        Path path = new Path();
        float space = mRadius / (edgeNum - 1);
        for (int i = 1; i < edgeNum; i++) {
            float curRadius = space * i;
            path.reset();
            for(int j = 0; j < edgeNum; j++) {
                float x = (float)(centerX + curRadius * Math.cos(mAngle*j + mRotateAngle));
                float y = (float)(centerY + curRadius * Math.sin(mAngle*j + mRotateAngle));
                if (j == 0) {
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }
            path.close();
            canvas.drawPath(path, radarPaint);
        }
    }

    private void drawCrossLine(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < edgeNum; i++) {
            path.reset();
            path.moveTo(centerX, centerY);
            float x = (float)(centerX + mRadius * Math.cos(i * mAngle + mRotateAngle));
            float y = (float)(centerY + mRadius * Math.sin(i * mAngle + mRotateAngle));
            path.lineTo(x, y);
            canvas.drawPath(path, radarPaint);
        }
    }

    private void drawOutLabel(Canvas canvas) {
        Paint.FontMetrics fontMetrics = labelPaint.getFontMetrics();
        float labelHeight = fontMetrics.descent - fontMetrics.ascent;
        for (int i = 0; i < edgeNum; i++) {
            String label = mLabels[i];
            float labelWidth = labelPaint.measureText(label);
            float x = (float)(centerX + (mRadius+mLableOffset+labelHeight/2)
                    * Math.cos(i * mAngle + mRotateAngle));
            float y = (float)(centerY + (mRadius+mLableOffset+labelHeight/2)
                    * Math.sin(i * mAngle + mRotateAngle));
            canvas.drawText(mLabels[i], x - labelWidth, y+labelHeight/4, labelPaint);
        }
    }

    private void drawValRegon(Canvas canvas) {
        Path path = new Path();
        coverPaint.setAlpha(255);
        for (int i = 0; i < edgeNum; i++) {
            double rate = mDatas[i]/maxVal;
            float x = (float)(centerX + mRadius*rate*Math.cos(i*mAngle + mRotateAngle));
            float y = (float)(centerY + mRadius*rate*Math.sin(i*mAngle + mRotateAngle));
            if (i ==0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
            canvas.drawCircle(x, y, 5, coverPaint);
        }
        path.close();
        coverPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, coverPaint);
        coverPaint.setAlpha(127);
        coverPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, coverPaint);

    }

    private class DefaultGestureDectector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            double rotate = mRotateAngle;
            double dis = RotateUtils.getRotateAngle(new PointF(e2.getX() - distanceX, e2.getY() - distanceY)
                    , new PointF(e2.getX(), e2.getY()), mCenterPoint);
            rotate += dis;

            mRotateAngle = RotateUtils.getNormalizedAngle(rotate);
            invalidate();
            mRotateOrientation = dis;
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                mFlingPoint = e2.getX();
                mScroller.fling((int) e2.getX(), 0, (int) velocityX, 0, (int) (-mPerimeter + e2.getX()),
                        (int) (mPerimeter + e2.getX()), 0, 0);
            } else if (Math.abs(velocityY) > Math.abs(velocityX)) {
                mFlingPoint = e2.getY();
                mScroller.fling(0, (int) e2.getY(), 0, (int) velocityY, 0, 0, (int) (-mPerimeter + e2.getY()),
                        (int) (mPerimeter + e2.getY()));
            }
            invalidate();

            return super.onFling(e1, e2, velocityX, velocityY);
        }

    }
}
