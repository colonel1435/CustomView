package com.zero.customview.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
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

import java.security.PublicKey;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/15 0015 16:58
 */

public class DanmukuPopupwindow extends PopupWindow implements View.OnClickListener{
    private Context mContext;
    private EditText mDanmukuEdit;
    private TextView mDanmukuSend;
    private OnDanmukuCallback mSendCallback;

    public DanmukuPopupwindow(Context context) {
        super(context);
        mContext = context;
        initSettings();
    }

    private void initSettings() {
        View popupView = LayoutInflater.from(mContext).inflate(R.layout.popupwindows_danmuku, null);
        mDanmukuEdit = (EditText) popupView.findViewById(R.id.et_danmuku_content);
        mDanmukuSend = (TextView) popupView.findViewById(R.id.tv_danmuku_send);
        mDanmukuSend.setOnClickListener(this);

        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setContentView(popupView);
        this.setTouchable(true);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new ColorDrawable());
        this.setAnimationStyle(R.style.pop_add_ainm);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

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
        mSendCallback.onSend(mDanmukuEdit.getText().toString());
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
}
