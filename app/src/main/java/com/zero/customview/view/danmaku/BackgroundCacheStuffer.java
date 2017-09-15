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
import com.zero.customview.utils.DisplayUtils;

import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/14 0014 17:08
 */

public class BackgroundCacheStuffer extends SpannedCacheStuffer{
    private final int DANMUKU_PADDING_INNER = 8;
    private final int DANMUKU_RADIUS = 12;
    final Paint paint = new Paint();
    private Context mContext;

    public BackgroundCacheStuffer() {
    }

    public BackgroundCacheStuffer(Context context) {
            mContext = context;
        }

    @Override
    public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {
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
        int paddingInner = DisplayUtils.dip2px(mContext, DANMUKU_PADDING_INNER);
        int radius = DisplayUtils.dip2px(mContext, DANMUKU_RADIUS);
        canvas.drawRoundRect(new RectF(left + paddingInner, top + paddingInner
                        , left + danmaku.paintWidth - paddingInner + 6,
                        top + danmaku.paintHeight - paddingInner + 6),
                radius, radius, paint);
    }

    @Override
    public void drawStroke(BaseDanmaku danmaku, String lineText, Canvas canvas, float left, float top, Paint paint) {
    }
}
