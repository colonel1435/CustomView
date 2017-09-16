package com.zero.customview.activity;

import android.app.Application;

import com.zero.customview.utils.SharedPreferencesUtils;

/**
 * Created by xy on 15/12/23.
 */
public class GlobalContext extends Application {
    private static GlobalContext context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        SharedPreferencesUtils.config(this);
    }
    public static GlobalContext getInstance() {
        return context;
    }
}
