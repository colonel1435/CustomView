package com.zero.customview.view.vedio;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zero.customview.R;
import com.zero.customview.utils.DisplayUtils;

import java.util.Random;

/**
 * Created by zhuyong on 2017/7/19.
 */

public class BalloonRelativeLayout extends RelativeLayout {
    private final String TAG = this.getClass().getSimpleName() + "@wumin";
    private Context mContext;
    private Interpolator[] interpolators;
    private Interpolator linearInterpolator = new LinearInterpolator();// 以常量速率改变
    private Interpolator accelerateInterpolator = new AccelerateInterpolator();//加速
    private Interpolator decelerateInterpolator = new DecelerateInterpolator();//减速
    private Interpolator accelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();//先加速后减速
    private LayoutParams layoutParams;
    private LayoutParams heartLayoutParams;
    private int mHeight;
    private int mWidth;
    private Random random = new Random();
    private int mViewHeight = DisplayUtils.dip2px(getContext(), 50);
    private Drawable[] drawables;
    private Drawable[] heartDrawables;

    public BalloonRelativeLayout(Context context) {
        this(context, null);
    }

    public BalloonRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BalloonRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        //初始化显示的图片
        drawables = new Drawable[3];
        Drawable mBalloon = ContextCompat.getDrawable(mContext, R.mipmap.balloon_pink);
        Drawable mBalloon2 = ContextCompat.getDrawable(mContext, R.mipmap.balloon_purple);
        Drawable mBalloon3 = ContextCompat.getDrawable(mContext, R.mipmap.balloon_blue);
        drawables[0] = mBalloon;
        drawables[1] = mBalloon2;
        drawables[2] = mBalloon3;

        heartDrawables = new Drawable[3];
        Drawable redDrawable = ContextCompat.getDrawable(mContext, R.mipmap.pl_red);
        Drawable blueDrawable = ContextCompat.getDrawable(mContext, R.mipmap.pl_blue);
        Drawable yellowDrawble = ContextCompat.getDrawable(mContext, R.mipmap.pl_yellow);
        heartDrawables[0] = redDrawable;
        heartDrawables[1] = blueDrawable;
        heartDrawables[2] = yellowDrawble;

        heartLayoutParams = new LayoutParams(mViewHeight, mViewHeight);
        heartLayoutParams.addRule(CENTER_HORIZONTAL, TRUE);
        heartLayoutParams.addRule(ALIGN_PARENT_BOTTOM, TRUE);

        layoutParams = new LayoutParams(mViewHeight, mViewHeight);
        layoutParams.addRule(ALIGN_PARENT_BOTTOM, TRUE);

        // 初始化插值器
        interpolators = new Interpolator[4];
        interpolators[0] = linearInterpolator;
        interpolators[1] = accelerateInterpolator;
        interpolators[2] = decelerateInterpolator;
        interpolators[3] = accelerateDecelerateInterpolator;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    public void addBalloon() {

        final ImageView imageView = new ImageView(getContext());
        //随机选一个
        imageView.setImageDrawable(drawables[random.nextInt(3)]);
        imageView.setLayoutParams(layoutParams);
        addView(imageView);

        Animator animator = getBalloonAnimator(imageView, new PointF(0, getHeight()));
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                removeView(imageView);
            }
        });
        animator.start();
    }

    public void addHeart(PointF startPoint) {
        Log.d(TAG, "addHeart: X -> " + startPoint.x + " Y -> " + startPoint.y);
        final ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(heartDrawables[random.nextInt(3)]);
        imageView.setLayoutParams(heartLayoutParams);
        imageView.setX(startPoint.x);
        imageView.setY(startPoint.y);
        addView(imageView);

        PointF point = new PointF(startPoint.x - imageView.getWidth(),
                                startPoint.y - imageView.getHeight());
        getHeartAnimator(imageView, point).start();
    }

    private Animator getHeartAnimator(final View target, PointF startPoint) {
        AnimatorSet enterAnimtor = getHeartEnterAnimtor(target);
        ValueAnimator bezierAnimator = getBezierValueAnimator(target, startPoint);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(enterAnimtor);
        animatorSet.playSequentially(enterAnimtor, bezierAnimator);
        animatorSet.setInterpolator(interpolators[random.nextInt(4)]);
        animatorSet.setTarget(target);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                removeView(target);
                Log.d(TAG, "onAnimationEnd: " + getChildCount());
            }
        });
        return animatorSet;
    }

    private Animator getBalloonAnimator(View target, PointF startPoint) {

        ValueAnimator bezierValueAnimator = getBezierValueAnimator(target, startPoint);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(bezierValueAnimator);
        animatorSet.setInterpolator(interpolators[random.nextInt(4)]);
        animatorSet.setTarget(target);
        return animatorSet;
    }

    private ValueAnimator getBezierValueAnimator(final View target, PointF startPoint) {

        BezierEvaluator evaluator = new BezierEvaluator(getPointF(), getPointF());

        ValueAnimator animator = ValueAnimator.ofObject(evaluator, startPoint
                , new PointF(random.nextInt(getWidth()), -mViewHeight));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                target.setX(pointF.x);
                target.setY(pointF.y);
            }
        });
        animator.setTarget(target);
        animator.setDuration(5000);
        return animator;
    }


    private PointF getPointF() {
        PointF pointF = new PointF();
        pointF.x = random.nextInt(mWidth);
        pointF.y = random.nextInt(mHeight);
        return pointF;
    }


    class BezierEvaluator implements TypeEvaluator<PointF> {

        private PointF pointF1;
        private PointF pointF2;

        public BezierEvaluator(PointF pointF1, PointF pointF2) {
            this.pointF1 = pointF1;
            this.pointF2 = pointF2;
        }

        @Override
        public PointF evaluate(float time, PointF startValue,
                               PointF endValue) {

            float timeOn = 1.0f - time;
            PointF point = new PointF();
            point.x = timeOn * timeOn * timeOn * (startValue.x)
                    + 3 * timeOn * timeOn * time * (pointF1.x)
                    + 3 * timeOn * time * time * (pointF2.x)
                    + time * time * time * (endValue.x);

            point.y = timeOn * timeOn * timeOn * (startValue.y)
                    + 3 * timeOn * timeOn * time * (pointF1.y)
                    + 3 * timeOn * time * time * (pointF2.y)
                    + time * time * time * (endValue.y);
            return point;
        }
    }

    private AnimatorSet getHeartEnterAnimtor(final View target) {

        ObjectAnimator alpha = ObjectAnimator.ofFloat(target,View.ALPHA, 0.2f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(target,View.SCALE_X, 0.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(target,View.SCALE_Y, 0.2f, 1f);
        AnimatorSet enter = new AnimatorSet();
        enter.setDuration(300);
        enter.setInterpolator(new LinearInterpolator());
        enter.playTogether(alpha,scaleX, scaleY);
        enter.setTarget(target);
        return enter;
    }

}
