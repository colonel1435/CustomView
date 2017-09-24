package com.zero.customview.view.bottombar;

/**
 * Created by zero on 2017/9/24.
 */

public class BottomItem {
    private String title;
    private int resId;

    public BottomItem(String title, int resId) {
        this.title = title;
        this.resId = resId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
