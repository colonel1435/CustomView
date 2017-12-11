package com.zero.customview.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;

import com.zero.customview.R;
import com.zero.customview.view.WaveProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WaveActivity extends AppCompatActivity {

    @BindView(R.id.waveProgressBar)
    WaveProgressBar waveProgressBar;
    @BindView(R.id.seek_bar)
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        seekBar.setMax(100);
        seekBar.setProgress(50);
        waveProgressBar.setProgressValue(50);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                waveProgressBar.setProgressValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
