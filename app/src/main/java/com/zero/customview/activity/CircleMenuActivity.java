package com.zero.customview.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.zero.customview.R;
import com.zero.customview.utils.TipUtils;
import com.zero.customview.view.CircleMenuView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CircleMenuActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.circle_menu)
    CircleMenuView circleMenu;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_menu);
        ButterKnife.bind(this);

        mContext = this;
        initView();

    }

    private void initView() {
        toolbar.setTitle(getString(R.string.circle_menu));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        circleMenu.setMenuClickListener(new CircleMenuView.onMenuClickListener() {
            @Override
            public void onCenterClick() {
                TipUtils.showTip(mContext, "Message", "CENTER CLICKED!");
            }

            @Override
            public void onTopClick() {
                TipUtils.showTip(mContext, "Message", "TOP CLICKED!");
            }

            @Override
            public void onBottomClick() {
                TipUtils.showTip(mContext, "Message", "BOTTOM CLICKED!");
            }

            @Override
            public void onLeftClick() {
                TipUtils.showTip(mContext, "Message", "LEFT CLICKED!");
            }

            @Override
            public void onRightClick() {
                TipUtils.showTip(mContext, "Message", "RIGHT CLICKED!");
            }
        });
    }

}
