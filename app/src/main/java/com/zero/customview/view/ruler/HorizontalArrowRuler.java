package com.zero.customview.view.ruler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Description
 *
 * @author : Mr.wuming
 * @email : fusu1435@163.com
 * @date : 2017/12/4 0004 15:20
 */

public class HorizontalArrowRuler extends HorizontalRuler {
    private float arrowHeight;
    private Path arrowPath;
    public HorizontalArrowRuler(Context context) {
        super(context);
    }

    public HorizontalArrowRuler(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalArrowRuler(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initRuler() {
        super.initRuler();
        arrowPath = new Path();
    }

    @Override
    public void drawCurrentLine(Canvas canvas) {
        arrowHeight = 0.5f * mScaleLineHeight;
        float center = (mLeft + getScrollX() + mRight + getScrollX()) / 2;
        arrowPath.reset();
        arrowPath.moveTo(center - arrowHeight, mTop);
        arrowPath.lineTo(center + arrowHeight, mTop);
        arrowPath.lineTo(center, mTop + arrowHeight);
        arrowPath.close();
        canvas.drawPath(arrowPath, mCurrentPaint);
    }
}
