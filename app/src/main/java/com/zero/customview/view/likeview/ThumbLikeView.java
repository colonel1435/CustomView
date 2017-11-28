package com.zero.customview.view.likeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.zero.customview.R;

/**
 * Description
 * Author : Mr.wuming
 * Email  : fusu1435@163.com
 * Date   : 2017/11/28 0028 9:36
 */

public class ThumbLikeView extends LinearLayout {
    private static final float DEFAULT_NUMBER_SIZE = 24f;
    private static final int DEFAULT_NUMBER = 0;
    private Context mContext;
    private int mNumber;
    private int mTextColor;
    private float mTextSize;
    private ThumbUpView mThumbUpView;
    private ShiftNumberView mNumberView;
    private OnLikeListener mLikeListener;

    public ThumbLikeView(Context context) {
        this(context, null);
    }

    public ThumbLikeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThumbLikeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ThumbLikeView);
        mTextColor = ta.getColor(R.styleable.ThumbLikeView_thumb_number_color, Color.GRAY);
        mTextSize = ta.getDimension(R.styleable.ThumbLikeView_thumb_number_size,
                DEFAULT_NUMBER_SIZE);
        ta.recycle();
        mContext = context;
        initView();
    }

    private void initView() {
        initThumbUp();
        initShiftNumber();
    }

    private void initThumbUp() {
        mThumbUpView = new ThumbUpView(mContext);
        mThumbUpView.setClickListener(new ThumbUpView.OnThumbUpListener() {
            @Override
            public void onLike(boolean isLike) {
                if (isLike) {
                    mNumber ++;
                } else {
                    mNumber --;
                }
                mNumberView.setText(String.valueOf(mNumber));
                if (mLikeListener != null) {
                    mLikeListener.onLike(isLike);
                }
            }
        });
        addView(mThumbUpView);
    }

    private void initShiftNumber() {
        mNumber = DEFAULT_NUMBER;
        mNumberView = new ShiftNumberView(mContext);
        mNumberView.setTextColor(mTextColor);
        mNumberView.setTextSize(mTextSize);
        addView(mNumberView);
    }

    public void setNumber(int number) {
        this.mNumber = number;
        mNumberView.setText(String.valueOf(number));
    }

    public int getNumber() {
        return this.mNumber;
    }

    public void setLikeListener(OnLikeListener listener) {
        mLikeListener = listener;
    }

    public interface OnLikeListener {
        void onLike(boolean isLiked);
    }
}
