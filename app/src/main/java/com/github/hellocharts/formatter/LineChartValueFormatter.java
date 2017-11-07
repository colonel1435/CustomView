package com.github.hellocharts.formatter;


import com.github.hellocharts.model.PointValue;

public interface LineChartValueFormatter {

    public int formatChartValue(char[] formattedValue, PointValue value);
}
