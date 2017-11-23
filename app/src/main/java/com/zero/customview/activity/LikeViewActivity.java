package com.zero.customview.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.zero.customview.R;
import com.zero.customview.anim.AnimatorFactory;
import com.zero.customview.view.ThumbLikeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LikeViewActivity extends AppCompatActivity {

    @BindView(R.id.iv_thumb_add)
    ImageView ivThumbAdd;
    @BindView(R.id.et_thumb_edit)
    EditText etThumbEdit;
    @BindView(R.id.iv_thumb_sub)
    ImageView ivThumbSub;
    private Context mContext;
    @BindView(R.id.thumb_like_view)
    ThumbLikeView thumbLikeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_view);
        ButterKnife.bind(this);

        mContext = this;
        thumbLikeView.setClickListener(new ThumbLikeView.onLikeListener() {
            @Override
            public void onLike(boolean isLiked) {
                Log.d("wumin", "onLike: ");
//                TipUtils.showTip(mContext, getString(R.string.msg_title),
//                        (isLiked?"Like it!":"Dislike it!"));
            }
        });

        etThumbEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    thumbLikeView.setNumber(Integer.valueOf(s.toString()));
                }
            }
        });
    }

    @OnClick({R.id.iv_thumb_add, R.id.iv_thumb_sub})
    public void onViewClicked(View view) {
        AnimatorFactory.getClickScaleAnimtor(view).start();
        switch (view.getId()) {
            case R.id.iv_thumb_add:
                thumbLikeView.setNumber(thumbLikeView.getNumber() + 1);
                break;
            case R.id.iv_thumb_sub:
                thumbLikeView.setNumber(thumbLikeView.getNumber() - 1);
                break;
        }
    }
}
