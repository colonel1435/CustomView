package com.zero.customview.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.zero.customview.R;
import com.zero.customview.view.ruler.RulerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RulerActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName()+"@wuming";
    @BindView(R.id.ruler)
    RulerView ruler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruler);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        ruler.setCurrentNumber(10);
        ruler.setOnCurrentChangeListener(new RulerView.OnCurrentChangeListener() {
            @Override
            public void onChange(float current) {
                Log.d(TAG, "onChange: " + current);
            }
        });
    }
}
