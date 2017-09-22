package com.zero.customview.view;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zero.customview.R;
import com.zero.customview.utils.SystemBarUtils;
import com.zero.customview.utils.SystemUtils;

import java.security.PublicKey;

import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/15 0015 16:58
 */

public class DanmukuPopupwindow extends PopupWindow implements View.OnClickListener,
    EmojiconsFragment.OnEmojiconBackspaceClickedListener, EmojiconGridFragment.OnEmojiconClickedListener{
    private Context mContext;
    private EditText mDanmukuEdit;
    private TextView mDanmukuSend;
    private ImageView mFaceEdit;
    private FrameLayout mEmojContainer;
    private OnDanmukuCallback mSendCallback;
    private EmojiconsFragment emojiconsFragment;
    private final LayoutTransition transitioner = new LayoutTransition();
    private int emotionHeight;
    private FragmentManager fm;

    public DanmukuPopupwindow(Context context) {
        super(context);
        mContext = context;
        initSettings();
    }

    private void initSettings() {
        View popupView = LayoutInflater.from(mContext).inflate(R.layout.popupwindows_danmuku, null);
        mDanmukuEdit = (EditText) popupView.findViewById(R.id.et_danmuku_content);
        mDanmukuSend = (TextView) popupView.findViewById(R.id.tv_danmuku_send);
        mFaceEdit = (ImageView) popupView.findViewById(R.id.iv_danmuku_face);
        mEmojContainer = (FrameLayout) popupView.findViewById(R.id.fl_danmuku_face) ;
        mDanmukuSend.setOnClickListener(this);
        mFaceEdit.setOnClickListener(this);

        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setContentView(popupView);
        this.setTouchable(true);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new ColorDrawable());
        this.setAnimationStyle(R.style.pop_add_ainm);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        ObjectAnimator animIn = ObjectAnimator.ofFloat(null, "translationY",
                SystemUtils.getScreenHeight((Activity) mContext), emotionHeight).
                setDuration(transitioner.getDuration(LayoutTransition.APPEARING));
        transitioner.setAnimator(LayoutTransition.APPEARING, animIn);
        ObjectAnimator animOut = ObjectAnimator.ofFloat(null, "translationY",
                emotionHeight,
                SystemUtils.getScreenHeight((Activity)mContext)).
                setDuration(transitioner.getDuration(LayoutTransition.DISAPPEARING));
        transitioner.setAnimator(LayoutTransition.DISAPPEARING, animOut);
//        contentLay.setLayoutTransition(transitioner);
    }

    public void startEmojFace(FragmentManager fm, int height) {
        emojiconsFragment = EmojiconsFragment.newInstance(false);
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fl_danmuku_face, emojiconsFragment).commit();

        SystemUtils.hideSoftInput(mDanmukuEdit);
        mEmojContainer.getLayoutParams().height = height;
        mEmojContainer.setVisibility(View.VISIBLE);
    }

    public void setFragmentManager(FragmentManager fm) {
        this.fm = fm;
    }
    public void setSendCallback(OnDanmukuCallback callback) {
        mSendCallback = callback;
    }

    public void show(Activity context, View view) {
        this.showAtLocation(view, Gravity.CENTER, 0 , 0);
    }

    public void show(Activity context, View view, int gravity) {
        this.showAtLocation(view, gravity, 0, 0);
        showInputMethodForQuery(context);
    }

    public void show(Activity context, View view, int gravity, int offsetX, int offsetY) {
        this.showAtLocation(view, gravity, offsetX, offsetY);
    }


    public void dismiss(Activity context) {
        super.dismiss();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_danmuku_send:
                mSendCallback.onSend(mDanmukuEdit.getText().toString());
                break;
            case R.id.iv_danmuku_face:
                showEmotionView(fm);
                break;
            default:
                break;
        }
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(mDanmukuEdit, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(mDanmukuEdit);
    }

    public interface OnDanmukuCallback {
        void onSend(String content);
    }

    public static void showInputMethodForQuery(final Context context) {

        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void hideInputMethod(final Context context) {

        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(1, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

    }

    public void showEmotionView(FragmentManager fm) {
        int statusBarHeight = SystemBarUtils.getStatusBarHeight(mContext);
        emotionHeight = SystemUtils.getKeyboardHeight((Activity) mContext);
        startEmojFace(fm, emotionHeight);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
