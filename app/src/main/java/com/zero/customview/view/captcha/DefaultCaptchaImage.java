package com.zero.customview.view.captcha;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.Random;

/**
 * Description
 *
 * @author : Mr.wuming
 * @email : fusu1435@163.com
 * @date : 2018/2/10 0010 13:57
 */

public class DefaultCaptchaImage implements CaptchaImageCall {
    private Context mContext;
    public DefaultCaptchaImage(Context context) {
        mContext = context;
    }

    @Override
    public Path onShape(int blockSize) {
        Path path = new Path();
        path.addCircle(blockSize/2, blockSize/2, blockSize/2, Path.Direction.CW);
        return path;
    }

    @Override
    public Position onPosition(int size, int width, int height) {
        Random random = new Random();
        int left = random.nextInt(width - size);
        if (left < size) {
            left = size;
        }

        int top = random.nextInt(height - size);
        if (top < size) {
            top = size;
        }
        return new Position(left, top);
    }

    @Override
    public void onDecoration(Canvas canvas, Path path) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(4);
        paint.setPathEffect(new DashPathEffect(new float[]{10,10}, 5));
        canvas.drawPath(path, paint);
    }
}
