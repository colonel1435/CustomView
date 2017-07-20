package com.zero.customview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AnimActivity extends AppCompatActivity {

    @BindView(R.id.tv_anim)
    TextView tvAnim;

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

    @OnClick(R.id.tv_anim)
    public void onViewClicked() {
        customAnim();
    }

    private void customAnim() {
        ObjectAnimator anim = ObjectAnimator
                .ofFloat(tvAnim, "custom", 1.0f, 0.0f, 1.0f)
                .setDuration(1000);
        anim.start();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                tvAnim.setAlpha(val);
                tvAnim.setScaleX(val);
                tvAnim.setScaleY(val);
            }
        });
    }

}
