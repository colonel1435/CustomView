package com.github.hellocharts.formatter;

import com.github.hellocharts.model.BubbleValue;

public interface BubbleChartValueFormatter {

    public int formatChartValue(char[] formattedValue, BubbleValue value);
}
