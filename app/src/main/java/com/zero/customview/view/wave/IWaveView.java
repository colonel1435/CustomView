package com.zero.customview.view.wave;

import android.animation.Animator;
import android.graphics.Canvas;

/**
 * Description
 *
 * @author : Mr.wuming
 * @email : fusu1435@163.com
 * @date : 2017/12/11 0011 11:14
 */

public interface IWaveView {
    /**
    *   Draw facade
    * @param canvas
    * @return
    */
    void drawFacade(Canvas canvas);
    /**
    *   Draw text
    * @param canvas
    * @return
    */
    void drawText(Canvas canvas);
    /**
    *   Start wave animation
    * @param animator
    * @return
    */
    void startAnimator(Animator animator);
}
