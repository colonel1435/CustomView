package com.zero.customview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;
import com.zero.customview.R;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/7/14 0014 17:54
 */

class ClockView extends View {

    Paint paint;

    public ClockView(Context context) {
        super(context);
        paint = new Paint(); //设置一个笔刷大小是3的黄色的画笔
        paint.setColor(Color.WHITE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(getResources().getColor(R.color.colorDarkBlue));
        // Out Circle
        float radius = canvas.getWidth()/4;
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        canvas.translate(canvas.getWidth()/2, canvas.getHeight()/3);
        canvas.drawCircle(0, 0, radius, paint);

        // Path text
        canvas.save();
        canvas.translate(0, (-radius)+60);
        Path path = new Path();
        path.addArc(new RectF(-60, -20, 60, 20), -180, 180);
        Paint citePaint = new Paint(paint);
        citePaint.setTextSize(24);
        citePaint.setStrokeWidth(1);
        canvas.drawTextOnPath("LZSUNLAND", path, 5, 0, citePaint);
        canvas.restore();

        // scale
        Paint tmpPaint = new Paint(paint);
        tmpPaint.setStrokeWidth(2);
        int count = 60;
        for(int i = 1; i <= count; i++) {
            canvas.rotate(360/count, 0, 0);
            if (i % 5 == 0) {
                canvas.drawLine(0f, -radius, 0, -radius-12f, paint);
                canvas.drawText(String.valueOf(i/5), -8f, -radius-25f, citePaint);
            } else {
                canvas.drawLine(0f, -radius, 0f, -radius - 6f, tmpPaint);
            }
        }
        // point
        tmpPaint.setColor(Color.GRAY);
        tmpPaint.setStrokeWidth(4);
        canvas.drawCircle(0, 0, 10, tmpPaint);

        tmpPaint.setStyle(Paint.Style.FILL);
        tmpPaint.setColor(Color.BLACK);
        canvas.drawCircle(0, 0, 10, tmpPaint);

        paint.setStrokeWidth(6);
        canvas.drawLine(0, 20, 0, -radius+65, paint);
    }

}
