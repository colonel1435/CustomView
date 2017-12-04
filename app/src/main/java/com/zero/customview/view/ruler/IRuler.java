package com.zero.customview.view.ruler;

import android.graphics.Canvas;

/**
 * Description
 * @author : Mr.wuming
 * @email  : fusu1435@163.com
 * @date   : 2017/12/4 0004 11:53
 */

public interface IRuler {
    /**
     *  Draw top & bottom border of ruler
     *  @param  canvas
     *  @return void
     */
    void drawBorder(Canvas canvas);
    /**
     *  Draw scale line
     *  @param canvas
     *  @return void
     */
    void drawScale(Canvas canvas);
    /**
     *  Draw current scale line
     *  @param canvas
     *  @return void
     */
    void drawCurrentLine(Canvas canvas);
    /**
    *   Draw edge effort
    *  @param canvas
    *  @return void
    */
    void drawEdge(Canvas canvas);
    /**
     *  Start scroll ruler
     *  @param distX
     *  @param distY
     *  @return void
     */
    void startScroll(float distX, float distY);
    /**
     *  String fling ruler
     *  @param velocityX
     *  @param  velocityY
     *  @return void
     */
    void startFling(float velocityX, float velocityY);
    /**
     *  Scroll to the nearest scale line
     *  @param
     *  @return void
     */
    void scrollToNearest();
    /**
     *  Change scale number to scale position
     *  @param scale
     *  @return int
     */
    int scaleToPosition(float scale);
    /**
     *  Change current number when ruler is scrolling
     *  @param
     *  @return
     */
    void scrollNumber();
    /**
    *   Start drag min scale line edge efforts
    * @param
    * @return
    */
    void startMinEdge(int position);
    /**
     *   Start drag max scale line edge efforts
     * @param
     * @return
     */
    void startMaxEdge(int position);
}
