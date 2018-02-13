package com.zero.customview.view.captcha;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

import com.zero.customview.R;
import com.zero.customview.utils.DisplayUtils;

/**
 * Description
 *
 * @author : Mr.wuming
 * @email : fusu1435@163.com
 * @date : 2018/2/7 0007 16:13
 */

public class CaptchaImageView extends AppCompatImageView{
    private final static String TAG = CaptchaImageView.class.getSimpleName()+"@wumin";
    private final static int EVENT_INVALID = -1;
    private final static int EVENT_DOWN = 0;
    private final static int EVENT_MOVE = 1;
    private final static int EVENT_UP = 2;
    private final static int EVENT_ACCESS = 3;
    private final static int EVENT_DENY = 4;
    private final static int DEFAULT_BLOCK_SIZE = 72;

    private Context mContext;
    private int mStatus;
    private Position mPosition;
    private int mBlockSize;
    private int currentPosition;
    private CaptchaImageCall captchaImageCall;
    private Paint mPaint;
    private Paint mShaderPaint;
    private Bitmap mBitmap;
    private Bitmap mTargetBitmap;
    public CaptchaImageView(Context context) {
        this(context, null);
    }

    public CaptchaImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptchaImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CaptchaView);
        ta.recycle();
        mContext = context;
        initViews();
    }

    private void initViews() {
        mStatus = EVENT_INVALID;
        mBlockSize = DisplayUtils.dip2px(mContext, DEFAULT_BLOCK_SIZE);
        currentPosition = 0;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mShaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShaderPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mShaderPaint.setColor(Color.BLACK);
        mShaderPaint.setAlpha(128);
        if (null == captchaImageCall) {
            captchaImageCall = new DefaultCaptchaImage(mContext);
        }

        if (null != getDrawable()) {
            mBitmap = ((BitmapDrawable)getDrawable()).getBitmap();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw: widht ->" + getWidth() + " height ->" + getHeight());
        if (null == mPosition) {
            mPosition = captchaImageCall.onPosition(mBlockSize, getWidth(), getHeight());
        }

        Path blockPath = captchaImageCall.onShape(mBlockSize);
        if (EVENT_ACCESS != mStatus) {
            drawCutoutRect(canvas, blockPath);
        }

        if (EVENT_MOVE == mStatus || EVENT_INVALID == mStatus) {
            drawBlockRect(canvas, blockPath);
        }

    }

    private void drawCutoutRect(Canvas canvas, Path blockPath) {
        blockPath.offset(mPosition.getLeft(), mPosition.getTop());
        canvas.drawPath(blockPath, mShaderPaint);
    }

    private void drawBlockRect(Canvas canvas, Path blockPath) {
        if (null == mTargetBitmap) {
            mTargetBitmap = createSrcImage(mBitmap, getWidth(), getHeight());
        }
        canvas.save();
        canvas.translate(-mPosition.getLeft(), 0);
        canvas.clipPath(blockPath);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(mTargetBitmap, currentPosition, 0, mPaint);
        mPaint.setXfermode(null);
        captchaImageCall.onDecoration(canvas, blockPath);
        canvas.restore();
    }

    public static Bitmap createSrcImage(Bitmap src, int newWidth,
                                   int newHeight) {
        int width = src.getWidth();
        int height = src.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap target = Bitmap.createBitmap(src, 0, 0, width,
                height, matrix, true);
        return target;
    }

    public void setCurrentPosition(float index) {
        currentPosition = (int)(index * getWidth());
        invalidate();
    }
}
