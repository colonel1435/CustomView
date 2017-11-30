package com.zero.customview.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.zero.customview.R;
import com.zero.customview.view.ruler.RulerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RulerActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName() + "@wuming";
    @BindView(R.id.ruler)
    RulerView ruler;
    @BindView(R.id.tv_current)
    TextView tvCurrent;

    private float mNumber = 60.5f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruler);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        tvCurrent.setText(String.valueOf(mNumber));
        ruler.setCurrentNumber(mNumber);
        ruler.setOnCurrentChangeListener(new RulerView.OnCurrentChangeListener() {
            @Override
            public void onChange(float current) {
                tvCurrent.setText(String.valueOf(current));
            }
        });
    }
}
