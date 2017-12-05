package com.zero.customview.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zero.customview.R;
import com.zero.customview.view.flip.FlipView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FlipActivity extends AppCompatActivity {

    @BindView(R.id.flip_view)
    FlipView flipView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
    }
}
