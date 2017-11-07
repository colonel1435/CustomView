package com.github.hellocharts.formatter;

import com.github.hellocharts.model.SliceValue;

public interface PieChartValueFormatter {

    public int formatChartValue(char[] formattedValue, SliceValue value);
}
