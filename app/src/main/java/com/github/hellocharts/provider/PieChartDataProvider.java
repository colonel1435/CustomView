package com.github.hellocharts.provider;

import com.github.hellocharts.model.PieChartData;

public interface PieChartDataProvider {

    public PieChartData getPieChartData();

    public void setPieChartData(PieChartData data);

}
