package com.zero.customview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zero.customview.utils.DisplayUtils;
import com.zero.customview.view.BallView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AnimActivity extends AppCompatActivity {
    @BindView(R.id.ball_veritcal)
    BallView ballVeritcal;
    @BindView(R.id.ball_parabola)
    BallView ballParabola;
    @BindView(R.id.ll_point_circle_1)
    LinearLayout llPointCircle1;
    @BindView(R.id.ll_point_circle_2)
    LinearLayout llPointCircle2;
    @BindView(R.id.ll_point_circle_3)
    LinearLayout llPointCircle3;
    @BindView(R.id.ll_point_circle_4)
    LinearLayout llPointCircle4;
    @BindView(R.id.tv_startup)
    TextView tvStartup;
    @BindView(R.id.tv_shop)
    TextView tvShop;
    @BindView(R.id.iv_shop_car)
    ImageView ivShopCar;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anim);
        ButterKnife.bind(this);

        mContext = this;
        initView();
    }

    private void initView() {
    }

    private void customAnim() {
        ObjectAnimator anim = ObjectAnimator
                .ofFloat(tvShop, "custom", 1.0f, 0.0f, 1.0f)
                .setDuration(1000);
        anim.start();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                tvShop.setAlpha(val);
                tvShop.setScaleX(val);
                tvShop.setScaleY(val);
            }
        });
    }

    @OnClick({R.id.tv_shop, R.id.ball_veritcal, R.id.ball_parabola, R.id.tv_startup})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_shop:
                customAnim();
                beginShopping(view);
                break;
            case R.id.ball_veritcal:
                verticalDropdown(view);
                break;
            case R.id.ball_parabola:
                parabolaDropdown(view);
                break;
            case R.id.tv_startup:
                beginStartupAnim();
                break;
        }
    }

    private void beginShopping(final View v) {
        final ImageView dot = new ImageView(this);
        dot.setImageResource(R.drawable.shape_dot);
        dot.setScaleType(ImageView.ScaleType.MATRIX);
        dot.setLayoutParams(new ViewGroup.LayoutParams(50,50));
        ViewGroup rootView = (ViewGroup) this.getWindow().getDecorView();
        rootView.addView(dot);

        int[] pos = new int[2];
        tvShop.getLocationInWindow(pos);

        int[] des = new int[2];
        ivShopCar.getLocationInWindow(des);

        Point startPosition = new Point(pos[0], pos[1]);
        Point endPosition = new Point(des[0] + ivShopCar.getWidth() / 2, des[1] + ivShopCar.getHeight() / 2);

        int pointX = (startPosition.x + endPosition.x) / 2 - 100;
        int pointY = startPosition.y - 200;
        Point controllPoint = new Point(pointX, pointY);

        ValueAnimator valueAnimator = ValueAnimator.ofObject(new BizierEvaluator(controllPoint),startPosition, endPosition);
        valueAnimator.setDuration(1000);
        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Point point = (Point) valueAnimator.getAnimatedValue();
                dot.setX(point.x);
                dot.setY(point.y);
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ViewGroup rootView = (ViewGroup) AnimActivity.this.getWindow().getDecorView();
                rootView.removeView(dot);
            }
        });

    }

    private void verticalDropdown(final View v) {
        ValueAnimator animator = ValueAnimator.ofFloat(0,
                DisplayUtils.getScreenHeight(mContext) - v.getHeight());

        animator.setTarget(v);
        animator.setInterpolator(new BounceInterpolator());
        animator.setDuration(2000).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.setTranslationY((float) animation.getAnimatedValue());
            }
        });
    }

    private void parabolaDropdown(final View v) {
        ValueAnimator animator = new ValueAnimator();
        animator.setDuration(4000);
        animator.setObjectValues(new PointF(0, 0),
                new PointF((DisplayUtils.getScreenWidth(mContext) - v.getWidth()) / 2,
                        DisplayUtils.getScreenHeight(mContext) - v.getHeight()));
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setEvaluator(new TypeEvaluator() {
            @Override
            public Object evaluate(float fraction, Object startValue, Object endValue) {
                // x方向200px/s ，则y方向0.5 * 10 * t
                PointF point = new PointF();
                point.x = 200 * fraction * 4;
                point.y = 0.5f * 200 * (fraction * 4) * (fraction * 4);

                return point;
            }
        });
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                v.setTranslationX(pointF.x);
                v.setTranslationY(pointF.y);
            }
        });
    }

    private void beginStartupAnim() {
        ObjectAnimator animator_1 = ObjectAnimator.ofFloat(
                llPointCircle1,
                "rotation",
                0,
                360);
        animator_1.setDuration(2000);
        animator_1.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator animator_2 = ObjectAnimator.ofFloat(
                llPointCircle2,
                "rotation",
                0,
                360);
        animator_2.setStartDelay(150);
        animator_2.setDuration(2000 + 150);
        animator_2.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator animator_3 = ObjectAnimator.ofFloat(
                llPointCircle3,
                "rotation",
                0,
                360);
        animator_3.setStartDelay(2 * 150);
        animator_3.setDuration(2000 + 2 * 150);
        animator_3.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator animator_4 = ObjectAnimator.ofFloat(
                llPointCircle4,
                "rotation",
                0,
                360);
        animator_4.setStartDelay(3 * 150);
        animator_4.setDuration(2000 + 3 * 150);
        animator_4.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animator_1).with(animator_2).with(animator_3).with(animator_4);
        animatorSet.start();
    }

    private class BizierEvaluator implements TypeEvaluator<Point> {
        private Point controllPoint;

        public BizierEvaluator(Point point) {
            controllPoint = point;
        }
        @Override
        public Point evaluate(float fraction, Point startValue, Point endValue) {
            int x = (int) ((1 - fraction) * (1 - fraction) * startValue.x + 2 * fraction * (1 - fraction) * controllPoint.x + fraction * fraction * endValue.x);
            int y = (int) ((1 - fraction) * (1 - fraction) * startValue.y + 2 * fraction * (1 - fraction) * controllPoint.y + fraction * fraction * endValue.y);
            return new Point(x, y);
        }
    }
}
