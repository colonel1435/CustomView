package com.zero.customview.view.danmaku;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import com.zero.customview.R;

/**
 * 圆形的Drawable
 * Created by feiyang on 16/2/18.
 */
public class CircleDrawable extends Drawable {

    private Paint   mPaint;
    private Bitmap  mBitmap;
    private Bitmap  mBitmapHeart;
    private boolean mHasHeart;

    private static final int BLACKGROUDE_ADD_SIZE = 4;//背景比图片多出来的部分

    public CircleDrawable(Bitmap bitmap) {
        mBitmap = bitmap;
        BitmapShader bitmapShader = new BitmapShader(bitmap,
                Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setShader(bitmapShader);
    }

    /**
     * 右下角包含一个‘心’的圆形drawable
     *
     * @param context
     * @param bitmap
     * @param hasHeart
     */
    public CircleDrawable(Context context, Bitmap bitmap, boolean hasHeart) {
        this(bitmap);
        mHasHeart = hasHeart;
        if (hasHeart) {
            setBitmapHeart(context);
        }
    }

    private void setBitmapHeart(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_liked);
        if (bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.postScale(0.95f, 0.95f);
            mBitmapHeart = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
    }

    @Override
    public void draw(Canvas canvas) {
        int width = getIntrinsicWidth();
        int height = getIntrinsicHeight();
        if (mHasHeart && mBitmapHeart != null) {
            Paint backgroundPaint = new Paint();
            backgroundPaint.setAntiAlias(true);
            backgroundPaint.setColor(Color.BLACK);
            canvas.drawCircle(width / 2 + BLACKGROUDE_ADD_SIZE, height / 2 + BLACKGROUDE_ADD_SIZE,
                    width / 2 + BLACKGROUDE_ADD_SIZE, backgroundPaint);

            canvas.translate(BLACKGROUDE_ADD_SIZE, BLACKGROUDE_ADD_SIZE);
            canvas.drawCircle(width / 2, height / 2, width / 2, mPaint);

            Rect srcRect = new Rect(0, 0, mBitmapHeart.getWidth(), mBitmapHeart.getHeight());
            Rect desRect = new Rect(width - mBitmapHeart.getWidth() + BLACKGROUDE_ADD_SIZE,
                    height - mBitmapHeart.getHeight() + BLACKGROUDE_ADD_SIZE,
                    width + BLACKGROUDE_ADD_SIZE,
                    height + BLACKGROUDE_ADD_SIZE);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setFilterBitmap(true);
            paint.setDither(true);
            canvas.drawBitmap(mBitmapHeart, srcRect, desRect, paint);
        } else {
            canvas.drawCircle(width / 2, height / 2, width / 2, mPaint);
        }
    }

    @Override
    public int getIntrinsicWidth() {
        return mBitmap.getWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mBitmap.getHeight();
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}

