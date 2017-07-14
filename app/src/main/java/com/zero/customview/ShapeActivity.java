package com.zero.customview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zero.customview.utils.TipUtils;
import com.zero.customview.view.CircleTextImageView;
import com.zero.customview.view.SixangleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShapeActivity extends AppCompatActivity {

    @BindView(R.id.iv_top)
    SixangleImageView ivTop;
    @BindView(R.id.iv_left)
    SixangleImageView ivLeft;
    @BindView(R.id.iv_right)
    SixangleImageView ivRight;
    @BindView(R.id.iv_center)
    CircleTextImageView ivCenter;

    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape);
        ButterKnife.bind(this);

        mContext = this;
    }

    @OnClick({R.id.iv_top, R.id.iv_left, R.id.iv_right, R.id.iv_center})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_top:
                TipUtils.showTip(mContext, getString(R.string.msg_title), getString(R.string.look_raw));
                break;
            case R.id.iv_left:
                TipUtils.showTip(mContext, getString(R.string.msg_title), getString(R.string.look_cut));
                break;
            case R.id.iv_right:
                TipUtils.showTip(mContext, getString(R.string.msg_title), getString(R.string.look_scale));
                break;
            case R.id.iv_center:
                TipUtils.showTip(mContext, getString(R.string.msg_title), getString(R.string.take_photo));
                break;
        }
    }
}
