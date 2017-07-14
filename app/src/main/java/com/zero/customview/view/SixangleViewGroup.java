package com.zero.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.zero.customview.R;


/**
 * Created by Administrator on 2017/2/15.
 */

public class SixangleViewGroup extends ViewGroup {
    private final String TAG = this.getClass().getSimpleName() + "@wumin";
    private int radius = 120;

    public SixangleViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        TypedArray type=context.obtainStyledAttributes(attrs, R.styleable.sixangleImageView);
        radius = type.getInteger(R.styleable.sixangleImageView_imageRadius, radius);

    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int viewWidth;
        int viewHeight;
        double radian30 = 30 * Math.PI / 180;
        int h = (int) (radius * Math.cos(radian30));
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int outCircleRadius = (int) ((radius/2) * Math.cos(radian30));
        int topCenterX = 0;
        int topCenterY = 0;
        int bottomCenterX = 0;
        int bottomCenterY = 0;

//        Log.d(TAG, "Center -> " + middle);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            viewWidth = child.getMeasuredWidth();
            viewHeight = child.getMeasuredHeight();
//            Log.d(TAG, "Radius -> " + radius + " Width -> " + viewWidth + " Height -> " + viewHeight);
            if (0 == i) {
                topCenterX = centerX ;
                topCenterY = centerY - outCircleRadius;
                child.layout(topCenterX - viewWidth/2, topCenterY - viewWidth/2, topCenterX + viewWidth/2, topCenterY + viewWidth/2);
            }

            if (1 == i) {
                bottomCenterX = topCenterX - radius/2;
                bottomCenterY = topCenterY + h;
                child.layout(bottomCenterX - viewWidth/2, bottomCenterY -viewWidth/2, bottomCenterX + viewWidth/2, bottomCenterY + viewWidth/2);
            }

            if (2 == i) {
                bottomCenterX = topCenterX + radius/2;
                bottomCenterY = topCenterY + h;
                child.layout(bottomCenterX - viewWidth/2, bottomCenterY -viewWidth/2, bottomCenterX + viewWidth/2, bottomCenterY + viewWidth/2);
            }

            if (3 == i) {
                bottomCenterX = centerX;
                bottomCenterY = centerY + viewHeight/2;
                child.layout(bottomCenterX - viewWidth/2, bottomCenterY - viewHeight/2, bottomCenterX + viewWidth/2, bottomCenterY + viewHeight/2);
                Log.d(TAG, "onLayout Width -> " + viewWidth + " Height -> " + viewHeight);

            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            Log.d(TAG, "onMeasure Width -> " + child.getWidth() + " Height -> " + child.getHeight()) ;
        }
    }
}