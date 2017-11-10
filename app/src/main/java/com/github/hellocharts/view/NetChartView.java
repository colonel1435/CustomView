package com.github.hellocharts.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;

import com.github.hellocharts.listener.BubbleChartOnValueSelectListener;
import com.github.hellocharts.listener.DummyNetChartOnValueSelectListener;
import com.github.hellocharts.listener.NetChartOnValueSelectListener;
import com.github.hellocharts.model.BubbleValue;
import com.github.hellocharts.model.BuildConfig;
import com.github.hellocharts.model.ChartData;
import com.github.hellocharts.model.NetChartData;
import com.github.hellocharts.model.SelectedValue;
import com.github.hellocharts.provider.NetChartDataProvider;
import com.github.hellocharts.renderer.NetChartRenderer;

/**
 * BubbleChart, supports circle bubbles and square bubbles.
 *
 * @author lecho
 */
public class NetChartView extends AbstractChartView implements NetChartDataProvider {
    private static final String TAG = "BubbleChartView";
    protected NetChartData data;
    protected NetChartOnValueSelectListener onValueTouchListener = new DummyNetChartOnValueSelectListener();

    protected NetChartRenderer netChartRenderer;

    public NetChartView(Context context) {
        this(context, null, 0);
    }

    public NetChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        netChartRenderer = new NetChartRenderer(context, this, this);
        setChartRenderer(netChartRenderer);
        setBubbleChartData(NetChartData.generateDummyData());
    }

    @Override
    public NetChartData getBubbleChartData() {
        return data;
    }

    @Override
    public void setBubbleChartData(NetChartData data) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Setting data for BubbleChartView");
        }

        if (null == data || 0 == data.getValues().size()) {
            this.data = NetChartData.generateDummyData();
        } else {
            this.data = data;
        }

        super.onChartDataChange();
    }

    @Override
    public ChartData getChartData() {
        return data;
    }

    @Override
    public void callTouchListener() {
        SelectedValue selectedValue = chartRenderer.getSelectedValue();

        if (selectedValue.isSet()) {
            BubbleValue value = data.getValues().get(selectedValue.getFirstIndex());
            onValueTouchListener.onValueSelected(selectedValue.getFirstIndex(), value);
        } else {
            onValueTouchListener.onValueDeselected();
        }
    }

    public NetChartOnValueSelectListener getOnValueTouchListener() {
        return onValueTouchListener;
    }

    public void setOnValueTouchListener(NetChartOnValueSelectListener touchListener) {
        if (null != touchListener) {
            this.onValueTouchListener = touchListener;
        }
    }

    /**
     * Removes empty spaces, top-bottom for portrait orientation and left-right for landscape. This method has to be
     * called after view View#onSizeChanged() method is called and chart data is set. This method may be inaccurate.
     *
     * @see NetChartRenderer#removeMargins()
     */
    public void removeMargins() {
        netChartRenderer.removeMargins();
        ViewCompat.postInvalidateOnAnimation(this);
    }
}
