package com.zero.customview.view.bottombar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zero.customview.R;
import com.zero.customview.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zero on 2017/9/24.
 */

public class AnimBottomBar extends LinearLayout implements View.OnClickListener{
    private final String TAG = this.getClass().getSimpleName()+"@wumin";
    public enum ANIMATION { DEFAULT, SCALE, GRADIENT, TRANSLATE}
    private final int ANIMATION_TIME = 500;
    private List<BottomTab> mItems;
    private Context mContext;
    private Paint mPaint;

    private int itemWidth;
    private int childCount;
    private int barWidth;
    private int barHeight;
    private int mTextSize;
    private int mBackgoudColor;
    private int mSelectColor;
    private int mTextColor;
    private int mTextSelectColor;
    private int itemMoveLeft = 0;
    private int itemMoveRight = 0;
    private int itemMoveCenter = 0;
    private int lastMoveLeft;
    private int lastMoveRight;
    private int lastSelectPositon;
    private int itemSelectPosition = 0;
    private int[] itemCenterX;
    private float[] itemScale;
    private int defaultColor = Color.WHITE;
    private ViewPager viewPager = null;
    private OnItemSelectListener onItemSelectListener = null;
    private boolean mEableAnimation;
    private ValueAnimator slideAnimator = null;
    private ValueAnimator colorAnimator = null;
    private int mAnimType;

    public AnimBottomBar(Context context) {
        this(context, null);
    }

