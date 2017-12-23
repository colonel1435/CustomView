package com.zero.customview.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import com.zero.customview.R;

/**
 * Description
 *
 * @author : Mr.wuming
 * @email : fusu1435@163.com
 * @date : 2017/12/22 0022 10:16
 */

public class CircleMenuDialog extends DialogFragment {
    private CircleMenuView circleMenu;
    private CircleMenuView.onMenuClickListener menuClickListener;
    public static CircleMenuDialog newInstance() {
        CircleMenuDialog fragment = new CircleMenuDialog();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ResourceType")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dlg_circle_menu, null);
        circleMenu = (CircleMenuView) view.findViewById(R.id.circle_menu);
        circleMenu.setMenuClickListener(menuClickListener);
        Dialog dlg = new AlertDialog.Builder(getActivity()).setView(view)
                .create();
        dlg.setCanceledOnTouchOutside(false);
        dlg.getWindow().setWindowAnimations(R.anim.fade_in);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dlg.setCancelable(true);
        return dlg;
    }

    public void setOnMenuClickListener(CircleMenuView.onMenuClickListener listener) {
        menuClickListener = listener;
    }
}
