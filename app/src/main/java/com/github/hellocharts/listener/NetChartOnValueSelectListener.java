package com.github.hellocharts.listener;

import com.github.hellocharts.model.BubbleValue;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/11/9 0009 15:21
 */

public interface NetChartOnValueSelectListener extends OnValueDeselectListener {
    public void onValueSelected(int bubbleIndex, BubbleValue value);
}
