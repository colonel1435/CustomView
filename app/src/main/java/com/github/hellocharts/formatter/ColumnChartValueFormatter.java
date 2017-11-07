package com.github.hellocharts.formatter;

import com.github.hellocharts.model.SubcolumnValue;

public interface ColumnChartValueFormatter {

    public int formatChartValue(char[] formattedValue, SubcolumnValue value);

}
