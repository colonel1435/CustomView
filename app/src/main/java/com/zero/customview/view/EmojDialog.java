package com.zero.customview.view;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.zero.customview.R;

import io.github.rockerhieu.emojicon.EmojiconsFragment;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/15 0015 16:39
 */

public class EmojDialog extends DialogFragment{
    private EmojiconsFragment emojFragment;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.popupwindows_emoji, null);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        emojFragment = EmojiconsFragment.newInstance(false);
        transaction.add(R.id.emoj_icons, emojFragment).commit();

        AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());
        AlertDialog emoj = builder.show();
        emoj.getWindow().setContentView(view);
        emoj.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        emoj.getWindow().setGravity(Gravity.BOTTOM);
        emoj.getWindow().setWindowAnimations(R.style.pop_add_ainm);
        emoj.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        //支持输入法show.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return emoj;
    }

}
