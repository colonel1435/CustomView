package com.zero.customview.view.bottombar;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zero.customview.R;
import com.zero.customview.view.MiClockView;

import java.security.PublicKey;

import static com.zero.customview.R.attr.selectableItemBackgroundBorderless;
import static com.zero.customview.R.attr.switchMinWidth;
import static com.zero.customview.R.attr.thickness;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/25 0025 9:37
 */

public class AnimBottomTab extends LinearLayout {
    private final String TAG = this.getClass().getSimpleName()+"@wumin";
    public enum TabType {NORMAL, MESSAGE, USER}
    private Context mContext;
    private TextView mTitle;
    private ImageView mImage;
    private int mChildCount = 2;

    private String mTitleText;
    private int mTitleSize;
    private int mTitleColor;
    private int mImageRes;
    private int mImageWidth = 56;
    private int mImageHeight = 56;
    private int mTopPadding = 0;
    private int mBottomPadding = 0;
    private int mSelectColor;
    private int mTabType;
    public AnimBottomTab(Context context) {
        this(context, null);
    }

    public AnimBottomTab(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimBottomTab(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.AnimBottomTab);
        mTitleText = ta.getString(R.styleable.AnimBottomTab_anim_tab_text);
        mTitleSize = ta.getDimensionPixelSize(R.styleable.AnimBottomTab_anim_tab_textSize, 12);
        mTitleColor = ta.getColor(R.styleable.AnimBottomTab_anim_tab_textColor, Color.BLACK);
        mImageRes = ta.getResourceId(R.styleable.AnimBottomTab_anim_tab_image, R.mipmap.ic_launcher);
        mSelectColor =ta.getColor(R.styleable.AnimBottomTab_anim_tab_selectColor, Color.WHITE);
        mTabType = ta.getInt(R.styleable.AnimBottomTab_anim_tab_type, TabType.NORMAL.ordinal());
        ta.recycle();

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        setPadding(0, 5, 0, 5);

        if (mTabType == TabType.NORMAL.ordinal()) {
            mImage = new ImageView(mContext);
        } else {
            mImage = new MessageTab(mContext);
        }
        mImage.setImageResource(mImageRes);
        LayoutParams params = new LayoutParams(mImageWidth, mImageHeight);
        mImage.setPadding(0, mTopPadding, 0, mBottomPadding);
        addView(mImage, params);

        mTitle = new TextView(mContext);
        mTitle.setGravity(Gravity.CENTER);
        mTitle.setText(mTitleText);
        mTitle.setTextSize(mTitleSize);
        mTitle.setTextColor(mTitleColor);
        addView(mTitle);
    }

    public ImageView getImage() {
        return this.mImage;
    }

    public TextView getTitle() {
        return this.mTitle;
    }

    public int getSelectColor() {
        return this.mSelectColor;
    }

    public int getImageRes() {
        return this.mImageRes;
    }

    public int getTabType() {
        return this.mTabType;
    }

    public void updateTabWithScale(float offset) {
        float scaleValue;
        int tabWidth = getWidth();
        if (offset < tabWidth) {
            scaleValue = (float) (1.0 - 0.2*offset/tabWidth);
        } else {
            scaleValue = 0.8f;
        }

        mTitle.setScaleX(scaleValue);
        mTitle.setScaleY(scaleValue);

        mImage.setScaleX(scaleValue);
        mImage.setScaleY(scaleValue);
    }

    public void updateTabWithGradient(float offset) {
        float scaleValue;
        int tabWidth = getWidth();
        if (offset < tabWidth) {
            scaleValue = offset/tabWidth;
        } else {
            scaleValue = 1.0f;
        }
        if (mTabType == TabType.MESSAGE.ordinal()) {
            ((MessageTab)mImage).updateRadius(scaleValue);
        }
        Log.d(TAG, "updateTabWithGradient: offset -> " + offset + " scale -> " + scaleValue);
    }

    public void updateTabAnimation(int type, float offset) {
        if (type == AnimBottomBar.ANIMATION.SCALE.ordinal()) {
            updateTabWithScale(offset);
        } else if (type == AnimBottomBar.ANIMATION.GRADIENT.ordinal()){
            updateTabWithGradient(offset);
        }
    }

    public void selected() {
        updateTabColor(mTitleColor, mSelectColor);
    }

    public void unselected() {
        updateTabColor(mSelectColor, mTitleColor);
    }

    public void updateTabColor(int previous, int current) {
        ValueAnimator colorAnimator = ValueAnimator.ofInt(previous, current);
        colorAnimator.setDuration(500);
        colorAnimator.setEvaluator(new ArgbEvaluator());
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int colorValue = (int) animation.getAnimatedValue();
                updateColor(colorValue);
            }
        });
        colorAnimator.start();
    }


    public void updateColor(int color) {
        if (mImage != null) {
            if (mTabType == TabType.NORMAL.ordinal()) {
                mImage.setColorFilter(color);
            } else {
                if (mTabType == TabType.MESSAGE.ordinal()) {
                    ((MessageTab)mImage).updateColor(color);
                }
            }
        }

        if (mTitle != null) {
            mTitle.setTextColor(color);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < mChildCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