    public AnimBottomBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimBottomBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setWillNotDraw(false);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        mItems = new ArrayList<>();
        TypedArray ta = mContext.obtainStyledAttributes(attrs,R.styleable.AnimBottomBar);
        mBackgoudColor = ta.getColor(R.styleable.AnimBottomBar_anim_bottom_background, defaultColor);
        mSelectColor = ta.getColor(R.styleable.AnimBottomBar_anim_bottom_selectColor,defaultColor);
        mTextColor = ta.getColor(R.styleable.AnimBottomBar_anim_bottom_textColor, defaultColor);
        mTextSelectColor = ta.getColor(R.styleable.AnimBottomBar_anim_bottom_textSelectColor, defaultColor);
        mEableAnimation = ta.getBoolean(R.styleable.AnimBottomBar_anim_bottom_enableAnimation, true);
        mAnimType = ta.getInt(R.styleable.AnimBottomBar_anim_bottom_animationType, ANIMATION.DEFAULT.ordinal());
        ta.recycle();
        mTextSize = DisplayUtils.dip2px(mContext, 12);


        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        if (mEableAnimation) {
            initSlideAnimator();
        }
    }

    private void initSlideAnimator() {
        slideAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        slideAnimator.setDuration(ANIMATION_TIME);
        slideAnimator.setInterpolator(new AccelerateInterpolator());
        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animValue = (float)animation.getAnimatedValue();
                updateTabBackground(animValue);
            }
        });
        slideAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                clearAnimation();
                lastMoveLeft = itemMoveLeft;
                lastMoveRight = itemMoveRight;
                lastSelectPositon = itemSelectPosition;
            }
        });
    }

    public void setViewPager(ViewPager vp) {
        this.viewPager = vp;
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    if (itemSelectPosition != position) {
                        itemSelectPosition = position;
                        updateBottomBar();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }
    }

    public void setOnItemSelectListener(OnItemSelectListener listener) {
        this.onItemSelectListener = listener;
    }

    private void setSelectPosition(int position) {
        itemSelectPosition = position;
        itemMoveLeft = itemWidth * itemSelectPosition;
        itemMoveRight = itemWidth * (itemSelectPosition+1);
        itemMoveCenter = itemMoveLeft + itemWidth / 2;

        lastMoveLeft = itemMoveLeft;
        lastMoveRight = itemMoveRight;
        lastSelectPositon = itemSelectPosition;
        postInvalidate();
        intTabs();
    }

    private void updateTabColor() {
        AnimBottomTab previousTab = (AnimBottomTab) getChildAt(lastSelectPositon);
        previousTab.updateTabColor(mTextSelectColor, mTextColor);
        AnimBottomTab currentTab = (AnimBottomTab) getChildAt(itemSelectPosition);
        currentTab.updateTabColor(mTextColor, mTextSelectColor);
    }

    private void updateTabBackground(float animValue) {
        int position = itemSelectPosition - lastSelectPositon;
        if (position < 0) {/***    Slide to left    ***/
            itemMoveRight = (int) (lastMoveRight + animValue * itemWidth * position);
            itemMoveLeft = (int) (lastMoveLeft + animValue * itemWidth * position);
            itemMoveCenter = (int) (lastMoveRight + animValue * itemWidth * position) -itemWidth / 2;
        } else {/***    Slide to right  ***/
            itemMoveRight = (int) (lastMoveRight + animValue * itemWidth * position);
            itemMoveLeft = (int) (lastMoveLeft + animValue * itemWidth * position);
            itemMoveCenter = (int) (lastMoveLeft + animValue * itemWidth * position) + itemWidth / 2;
        }
        postInvalidate();
    }

    private void intTabs() {
        for(int i = 0; i < childCount; i++) {
            AnimBottomTab tab = (AnimBottomTab)getChildAt(i);
            if (i == itemSelectPosition) {
                tab.getTitle().setTextColor(mTextSelectColor);
                tab.updateColor(mTextSelectColor);
//                tab.getImage().setColorFilter(mTextSelectColor);
            } else {
                tab.getTitle().setTextColor(mTextColor);
                tab.updateColor(mTextColor);
//                tab.getImage().clearColorFilter();
            }
        }
    }
    private void updateBottomBar() {
        if (mEableAnimation) {
            slideAnimator.start();
        } else {
            updateTabBackground(1.0f);
        }
        updateTabColor();
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
        childCount = getChildCount();
        itemCenterX = new int[childCount];
        itemScale = new float[childCount];
        barWidth = measureSize(widthMeasureSpec);
        barHeight = measureSize(heightMeasureSpec);
        itemWidth = barWidth / childCount;
        for(int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            child.getLayoutParams().width = itemWidth;
        }

        setSelectPosition(0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        for (int i = 0; i < childCount; i++) {
            itemCenterX[i] = (int) (itemWidth * (i+0.5));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
         /***    Draw bottom tab background color   ***/
        for (int i = 0; i < childCount; i++) {
            AnimBottomTab tab = (AnimBottomTab)getChildAt(i);
            if (mSelectColor != defaultColor) {
                mPaint.setColor(mSelectColor);
            } else {
                mPaint.setColor(tab.getSelectColor());
            }
            canvas.drawRect(itemWidth * i, 0, itemWidth * (i + 1), barHeight, mPaint);
            canvas.save();

            /***    Scale tab   ***/
            int deltaX=Math.abs(itemMoveCenter-itemCenterX[i]);
            tab.updateTabAnimation(mAnimType, deltaX);
            tab.setTag(i);
            tab.setOnClickListener(this);
        }

        /***    Draw bottom bar background     ***/
        mPaint.setColor(mBackgoudColor);
        canvas.drawRect(0, 0, itemMoveLeft, barHeight, mPaint);
        canvas.drawRect(itemMoveRight, 0, itemWidth * childCount, barHeight, mPaint);
        canvas.save();
        super.onDraw(canvas);
    }

    @Override
    public void onClick(View v) {
        if (!(mEableAnimation && slideAnimator.isRunning())) {
            itemSelectPosition = (int) v.getTag();
            updateBottomBar();
            if (onItemSelectListener != null) {
                onItemSelectListener.onItemSelected(itemSelectPosition);
            }
            if (viewPager != null) {
                viewPager.setCurrentItem(itemSelectPosition);
            }
        }
    }

    public interface OnItemSelectListener {
        public void onItemSelected(int position);
    }
}
