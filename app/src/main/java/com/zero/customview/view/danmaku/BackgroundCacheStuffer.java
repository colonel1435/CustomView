package com.zero.customview.view.danmaku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.widget.MultiAutoCompleteTextView;

import com.zero.customview.R;

import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/14 0014 17:08
 */

public class BackgroundCacheStuffer extends SpannedCacheStuffer{
    private final int DANMU_PADDING_INNER = 5;
    private final int DANMU_RADIUS = 3;
    final Paint paint = new Paint();
    private Context mContext;

    public BackgroundCacheStuffer() {
    }

    public BackgroundCacheStuffer(Context context) {
            mContext = context;
        }

    @Override
    public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {
//            danmaku.padding = 20;  // 在背景绘制模式下增加padding
        super.measure(danmaku, paint, fromWorkerThread);
    }

    @Override
    public void drawBackground(BaseDanmaku danmaku, Canvas canvas, float left, float top) {
        paint.setAntiAlias(true);
        if (!danmaku.isGuest && danmaku.userId == 1) {
            paint.setColor(ContextCompat.getColor(mContext, R.color.colorCadeBlue));
        } else if (!danmaku.isGuest && danmaku.userId == 2) {
            paint.setColor(ContextCompat.getColor(mContext, R.color.colorDeepOrange));
        } else {
            paint.setColor(Color.BLACK);
        }
        if (danmaku.isGuest) {
            paint.setColor(Color.TRANSPARENT);
        }
        canvas.drawRoundRect(new RectF(left + DANMU_PADDING_INNER, top + DANMU_PADDING_INNER
                        , left + danmaku.paintWidth - DANMU_PADDING_INNER + 6,
                        top + danmaku.paintHeight - DANMU_PADDING_INNER + 6),
                DANMU_RADIUS, DANMU_RADIUS, paint);
    }

    @Override
    public void drawStroke(BaseDanmaku danmaku, String lineText, Canvas canvas, float left, float top, Paint paint) {
    }
}
