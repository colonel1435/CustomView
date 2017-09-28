package com.zero.customview.view.bottombar;

import android.content.Context;
import android.widget.ImageView;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/28 0028 10:55
 */

public class BottomTabFactory {
    public enum TabType {NORMAL, MESSAGE, CONTACT, MENU, SETUP}

    public static BottomTab create(Context context, int typeIndex) {
        TabType type = TabType.values()[typeIndex];
        BottomTab tab = null;
        switch (type) {
            case NORMAL:
                tab = new NormTab(context);
                break;
            case MESSAGE:
                tab = new MessageTab(context);
                break;
            case CONTACT:
                tab = new ContactTab(context);
                break;
            case MENU:
                tab = new MenuTab(context);
                break;
            case SETUP:
                tab = new SetupTab(context);
                break;
            default:
                break;
        }

        return tab;
    }
}
