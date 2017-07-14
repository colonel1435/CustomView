package com.zero.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.zero.customview.R;
import com.zero.customview.utils.DisplayUtils;


/**
 * Created by Administrator on 2017/2/15.
 */

public class SixangleImageView extends ImageView {
    private final String TAG = this.getClass().getSimpleName() + "@wumin";
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 1;
    private static final int INNER_CIRCLE_PADDING = 40;
    private int mWidth;
    private int mHeight;
    private int centreX;//中心点
    private int centreY;//中心点
    private int mLength;
    private int innerCircleWidth;
    private Paint paint;
    private Paint paintcontent;
    private Canvas mCanvas;
    private Bitmap bitmapContent = null;
    private String titleText;
    private int bgColor = Color.parseColor("#34D4F3");
    private int sideLength = 64;
    private int titleSize = 8;
    private int textMarginBottom = 2;
    private int innderCircleSize = 32;
    private int logoImageId = R.mipmap.ic_launcher;

    public SixangleImageView(Context context) {
        super(context);
    }
    public SixangleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray type=context.obtainStyledAttributes(attrs, R.styleable.sixangleImageView);
        titleText= type.getString(R.styleable.sixangleImageView_titleText);
        titleSize = type.getDimensionPixelSize(R.styleable.sixangleImageView_titleSize, titleSize);
        logoImageId = type.getResourceId(R.styleable.sixangleImageView_logoImage, logoImageId);
        textMarginBottom = type.getInt(R.styleable.sixangleImageView_textMarginBottom, textMarginBottom);
        bgColor = type.getColor(R.styleable.sixangleImageView_bgColor, bgColor);
        sideLength= type.getInt(R.styleable.sixangleImageView_sideLength, sideLength);
        type.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int width;
        int padding = getPaddingLeft() + getPaddingRight();
        if (MeasureSpec.EXACTLY == widthMode) {
            width = widthSize;
        } else {
            width = padding + sideLength;
            if (widthMode == MeasureSpec.AT_MOST) {
                width = Math.min(width, widthSize);
            }
        }
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;

        mWidth = getWidth();
        mHeight = getHeight();
        Log.d(TAG, "width -> " + mWidth + " height -> " + mHeight);
        // 计算中心点
        centreX = mWidth / 2;
        centreY = mHeight / 2;
        mLength = mWidth / 2;
        double radian30 = 30 * Math.PI / 180;
        float a = (float) (mLength * Math.sin(radian30));
        float b = (float) (mLength * Math.cos(radian30));
        float c = (mHeight - 2 * b) / 2;
        if (null == paint) {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);//FILL
            paint.setColor(bgColor);
            paint.setAlpha(200);
        }
        Path path = new Path();
        path.moveTo(getWidth(), getHeight() / 2);
        path.lineTo(getWidth() - a, getHeight() - c);
        path.lineTo(getWidth() - a - mLength, getHeight() - c);
        path.lineTo(0, getHeight() / 2);
        path.lineTo(a, c);
        path.lineTo(getWidth() - a, c);
        path.close();
        canvas.drawPath(path, paint);

        paintcontent = new Paint();
        paintcontent.setColor(Color.WHITE);
        paintcontent.setTextAlign(Paint.Align.CENTER);
        paintcontent.setTextSize(titleSize);

        canvas.drawText(titleText, this.getWidth() / 2, this.getHeight() - titleSize - textMarginBottom, paintcontent);

        innderCircleSize = getHeight() - textMarginBottom*2 - DisplayUtils.sp2px(getContext(), titleSize) - INNER_CIRCLE_PADDING;
        Log.d(TAG, "innder Circle size -> " + innderCircleSize);
        if(bitmapContent == null)  {
            Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), logoImageId);
            Matrix matrix=new Matrix();
            matrix.postTranslate(this.getWidth() / 2 - mBitmap.getWidth()/ 2, this.getHeight() / 2 - mBitmap.getHeight() / 2-5);
            canvas.drawBitmap(mBitmap, matrix, paint);
        } else {
            Matrix matrix = new Matrix();
            matrix.postTranslate(this.getWidth() / 2 - bitmapContent.getWidth() / 2, this.getHeight() / 2 - bitmapContent.getHeight() / 2 - 5);
            canvas.drawBitmap(bitmapContent, matrix, paint);
        }

    }


    public void setBitmap(Bitmap bmp) {
        Bitmap bitmap = getBitmapFromDrawable(new RoundImageDrawable(bmp, innderCircleSize));
        bitmapContent = bitmap;
        invalidate();
    }
//    public void setDrawable(Bitmap bmp) {
//        Bitmap bitmap = getBitmapFromDrawable(new RoundImageDrawable(bmp, innderCircleSize));
//        setImageDrawable(bitmap);
////        center(true, true);
//    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(w, h, BITMAP_CONFIG);
                Log.d(TAG, "WIDTH -> " + w + " HEIGHT -> " + h);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, w, h);
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                float edgeLength = ((float) getWidth()) / 2;
//                float radiusSquare = edgeLength * edgeLength * 3 / 4;
//                float dist = (event.getX() - getWidth() / 2)
//                        * (event.getX() - getWidth() / 2)
//                        + (event.getY() - getHeight() / 2)
//                        * (event.getY() - getHeight() / 2);
//
//                if (dist <= radiusSquare) {// 点中六边形区域
//                    paint.setColor(Color.parseColor("#A8A8A8"));
//                    paint.setStyle(Paint.Style.FILL);
//                    paint.setAlpha(100);
//                    invalidate();
//                }
//
//                break;
//            case MotionEvent.ACTION_UP:
//                paint.setColor(bgColor);
//                paint.setStyle(Paint.Style.FILL);
//                paint.setAlpha(200);
//                //String dd = this.getTag().toString();
//
//                invalidate();
//                CharSequence flagIcons = this.getContentDescription();//Flag_image
//                if(flagIcons==null){
//
//                }else{
//                    Message msg1=new Message();
//                    msg1.what = Integer.parseInt(flagIcons.toString());
//                    MainActivity.myHandler.sendMessage(msg1);
//                }
//                break;
//        }
//        return true;
//    }
}
