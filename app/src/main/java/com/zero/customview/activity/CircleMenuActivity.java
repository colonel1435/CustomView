package com.zero.customview.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.PopupWindow;

import com.zero.customview.R;
import com.zero.customview.utils.TipUtils;
import com.zero.customview.utils.ToastUtil;
import com.zero.customview.view.CircleMenuDialog;
import com.zero.customview.view.CircleMenuPopupWindow;
import com.zero.customview.view.CircleMenuView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CircleMenuActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.circle_menu)
    CircleMenuView circleMenu;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_menu);
        ButterKnife.bind(this);

        mContext = this;
        initView();

    }

    private void initView() {
        toolbar.setTitle(getString(R.string.circle_menu));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        circleMenu.setMenuClickListener(new CircleMenuView.onMenuClickListener() {
            @Override
            public void onCenterClick() {
                ToastUtil.showShortToast(mContext,"CENTER CLICKED!");
                showTest1();
            }

            @Override
            public void onTopClick() {
                ToastUtil.showShortToast(mContext,"TOP CLICKED!");
                showTest2();
            }

            @Override
            public void onBottomClick() {
                ToastUtil.showShortToast(mContext,"BOTTOM CLICKED!");
            }

            @Override
            public void onLeftClick() {
                ToastUtil.showShortToast(mContext,"LEFT CLICKED!");
            }

            @Override
            public void onRightClick() {
                ToastUtil.showShortToast(mContext,"RIGHT CLICKED!");
            }
        });
    }

    private void showTest1() {
        final CircleMenuDialog dlg = CircleMenuDialog.newInstance();
        dlg.setOnMenuClickListener(new CircleMenuView.onMenuClickListener() {
            @Override
            public void onCenterClick() {
                dlg.dismiss();
                ToastUtil.showShortToast(mContext, "Center");
            }

            @Override
            public void onTopClick() {
                dlg.dismiss();
                ToastUtil.showShortToast(mContext, "Top");
            }

            @Override
            public void onBottomClick() {
                dlg.dismiss();
                ToastUtil.showShortToast(mContext, "Bottom");
            }

            @Override
            public void onLeftClick() {
                dlg.dismiss();
                ToastUtil.showShortToast(mContext, "left");
            }

            @Override
            public void onRightClick() {
                dlg.dismiss();
                ToastUtil.showShortToast(mContext, "right");
            }
        });
        dlg.show(getFragmentManager(), "menu");
    }

    private void showTest2() {
        final CircleMenuPopupWindow window = new CircleMenuPopupWindow(mContext);
        window.setDismissListener(this, new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                window.dismiss(CircleMenuActivity.this);
            }
        });
        window.setMenuClickListener(new CircleMenuView.onMenuClickListener() {
            @Override
            public void onCenterClick() {
                window.dismiss();
            }

            @Override
            public void onTopClick() {
                window.dismiss();
            }

            @Override
            public void onBottomClick() {
                window.dismiss();
            }

            @Override
            public void onLeftClick() {
                window.dismiss();
            }

            @Override
            public void onRightClick() {
                window.dismiss();
            }
        });
        window.show(this, getWindow().getDecorView());
    }
}
