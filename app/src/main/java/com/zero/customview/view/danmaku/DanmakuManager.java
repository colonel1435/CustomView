package com.zero.customview.view.danmaku;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;

import com.zero.customview.R;
import com.zero.customview.utils.DisplayUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/14 0014 17:37
 */

public class DanmakuManager {
    private final String TAG = getClass().getSimpleName() + "@wumin";

    private int   BITMAP_WIDTH    = 30;
    private int   BITMAP_HEIGHT   = 30;
    private float DANMU_TEXT_SIZE = 16f;
    private int DANMU_PADDING       = 8;

    private Context mContext;
    private DanmakuView mDanmakuView;
    private DanmakuContext mDanmakuContext;

    public DanmakuManager() {
    }

    public DanmakuManager(Context context) {
        this.mContext = context;
        setSize(context);
        initDanmuConfig();
    }

    private void setSize(Context context) {
        BITMAP_WIDTH = DisplayUtils.dip2px(context, BITMAP_HEIGHT);
        BITMAP_HEIGHT = DisplayUtils.dip2px(context, BITMAP_HEIGHT);
        DANMU_PADDING = DisplayUtils.dip2px(context, DANMU_PADDING);
        DANMU_TEXT_SIZE = DisplayUtils.sp2px(context, DANMU_TEXT_SIZE);
    }

    private void initDanmuConfig() {
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 2);
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        mDanmakuContext = DanmakuContext.create();
        mDanmakuContext
                .setDanmakuStyle(IDisplayer.DANMAKU_STYLE_NONE)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f)
                .setScaleTextSize(1.2f)
                .setCacheStuffer(new BackgroundCacheStuffer(mContext), mCacheStufferAdapter)
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);
    }

    private BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {

        @Override
        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {

        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
            if (danmaku.text instanceof Spanned) {
                danmaku.text = "";
            }
        }
    };

    public void setDanmakuView(DanmakuView danmakuView) {
        this.mDanmakuView = danmakuView;
        initDanmuView();
    }

    private void initDanmuView() {
        if (mDanmakuView != null) {
            mDanmakuView.setCallback(new DrawHandler.Callback() {
                @Override
                public void prepared() {
                    mDanmakuView.start();
                    Log.d(TAG, "prepared: ");
                }

                @Override
                public void updateTimer(DanmakuTimer timer) {
                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {
                    Log.d(TAG, "danmakuShown: ");
                }

                @Override
                public void drawingFinished() {
                    Log.d(TAG, "drawingFinished: ");
                }
            });
        }

        mDanmakuView.prepare(new BaseDanmakuParser() {

            @Override
            protected Danmakus parse() {
                return new Danmakus();
            }
        }, mDanmakuContext);
        mDanmakuView.enableDanmakuDrawingCache(true);
    }

    public void pause() {
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    public void hide() {
        if (mDanmakuView != null) {
            mDanmakuView.hide();
        }
    }

    public void show() {
        if (mDanmakuView != null) {
            mDanmakuView.show();
        }
    }

    public void resume() {
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    public void destroy() {
        if (mDanmakuView != null) {
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }

    public void addDanmuList(final List<DanmakuMsg> danmukus) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(DanmakuMsg msg : danmukus) {
                    addDanmu(msg);
                }
            }
        }).start();
    }

    public void addDanmu(DanmakuMsg msg) {
        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);

        if (danmaku == null || mDanmakuView == null) {
            return;
        }

        danmaku.isGuest = msg.getUserType() == 0 ? true:false;
        danmaku.userId = msg.getUserId();

        SpannableStringBuilder spannable;
        Bitmap bitmap = getDefaultBitmap(msg.getIconId(), null);
        CircleDrawable circleDrawable = new CircleDrawable(mContext, bitmap, danmaku.isGuest);
        circleDrawable.setBounds(0, 0, BITMAP_WIDTH, BITMAP_HEIGHT);
        if (!danmaku.isGuest) {
            spannable = createSpannable(circleDrawable, msg.getMsg());
        } else {
            spannable = createSpannable(circleDrawable, "");
        }
        danmaku.text = spannable;

        danmaku.padding = DANMU_PADDING;
        danmaku.priority = 0;
        danmaku.isLive = false;
        danmaku.textSize = DANMU_TEXT_SIZE;
        danmaku.textColor = Color.WHITE;
        danmaku.textShadowColor = 0;
        danmaku.setTime(mDanmakuView.getCurrentTime());
        mDanmakuView.addDanmaku(danmaku);
        Log.d(TAG, "addDanmu: " + msg.toString());
    }

    private Bitmap getDefaultBitmap(int drawableId, String drawblePath) {
        Bitmap mDefauleBitmap = null;
        Bitmap bitmap;
        if (drawableId != 0) {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), drawableId);
        } else {
            bitmap = BitmapFactory.decodeFile(drawblePath);
        }
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.postScale(((float) BITMAP_WIDTH) / width, ((float) BITMAP_HEIGHT) / height);
            mDefauleBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        }
        return mDefauleBitmap;
    }

    private SpannableStringBuilder createSpannable(Drawable drawable, String content) {
        String text = "bitmap";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        CenteredImageSpan span = new CenteredImageSpan(drawable);
        spannableStringBuilder.setSpan(span, 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        if (!TextUtils.isEmpty(content)) {
            spannableStringBuilder.append(" ");
            spannableStringBuilder.append(content.trim());
        }
        return spannableStringBuilder;
    }
}
