package com.github.hellocharts.provider;

import com.github.hellocharts.model.NetChartData;

public interface NetChartDataProvider {

    public NetChartData getBubbleChartData();

    public void setBubbleChartData(NetChartData data);

}
