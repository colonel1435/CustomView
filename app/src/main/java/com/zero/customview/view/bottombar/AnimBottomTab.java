package com.zero.customview.view.bottombar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
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

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/25 0025 9:37
 */

public class AnimBottomTab extends LinearLayout {
    private Context mContext;
    private TextView mTitle;
    private ImageView mImage;
    private int mChildCount = 2;

    private String mTitleText;
    private int mTitleSize;
    private int mTitleColor;
    private int mImageRes;
    private int mImageWidth = 48;
    private int mImageHeight = 48;
    private int mTopPadding = 0;
    private int mBottomPadding = 0;
    private int mSelectColor;
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
        ta.recycle();

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        mImage = new ImageView(mContext);
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

    public void updateTabScale(float scale) {
        mTitle.setScaleX(scale);
        mTitle.setScaleY(scale);

        mImage.setScaleX(scale);
        mImage.setScaleY(scale);
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
