package com.github.hellocharts.listener;


import com.github.hellocharts.model.SliceValue;

public interface PieChartOnValueSelectListener extends OnValueDeselectListener {

    public void onValueSelected(int arcIndex, SliceValue value);

}
