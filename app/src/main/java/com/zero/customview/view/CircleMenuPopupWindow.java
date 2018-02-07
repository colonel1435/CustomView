package com.zero.customview.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.zero.customview.R;
/**
 * Description
 *
 * @author : Mr.wuming
 * @email : fusu1435@163.com
 * @date : 2017/12/23 0023 8:49
 */

public class CircleMenuPopupWindow extends PopupWindow {
    private Context mContext;
    private OnDismissListener mDismissListener;
    private CircleMenuView mCircleMenu;
    public CircleMenuPopupWindow(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_circle_menu, null);
        mCircleMenu = (CircleMenuView) view.findViewById(R.id.circle_menu);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setContentView(view);
        this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setTouchable(true);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setAnimationStyle(R.style.popwindow_voice_anim);
        this.setOnDismissListener(mDismissListener);
    }

    public void show(Activity activity, View view) {
        darkBackground(activity, 1.0f);
        this.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    public void setDismissListener(final Activity context, OnDismissListener listener) {
        mDismissListener = listener;
        this.setOnDismissListener(mDismissListener);
        darkBackground(context, 1.0f);
    }

    public void setMenuClickListener(CircleMenuView.onMenuClickListener listener) {
        mCircleMenu.setMenuClickListener(listener);
    }

    private void darkBackground(Activity context, float alpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = alpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    public void dismiss(Activity context) {
        super.dismiss();
        darkBackground(context, 1.0f);
    }
}
