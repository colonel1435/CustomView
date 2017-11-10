package com.github.hellocharts.formatter;

import com.github.hellocharts.model.BubbleValue;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/11/9 0009 15:18
 */

public class SimpleNetChartValueFormatter implements NetChartValueFormatter {
    private ValueFormatterHelper valueFormatterHelper = new ValueFormatterHelper();

    public SimpleNetChartValueFormatter() {
        valueFormatterHelper.determineDecimalSeparator();
    }

    public SimpleNetChartValueFormatter(int decimalDigitsNumber) {
        this();
        valueFormatterHelper.setDecimalDigitsNumber(decimalDigitsNumber);
    }

    @Override
    public int formatChartValue(char[] formattedValue, BubbleValue value) {
        return valueFormatterHelper.formatFloatValueWithPrependedAndAppendedText(formattedValue, value.getZ(), value
                .getLabelAsChars());
    }

    public int getDecimalDigitsNumber() {
        return valueFormatterHelper.getDecimalDigitsNumber();
    }

    public SimpleNetChartValueFormatter setDecimalDigitsNumber(int decimalDigitsNumber) {
        valueFormatterHelper.setDecimalDigitsNumber(decimalDigitsNumber);
        return this;
    }

    public char[] getAppendedText() {
        return valueFormatterHelper.getAppendedText();
    }

    public SimpleNetChartValueFormatter setAppendedText(char[] appendedText) {
        valueFormatterHelper.setAppendedText(appendedText);
        return this;
    }

    public char[] getPrependedText() {
        return valueFormatterHelper.getPrependedText();
    }

    public SimpleNetChartValueFormatter setPrependedText(char[] prependedText) {
        valueFormatterHelper.setPrependedText(prependedText);
        return this;
    }

    public char getDecimalSeparator() {
        return valueFormatterHelper.getDecimalSeparator();
    }

    public SimpleNetChartValueFormatter setDecimalSeparator(char decimalSeparator) {
        valueFormatterHelper.setDecimalSeparator(decimalSeparator);
        return this;
    }
}
