package com.zero.customview.view.bottombar;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zero.customview.R;
import com.zero.customview.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zero on 2017/9/24.
 */

public class AnimBottomBar extends LinearLayout {
    private List<BottomItem> mItems;
    private Context mContext;
    private int itemHeight = 44;
    private int itemWidth;
    private int childNum;
    private int barWidth;
    private int barHeight;
    private int mTextSize;
    public AnimBottomBar(Context context) {
        this(context, null);
    }

    public AnimBottomBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimBottomBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        mItems = new ArrayList<>();
        mTextSize = DisplayUtils.dip2px(mContext, 12);
    }

    public AnimBottomBar addItem(BottomItem item) {
        mItems.add(item);
        return this;
    }

    public AnimBottomBar build() {
        childNum = mItems.size();
        itemWidth = getLayoutParams().width / childNum;
        for(BottomItem item : mItems) {
            ImageView imageView = new ImageView(mContext);
            imageView.setImageResource(item.getResId());
            addView(imageView, itemWidth, itemHeight);
        }

        for(BottomItem item : mItems) {
            TextView tv = new TextView(mContext);
            tv.setText(item.getTitle());
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(mTextSize);
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));
            addView(tv, itemWidth, itemHeight);
        }
        return this;
    }

    private int measureSize(int measureSpec) {
        int defaultSize = 480;
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        barWidth = measureSize(widthMeasureSpec);
        barHeight = measureSize(heightMeasureSpec);
        for(int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            child.getLayoutParams().width = itemWidth;
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for(int i = 0; i < childNum; i++) {
            View childImage = getChildAt(i);
            childImage.layout(itemWidth*i, 0, itemWidth*(i+1), 100);
            View childText = getChildAt(childNum+i);
            childText.layout(itemWidth*i+childText.getWidth()/4, 100,
                    itemWidth*(i+1), barHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
