package com.zero.customview.view;

import android.content.Context;
import android.content.pm.ProviderInfo;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/23 0023 11:06
 */

public class CircleMenuView extends View {
    enum  POSITION {NONE, TOP, BOTTOM, LEFT, RIGHT, CENTER};

    private Paint mPaint;
    private Matrix mMatrix;

    private Path centerPath;
    private Path topPath;
    private Path bottomPath;
    private Path leftPath;
    private Path rightPath;

    private Region centerRegion;
    private Region topRegion;
    private Region bottomRegion;
    private Region leftRegion;
    private Region rightRegion;

    private int touchPosition = -1;
    private int currentPosition = -1;

    private int regionColor = 0xFF4699A3;
    private int touchColor = 0XFFE67B63;

    private int mWidth;
    private int mHeight;

    private int down_x;
    private int down_y;
    private onMenuClickListener mClickListener;
    public CircleMenuView(Context context) {
        this(context, null);
    }

    public CircleMenuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(regionColor);
        mMatrix = new Matrix();

        topPath = new Path();
        bottomPath = new Path();
        leftPath = new Path();
        rightPath = new Path();
        centerPath = new Path();

        topRegion = new Region();
        bottomRegion = new Region();
        leftRegion = new Region();
        rightRegion = new Region();
        centerRegion = new Region();
    }

    public void setMenuClickListener(onMenuClickListener clickListener) {
        this.mClickListener = clickListener;
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
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        mMatrix.reset();
        Region globalRegion = new Region(-w, -h, w, h);
        int minWidth = Math.min(w, h);
        minWidth *= 0.8;
        int outRadius = minWidth / 2;
        RectF outCircle = new RectF(-outRadius, -outRadius, outRadius, outRadius);
        int innerRadius = minWidth / 4;
        RectF innerCircle = new RectF(-innerRadius, -innerRadius, innerRadius, innerRadius);

        float outAngle = 84;
        float innerAngle = -80;

        centerPath.addCircle(0, 0, 0.2f * minWidth, Path.Direction.CW);
        centerRegion.setPath(centerPath, globalRegion);

        rightPath.addArc(outCircle, -40, outAngle);
        rightPath.arcTo(innerCircle, 40, innerAngle);
        rightPath.close();
        rightRegion.setPath(rightPath, globalRegion);

        bottomPath.addArc(outCircle, 50, outAngle);
        bottomPath.arcTo(innerCircle, 130, innerAngle);
        bottomPath.close();
        bottomRegion.setPath(bottomPath, globalRegion);

        leftPath.addArc(outCircle, 140, outAngle);
        leftPath.arcTo(innerCircle, 220, innerAngle);
        leftPath.close();
        leftRegion.setPath(leftPath, globalRegion);

        topPath.addArc(outCircle, 230, outAngle);
        topPath.arcTo(innerCircle, 310, innerAngle);
        topPath.close();
        topRegion.setPath(topPath, globalRegion);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mWidth / 2, mHeight / 2);

        if (mMatrix.isIdentity()) {
            canvas.getMatrix().invert(mMatrix);
        }

        canvas.drawPath(topPath, mPaint);
        canvas.drawPath(bottomPath, mPaint);
        canvas.drawPath(leftPath, mPaint);
        canvas.drawPath(rightPath, mPaint);
        canvas.drawPath(centerPath, mPaint);

        mPaint.setColor(touchColor);
        if (touchPosition == POSITION.TOP.ordinal()) {
            canvas.drawPath(topPath, mPaint);
        }else if(touchPosition == POSITION.BOTTOM.ordinal()) {
            canvas.drawPath(bottomPath, mPaint);
        } else if (touchPosition == POSITION.LEFT.ordinal()) {
            canvas.drawPath(leftPath, mPaint);
        } else if (touchPosition == POSITION.RIGHT.ordinal()) {
            canvas.drawPath(rightPath, mPaint);
        } else if (touchPosition == POSITION.CENTER.ordinal()) {
            canvas.drawPath(centerPath, mPaint);
        }
        mPaint.setColor(regionColor);

//        canvas.drawCircle(down_x, down_y, 20, mPaint);
    }

    private int getTouchPosition(int x, int y) {
        int position = POSITION.NONE.ordinal();
        if (centerRegion.contains(x, y)) {
            position = POSITION.CENTER.ordinal();
        } else if (topRegion.contains(x, y)) {
            position = POSITION.TOP.ordinal();
        } else if (bottomRegion.contains(x, y)) {
            position = POSITION.BOTTOM.ordinal();
        } else if (leftRegion.contains(x, y)) {
            position = POSITION.LEFT.ordinal();
        } else if (rightRegion.contains(x, y)) {
            position = POSITION.RIGHT.ordinal();
        }

        return position;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float[] pts = new float[2];
        pts[0] = event.getRawX();
        pts[1] = event.getRawY();
        mMatrix.mapPoints(pts);

        int x = (int) pts[0];
        int y = (int) pts[1];
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                touchPosition = getTouchPosition(x, y);
                currentPosition = touchPosition;
                break;
            case MotionEvent.ACTION_MOVE:
                currentPosition = getTouchPosition(x, y);
                break;
            case MotionEvent.ACTION_UP:
                currentPosition = getTouchPosition(x, y);
                if (currentPosition == touchPosition
                        && currentPosition != -1
                        && mClickListener != null) {
                    if (currentPosition == POSITION.CENTER.ordinal()) {
                        mClickListener.onCenterClick();
                    } else if (currentPosition == POSITION.TOP.ordinal()) {
                        mClickListener.onTopClick();
                    } else if (currentPosition == POSITION.BOTTOM.ordinal()) {
                        mClickListener.onBottomClick();
                    } else if (currentPosition == POSITION.LEFT.ordinal()) {
                        mClickListener.onLeftClick();
                    } else if (currentPosition == POSITION.RIGHT.ordinal()) {
                        mClickListener.onRightClick();
                    }
                }
                currentPosition = touchPosition = -1;
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }

        down_x = x;
        down_y = y;
        invalidate();
        return true;
    }

    public interface onMenuClickListener {
        void onCenterClick();
        void onTopClick();
        void onBottomClick();
        void onLeftClick();
        void onRightClick();
    }
}
