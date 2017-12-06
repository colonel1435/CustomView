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
 * @date : 2017/12/6 0006 15:26
 */

public class VerticalArrowRuler extends VerticalRuler {
    private float arrowHeight;
    private Path arrowPath;
    public VerticalArrowRuler(Context context) {
        this(context, null);
    }

    public VerticalArrowRuler(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalArrowRuler(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        float center = (mTop + getScrollY() + mBottom  + getScrollY()) / 2;
        arrowPath.reset();
        arrowPath.moveTo(mLeft, center - arrowHeight);
        arrowPath.lineTo(mLeft, center + arrowHeight);
        arrowPath.lineTo(mLeft + arrowHeight, center);
        arrowPath.close();
        canvas.drawPath(arrowPath, mCurrentPaint);
    }
}
