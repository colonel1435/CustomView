package com.zero.customview.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.zero.customview.R;
import com.zero.customview.view.ruler.BaseRuler;
import com.zero.customview.view.ruler.HorizontalArrowRuler;
import com.zero.customview.view.ruler.HorizontalRuler;
import com.zero.customview.view.ruler.VerticalRuler;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RulerActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName() + "@wuming";
    @BindView(R.id.tv_horizontal)
    TextView tvHorizontal;
    @BindView(R.id.ht_ruler)
    HorizontalRuler htRuler;
    @BindView(R.id.tv_horizontal_arrow)
    TextView tvHorizontalArrow;
    @BindView(R.id.arrow_ruler)
    HorizontalArrowRuler arrowRuler;
    @BindView(R.id.vertical_ruler)
    VerticalRuler verticalRuler;

    private float mNumber = 60.5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruler);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {

        tvHorizontal.setText(String.valueOf(mNumber));
        htRuler.setCurrentNumber(mNumber);
        htRuler.setOnCurrentChangeListener(new BaseRuler.OnCurrentChangeListener() {
            @Override
            public void onChange(float current) {
                tvHorizontal.setText(String.valueOf(current));
            }
        });

        tvHorizontalArrow.setText(String.valueOf(40));
        arrowRuler.setCurrentNumber(40);
        arrowRuler.setOnCurrentChangeListener(new BaseRuler.OnCurrentChangeListener() {
            @Override
            public void onChange(float current) {
                tvHorizontalArrow.setText(String.valueOf(current));
            }
        });
        arrowRuler.setScaleRatio(10);

        verticalRuler.setCurrentNumber(99);
    }
}
