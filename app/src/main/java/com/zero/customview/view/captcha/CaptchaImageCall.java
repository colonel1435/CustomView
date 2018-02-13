package com.zero.customview.view.captcha;

import android.graphics.Canvas;
import android.graphics.Path;

/**
 * Description
 *
 * @author : Mr.wuming
 * @email : fusu1435@163.com
 * @date : 2018/2/7 0007 16:23
 */

public interface CaptchaImageCall {
    /**
    * Return captcha image shape
    * @param blockSize
    * @return
    */
    Path onShape(int blockSize);

    /**
    * Return captcha image position
    * @param width
     * @param height
    * @return
    */
    Position onPosition(int size, int width, int height);
    /**
    *   Decorate captcha image
    * @param canvas
     * @param path
    * @return
    */
    void onDecoration(Canvas canvas, Path path);
}
