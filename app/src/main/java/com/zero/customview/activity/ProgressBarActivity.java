package com.zero.customview.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zero.customview.R;
import com.zero.customview.view.HorizontalProgressBar;
import com.zero.customview.view.MultilevelProgressBar;
import com.zero.customview.view.RoundProgressBar;

public class ProgressBarActivity extends AppCompatActivity {
    private Context mContext = null;
    private ProgressBar mProgressBar;
    private PopupWindow mPopupWindows;
    private FrameLayout mProgressLayout;
    private LinearLayout mSpeakLayout;
    private LinearLayout mSpeakErrLayout;
    private TextView mMsgText;
    private ImageView mMsgImage;
    private ImageView mTest;
    private HorizontalProgressBar mHorizontalProgress;
    private RoundProgressBar mRoundProgress;
    private MultilevelProgressBar mMultiLevelProgress;
    public static final int HORIZONTAL_PROGRESSBAR_UPDATE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);

        initView();
    }

    private void initView() {
        mContext = this;
        mHorizontalProgress = (HorizontalProgressBar) findViewById(R.id.horizontal_progress_bar);
        mHorizontalProgress.setMax(100);
        mRoundProgress = (RoundProgressBar) findViewById(R.id.round_progress_bar);
        mRoundProgress.setMax(100);
        mMultiLevelProgress = (MultilevelProgressBar) findViewById(R.id.multilevel_progress_bar);
        mMultiLevelProgress.setIndeterminate(true);
        mHandler.sendEmptyMessage(HORIZONTAL_PROGRESSBAR_UPDATE);
    }

    public void onDebug(View view) {
        showSpeakDialog(view);
    }

    public void dardBackground(Activity context, float alpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = alpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    public void showSpeakDialog(View view) {
        View popupView = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_voice, null);
        mProgressLayout = (FrameLayout)popupView.findViewById(R.id.fl_progressbar);
        mSpeakLayout = (LinearLayout)popupView.findViewById(R.id.ll_speak_image);
        mSpeakErrLayout = (LinearLayout)popupView.findViewById(R.id.ll_speak_err);
        mMsgText = (TextView)popupView.findViewById(R.id.tv_msg_text);
        mMsgImage = (ImageView)popupView.findViewById(R.id.iv_msg_image);
        mPopupWindows = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindows.setTouchable(true);
        mPopupWindows.setFocusable(true);
        mPopupWindows.setOutsideTouchable(true);
        mPopupWindows.setBackgroundDrawable(new ColorDrawable());
        mPopupWindows.setAnimationStyle(R.style.popwindow_voice_anim);
        mPopupWindows.getBackground().setAlpha(50);
        dardBackground(this, 0.4f);
        mPopupWindows.showAtLocation(view, Gravity.CENTER, 0 ,0);
        mPopupWindows.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dardBackground(ProgressBarActivity.this, 1.0f);
            }
        });
    }
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int id = msg.what;
            switch (id) {
                case HORIZONTAL_PROGRESSBAR_UPDATE:
                    int progress = mHorizontalProgress.getProgress();
                    int rProgress = mRoundProgress.getProgress();
                    mRoundProgress.setProgress(++rProgress);
                    mHorizontalProgress.setProgress(++progress);
                    if (progress >= 100) {
                        mHandler.removeMessages(HORIZONTAL_PROGRESSBAR_UPDATE);
                    }
                    mHandler.sendEmptyMessageDelayed(HORIZONTAL_PROGRESSBAR_UPDATE, 100);
                    break;
            }
        }
    };
}
