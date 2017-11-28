package com.zero.customview.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.zero.customview.R;
import com.zero.customview.anim.AnimatorFactory;
import com.zero.customview.view.likeview.ShiftNumberView;
import com.zero.customview.view.likeview.ThumbLikeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LikeViewActivity extends AppCompatActivity {

    private static final String TAG = "LikeViewActivity@wuming";
    @BindView(R.id.iv_thumb_add)
    ImageView ivThumbAdd;
    @BindView(R.id.et_thumb_edit)
    EditText etThumbEdit;
    @BindView(R.id.iv_thumb_sub)
    ImageView ivThumbSub;
    @BindView(R.id.thumb_like)
    ThumbLikeView thumbLike;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_view);
        ButterKnife.bind(this);

        mContext = this;
        thumbLike.setLikeListener(new ThumbLikeView.OnLikeListener() {
            @Override
            public void onLike(boolean isLiked) {
                Log.d(TAG, "onLike: " + (isLiked ? "Like it!" : "Leave it!"));
            }
        });
    }

    @OnClick({R.id.iv_thumb_add, R.id.iv_thumb_sub})
    public void onViewClicked(View view) {
        AnimatorFactory.getClickScaleAnimtor(view).start();
        int number = thumbLike.getNumber();
        String input = etThumbEdit.getText().toString();
        switch (view.getId()) {
            case R.id.iv_thumb_add:
                if (!"".equals(input)) {
                    etThumbEdit.setText("");
                    thumbLike.setNumber(Integer.valueOf(input));
                } else {
                    thumbLike.setNumber(number + 1);
                }
                break;
            case R.id.iv_thumb_sub:
                thumbLike.setNumber(number - 1);
                break;
            default:
                break;
        }
    }
}
