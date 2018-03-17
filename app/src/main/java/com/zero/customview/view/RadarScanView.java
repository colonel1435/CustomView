package com.zero.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.zero.customview.R;
import com.zero.customview.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Description
 *
 * @author : Mr.wuming
 * @email : fusu1435@163.com
 * @date : 2018/3/16 14:45
 */

public class RadarScanView extends View {
    private static final int DEFAULT_WIDTH = 256;
    private static final int DEFAULT_HEIGHT = 256;
    private static final int DEFAULT_LINE_SIZE = 1;
    private static final int DEFAULT_CIRCLE_MAX = 4;
    private static final int DEFAULT_DOT_SIZE = 4;
    private static final int DEFAULT_CROSSLINE_ANGLE = 45;
    private static final int DEFAULT_CIRCLE_ANGLE = 360;
    private static final int DEFAULT_PADDING = 8;
    public static final int REFRESH_DELAY_MILLIS = 100;
    public static final int DEFAULT_SCAN_SPEED = 8;

    private Context mContext;
    private int mLineColor;
    private int mRadarColor;
    private float mLineSize;
    private float mDotSize;
    private int mDotColor;
    private boolean isScanning;

    private int mWidth;
    private int mHeight;
    private int centerX;
    private int centerY;
    private float mRadius;
    private float mPadding;

    private Paint mOutlinePaint;
    private Paint mRadarPaint;
    private Paint mPointPaint;
    private Shader scanShader;
    private Matrix matrix;
    private int mScanSpeed;
    private int scanAngle = 0;
    private List<Point> mPoints;
    private boolean mAutoPoint;

    public RadarScanView(Context context) {
        this(context, null);
    }

    public RadarScanView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarScanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RadarScanView);
        try {
            mLineSize = ta.getDimension(R.styleable.RadarScanView_radar_line_size,
                    DisplayUtils.dip2px(context, DEFAULT_LINE_SIZE));
            mLineColor = ta.getColor(R.styleable.RadarScanView_radar_line_color,
                    Color.WHITE);
            mRadarColor = ta.getColor(R.styleable.RadarScanView_radar_color,
                    Color.BLUE);
            mAutoPoint = ta.getBoolean(R.styleable.RadarScanView_radar_auto_point, false);
            mScanSpeed = ta.getInt(R.styleable.RadarScanView_radar_scan_speed, DEFAULT_SCAN_SPEED);
            initViews();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != ta) {
                ta.recycle();
            }
        }
    }

    private void initViews() {
        mOutlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOutlinePaint.setStyle(Paint.Style.STROKE);
        mOutlinePaint.setStrokeWidth(mLineSize);
        mOutlinePaint.setDither(true);
        mOutlinePaint.setColor(mLineColor);

        mRadarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRadarPaint.setDither(true);
        mRadarPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setDither(true);
        mPointPaint.setColor(mDotColor);

        isScanning = true;
        mPadding = DisplayUtils.dip2px(mContext, DEFAULT_PADDING);
        matrix = new Matrix();

        mDotSize = DisplayUtils.dip2px(mContext, DEFAULT_DOT_SIZE);
        mPoints = new ArrayList<>();
        if (isScanning) {
            post(run);
        }
    }

    private int measureSize(int measureSpec, int defaultVal) {
        int defaultSize = defaultVal;
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        centerX = w / 2 + getPaddingLeft() - getPaddingRight();
        centerY = h / 2 + getPaddingTop() - getPaddingBottom();
        mRadius = Math.min((w - 2 * mPadding)/2, (h - 2 * mPadding)/2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureSize(widthMeasureSpec,
                                        DisplayUtils.dip2px(mContext, DEFAULT_WIDTH)),
                            measureSize(heightMeasureSpec,
                                        DisplayUtils.dip2px(mContext, DEFAULT_HEIGHT)));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawOutline(canvas);
        drawPoint(canvas);
        drawSector(canvas);
    }

    private void drawOutline(Canvas canvas) {
        float space = mRadius / DEFAULT_CIRCLE_MAX;
        canvas.save();
        canvas.translate(centerX, centerY);
        /**
         * Draw outline circle
         */
        for(int i = 1; i <= DEFAULT_CIRCLE_MAX; i++) {
            canvas.drawCircle(0, 0, space * i, mOutlinePaint);
        }

        /**
         * Draw outline crossline
         */
        double angle;
        for (int i = 0; i < DEFAULT_CIRCLE_MAX; i++) {
            angle = (2 * Math.PI)/DEFAULT_CIRCLE_ANGLE * (i * DEFAULT_CROSSLINE_ANGLE);
            canvas.drawLine((float) (- mRadius * Math.cos(angle)),
                            (float) (mRadius * Math.sin(angle)),
                            (float) (mRadius * Math.cos(angle)),
                            (float) (- mRadius * Math.sin(angle))
                            , mOutlinePaint);
        }
        canvas.restore();
    }

    private void drawSector(Canvas canvas) {
        canvas.save();
        scanShader = new SweepGradient(centerX, centerY,
                new int[]{Color.TRANSPARENT, mRadarColor}, null);
        mRadarPaint.setShader(scanShader);
        canvas.concat(matrix);
        canvas.drawCircle(centerX, centerX, mRadius + mLineSize, mRadarPaint);
        canvas.restore();
    }

    private void drawPoint(Canvas canvas) {
        if (mPoints.size() > 0) {
            for (Point point : mPoints) {
                mPointPaint.setColor(Color.RED);
                canvas.drawCircle(point.x, point.y, mDotSize, mPointPaint);
            }
        }
    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            if (mAutoPoint) {
                addPoint(true);
            }
            scanAngle = (scanAngle + mScanSpeed) % 360;
            matrix.postRotate(mScanSpeed, centerX, centerY);
            invalidate();
            postDelayed(run, REFRESH_DELAY_MILLIS);
        }
    };

    public void addPoint(boolean add) {
        if (add) {
            /**
             * Create random point in radar circle with random radius & random angle
             */
            Random random = new Random();
            float radius = random.nextInt((int)(mRadius - mDotSize/2));
            double angle = (2 * Math.PI / DEFAULT_CIRCLE_ANGLE) * random.nextInt(DEFAULT_CIRCLE_ANGLE);
            Point point = new Point(centerX + (int)(radius * Math.cos(angle)),
                                    centerY + (int)(radius * Math.sin(angle)));
            mPoints.add(point);
            invalidate();
        }
    }
}
