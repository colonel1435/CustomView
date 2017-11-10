package com.github.hellocharts.formatter;

import com.github.hellocharts.model.BubbleValue;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/11/9 0009 15:16
 */

public interface NetChartValueFormatter {
    public int formatChartValue(char[] formattedValue, BubbleValue value);
}
