package com.zero.customview.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.zero.customview.R;
import com.zero.customview.utils.TipUtils;
import com.zero.customview.view.ThumbLikeView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LikeViewActivity extends AppCompatActivity {

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
                TipUtils.showTip(mContext, getString(R.string.msg_title),
                        (isLiked?"Like it!":"Dislike it!"));
            }
        });
    }
}
