package com.zero.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.icu.text.TimeZoneFormat;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zero.customview.R;
import com.zero.customview.utils.DisplayUtils;

import java.util.zip.CheckedOutputStream;


/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/11/10 0010 14:25
 */

public class CouponView extends ViewGroup {
    private static final String TAG = "CouponView@wumin";
    private final static int DEFAULT_BINDER_HOLE_RADIUS = 10;
    private final static int DEFAULT_CHECK_HOLE_RADIUS = 20;
    private final static int DEFAULT_SEPERATOR_WIDTH = 4;
    private final static float DEFAULT_MARKER_SIZE = 32;
    private Context mContext;
    private Paint mBorderPaint;
    private Paint mLinePaint;
    private Paint mMarkerPaint;
    private Path mBorderPath;
    private float mSeperatorWidth;
    private int mSeperatorColor;
    private int mBorderColor;
    private int mMarkerColor;
    private float mMarkerSize;
    private float mBinderHoleRadius;
    private float mCheckHoleRadius;
    private String mWaterMarker;
    private float mWidth;
    private float mHeight;
    private float baseX;
    private float baseY;

    public CouponView(Context context) {
        this(context, null);
    }

    public CouponView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CouponView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CouponView);
        mSeperatorWidth = ta.getDimensionPixelSize(R.styleable.CouponView_coupon_seperaotr_width,
                DEFAULT_SEPERATOR_WIDTH);
        mSeperatorColor = ta.getColor(R.styleable.CouponView_coupon_seperator_color, Color.WHITE);
        mBorderColor = ta.getColor(R.styleable.CouponView_coupon_border_color, Color.WHITE);
        mBinderHoleRadius = ta.getDimensionPixelSize(R.styleable.CouponView_coupon_binder_radius,
                DEFAULT_BINDER_HOLE_RADIUS);
        mCheckHoleRadius = ta.getDimensionPixelSize(R.styleable.CouponView_coupon_check_radius,
                DEFAULT_CHECK_HOLE_RADIUS);
        mWaterMarker = ta.getString(R.styleable.CouponView_coupan_water_marker);
        mMarkerColor = ta.getColor(R.styleable.CouponView_coupan_marker_color, Color.WHITE);
        mMarkerSize = ta.getDimension(R.styleable.CouponView_coupon_marker_size,
                DEFAULT_MARKER_SIZE);
        ta.recycle();
        mContext = context;
        initView();
    }

    private void initView() {
        setBackgroundColor(ContextCompat.getColor(mContext, R.color.transparent));
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStrokeWidth(10);
        mBorderPaint.setColor(mBorderColor);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStrokeWidth(mSeperatorWidth);
        mLinePaint.setColor(mSeperatorColor);
        PathEffect pathEffect = new DashPathEffect(new float[]{10, 5}, 10);
        mLinePaint.setPathEffect(pathEffect);

        mMarkerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMarkerPaint.setStyle(Paint.Style.FILL);
        mMarkerPaint.setColor(mMarkerColor);

        mBorderPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for(int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        baseX = (float) (w * 0.25);
        baseY = (float) (h * 0.5);
        if (mBinderHoleRadius == DEFAULT_BINDER_HOLE_RADIUS) {
            mBinderHoleRadius = (float) (mHeight * 0.1);
        }
        if (mCheckHoleRadius == DEFAULT_CHECK_HOLE_RADIUS) {
            mCheckHoleRadius = (float) (mHeight * 0.05);
        }
        if (mMarkerSize == DEFAULT_MARKER_SIZE) {
            mMarkerSize = (float) DisplayUtils.px2sp(mContext, (float)(mHeight * 0.65));
        }
        Log.d(TAG, "onSizeChanged: mWidth -> " + mWidth + " mHeight -> " + mHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoarder(canvas);
        drawSeperator(canvas);
        drawWaterMarker(canvas);
    }

    private void drawBoarder(Canvas canvas) {
        canvas.save();
         float left = getLeft();
         float top = getTop();
         float right = getRight();
         float bottom = getBottom();
        Log.d(TAG, "drawBoarder: left[" + left + "],top[" + top + "],right[" + right + "],bottom["
            + bottom + "]");
         mBorderPath.moveTo(left - mCheckHoleRadius, top);
         RectF ltRect = new RectF(left - mCheckHoleRadius, top - mCheckHoleRadius,
                                left + mCheckHoleRadius, top + mCheckHoleRadius);
         mBorderPath.arcTo(ltRect, 0, 90);
         mBorderPath.lineTo(left, baseY - mBinderHoleRadius);
         RectF lcRect = new RectF(left - mBinderHoleRadius, baseY - mBinderHoleRadius,
                                    left + mBinderHoleRadius, baseY + mBinderHoleRadius);
         mBorderPath.arcTo(lcRect, -90, 180);
         mBorderPath.lineTo(left, bottom - mCheckHoleRadius);
         RectF lbRect = new RectF(left - mCheckHoleRadius, bottom - mCheckHoleRadius,
                                    left + mCheckHoleRadius, bottom + mCheckHoleRadius);
         mBorderPath.arcTo(lbRect, -90, 90);
         mBorderPath.lineTo(baseX - mCheckHoleRadius, bottom);
         RectF bRect = new RectF(baseX - mCheckHoleRadius, bottom - mCheckHoleRadius,
                                baseX + mCheckHoleRadius, bottom + mCheckHoleRadius);
         mBorderPath.arcTo(bRect, 180, 180);
         mBorderPath.lineTo(right - mCheckHoleRadius, bottom);
         RectF rbRect = new RectF(right - 2 * mCheckHoleRadius, bottom - 2 * mCheckHoleRadius,
                                    right, bottom);
         mBorderPath.arcTo(rbRect, 90, -90);
         mBorderPath.lineTo(right, top - mCheckHoleRadius);
         RectF rtRect = new RectF(right - 2 * mCheckHoleRadius, top,
                                    right, top + 2 * mCheckHoleRadius);
         mBorderPath.arcTo(rtRect, 0, -90);
         mBorderPath.lineTo(baseX + mCheckHoleRadius, top);
         RectF tRect = new RectF(baseX - mCheckHoleRadius, top - mCheckHoleRadius,
                                    baseX + mCheckHoleRadius, top + mCheckHoleRadius);
         mBorderPath.arcTo(tRect, 0, 180);
         mBorderPath.close();

        Shader shader = new LinearGradient(left, top, right, bottom,
                Color.parseColor("#76c1e9"), Color.parseColor("#00c6b5"),
                Shader.TileMode.CLAMP);
        mBorderPaint.setShader(shader);
        canvas.drawPath(mBorderPath, mBorderPaint);
        canvas.restore();
    }

    private void drawSeperator(Canvas canvas) {
        canvas.save();
        int top = getTop();
        int bottom = getBottom();
        canvas.drawLine(baseX, top + mCheckHoleRadius,
                        baseX, bottom - mCheckHoleRadius, mLinePaint);
        canvas.restore();
    }

    private void drawWaterMarker(Canvas canvas) {
        if (mWaterMarker != null && !"".equals(mWaterMarker)) {
            canvas.save();
            int right = getRight();
            Paint.FontMetrics fontMetrics = mMarkerPaint.getFontMetrics();
            float baseline_x = right;
            float baseline_y = baseY - (fontMetrics.descent - fontMetrics.ascent)/2;
            mMarkerPaint.setFakeBoldText(true);
            mMarkerPaint.setTextSize(mMarkerSize);
            mMarkerPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(mWaterMarker, baseline_x, baseline_y, mMarkerPaint);
            canvas.restore();
        }
    }
}
