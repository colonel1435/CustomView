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
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.zero.customview.R;
import com.zero.customview.utils.DisplayUtils;


/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/11/10 0010 14:25
 */

public class CouponView extends LinearLayout {
    private static final String TAG = "CouponView@wumin";
    private final static int DEFAULT_BINDER_HOLE_RADIUS = 10;
    private final static int DEFAULT_CHECK_HOLE_RADIUS = 20;
    private final static int DEFAULT_SEPERATOR_WIDTH = 4;
    private final static float DEFAULT_MARKER_SIZE = 32;
    private final static int DEFAULT_MARKER_ALPHA = 64;
    private Context mContext;
    private Paint mBorderPaint;
    private Paint mLinePaint;
    private Paint mMarkerPaint;
    private Path mBorderPath;
    private float mSeperatorWidth;
    private int mSeperatorColor;
    private int mBorderColor;
    private int mMarkerColor;
    private int mLeftColor;
    private int mRightColor;
    private float mMarkerSize;
    private float mBinderHoleRadius;
    private float mCheckHoleRadius;
    private String mWaterMarker;
    private int mMarkerAlpha;
    private float mWidth;
    private float mHeight;
    private float baseX;
    private float baseY;
    private float baseLeft, baseTop, baseRight, baseBottom;

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
        mWaterMarker = ta.getString(R.styleable.CouponView_coupon_water_marker);
        mMarkerColor = ta.getColor(R.styleable.CouponView_coupon_marker_color, Color.WHITE);
        mMarkerSize = ta.getDimension(R.styleable.CouponView_coupon_marker_size,
                DEFAULT_MARKER_SIZE);
        mLeftColor = ta.getColor(R.styleable.CouponView_coupon_left_color, Color.BLUE);
        mRightColor = ta.getColor(R.styleable.CouponView_coupon_right_color, Color.GREEN);
        ta.recycle();
        mContext = context;
        initView();
    }

    private void initView() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setBackgroundColor(ContextCompat.getColor(mContext, R.color.transparent));
        mMarkerAlpha = DEFAULT_MARKER_ALPHA;

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        baseX = (float) (w * 0.25);
        baseY = (float) (h * 0.5);
        baseLeft = getPaddingLeft();
        baseTop = getPaddingTop();
        baseRight = mWidth - getPaddingRight();
        baseBottom = mHeight - getPaddingBottom();
        if (mBinderHoleRadius == DEFAULT_BINDER_HOLE_RADIUS) {
            mBinderHoleRadius = (float) (mHeight * 0.1);
        }
        if (mCheckHoleRadius == DEFAULT_CHECK_HOLE_RADIUS) {
            mCheckHoleRadius = (float) (mHeight * 0.05);
        }
        if (mMarkerSize == DEFAULT_MARKER_SIZE) {
            mMarkerSize = DisplayUtils.px2sp(mContext, (float) (mWidth * 0.35));
        }

        postInvalidate();
        Log.d(TAG, "onSizeChanged: mWidth -> " + mWidth + " mHeight -> " + mHeight);
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
        Log.d(TAG, "drawBoarder: baseLeft[" + baseLeft + "],baseTop[" + baseTop + "],baseRight[" + baseRight + "],baseBottom["
            + baseBottom + "]");
         mBorderPath.moveTo(baseLeft - mCheckHoleRadius, baseTop);
         RectF ltRect = new RectF(baseLeft - mCheckHoleRadius, baseTop - mCheckHoleRadius,
                                baseLeft + mCheckHoleRadius, baseTop + mCheckHoleRadius);
         mBorderPath.arcTo(ltRect, 0, 90);
         mBorderPath.lineTo(baseLeft, baseY - mBinderHoleRadius);
         RectF lcRect = new RectF(baseLeft - mBinderHoleRadius, baseY - mBinderHoleRadius,
                                    baseLeft + mBinderHoleRadius, baseY + mBinderHoleRadius);
         mBorderPath.arcTo(lcRect, -90, 180);
         mBorderPath.lineTo(baseLeft, baseBottom - mCheckHoleRadius);
         RectF lbRect = new RectF(baseLeft - mCheckHoleRadius, baseBottom - mCheckHoleRadius,
                                    baseLeft + mCheckHoleRadius, baseBottom + mCheckHoleRadius);
         mBorderPath.arcTo(lbRect, -90, 90);
         mBorderPath.lineTo(baseX - mCheckHoleRadius, baseBottom);
         RectF bRect = new RectF(baseX - mCheckHoleRadius, baseBottom - mCheckHoleRadius,
                                baseX + mCheckHoleRadius, baseBottom + mCheckHoleRadius);
         mBorderPath.arcTo(bRect, 180, 180);
         mBorderPath.lineTo(baseRight - mCheckHoleRadius, baseBottom);
         RectF rbRect = new RectF(baseRight - 2 * mCheckHoleRadius, baseBottom - 2 * mCheckHoleRadius,
                                    baseRight, baseBottom);
         mBorderPath.arcTo(rbRect, 90, -90);
         mBorderPath.lineTo(baseRight, baseTop - mCheckHoleRadius);
         RectF rtRect = new RectF(baseRight - 2 * mCheckHoleRadius, baseTop,
                                    baseRight, baseTop + 2 * mCheckHoleRadius);
         mBorderPath.arcTo(rtRect, 0, -90);
         mBorderPath.lineTo(baseX + mCheckHoleRadius, baseTop);
         RectF tRect = new RectF(baseX - mCheckHoleRadius, baseTop - mCheckHoleRadius,
                                    baseX + mCheckHoleRadius, baseTop + mCheckHoleRadius);
         mBorderPath.arcTo(tRect, 0, 180);
         mBorderPath.close();

        Shader shader = new LinearGradient(baseLeft, baseTop, baseRight, baseBottom,
                mLeftColor, mRightColor, Shader.TileMode.CLAMP);
        mBorderPaint.setShader(shader);
        canvas.drawPath(mBorderPath, mBorderPaint);
        canvas.restore();
    }

    private void drawSeperator(Canvas canvas) {
        canvas.save();
        canvas.drawLine(baseX, baseTop + mCheckHoleRadius,
                        baseX, baseBottom - mCheckHoleRadius, mLinePaint);
        canvas.restore();
    }

    private void drawWaterMarker(Canvas canvas) {
        if (mWaterMarker != null && !"".equals(mWaterMarker)) {
            canvas.save();
            Paint.FontMetrics fontMetrics = mMarkerPaint.getFontMetrics();
            float baseline_x = baseRight;
            float baseline_y = baseTop + mMarkerSize;
            mMarkerPaint.setFakeBoldText(true);
            mMarkerPaint.setTextSkewX(-0.3f);
            mMarkerPaint.setTextSize(mMarkerSize);
            mMarkerPaint.setTextAlign(Paint.Align.RIGHT);
            mMarkerPaint.setAlpha(mMarkerAlpha);
            canvas.drawText(mWaterMarker, baseline_x, baseline_y, mMarkerPaint);
            canvas.restore();
        }
    }
}
