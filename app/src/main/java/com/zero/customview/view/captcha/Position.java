package com.zero.customview.view.captcha;

/**
 * Description
 *
 * @author : Mr.wuming
 * @email : fusu1435@163.com
 * @date : 2018/2/10 0010 13:53
 */

public class Position {
    private int left;
    private int top;

    public Position(int left, int top) {
        this.left = left;
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public void offset(int x, int y) {
        left += x;
        top += y;
    }

    @Override
    public String toString() {
        return "Position{" +
                "left=" + left +
                ", top=" + top +
                '}';
    }
}
