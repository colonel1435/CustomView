package com.github.hellocharts.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.github.hellocharts.listener.ComboLineColumnChartOnValueSelectListener;
import com.github.hellocharts.listener.DummyCompoLineColumnChartOnValueSelectListener;
import com.github.hellocharts.model.BuildConfig;
import com.github.hellocharts.model.ChartData;
import com.github.hellocharts.model.ColumnChartData;
import com.github.hellocharts.model.ComboLineColumnChartData;
import com.github.hellocharts.model.LineChartData;
import com.github.hellocharts.model.PointValue;
import com.github.hellocharts.model.SelectedValue;
import com.github.hellocharts.model.SubcolumnValue;
import com.github.hellocharts.provider.ColumnChartDataProvider;
import com.github.hellocharts.provider.ComboLineColumnChartDataProvider;
import com.github.hellocharts.provider.LineChartDataProvider;
import com.github.hellocharts.renderer.ColumnChartRenderer;
import com.github.hellocharts.renderer.ComboLineColumnChartRenderer;
import com.github.hellocharts.renderer.LineChartRenderer;


/**
 * ComboChart, supports ColumnChart combined with LineChart. Lines are always drawn on top.
 *
 * @author Leszek Wach
 */
public class ComboLineColumnChartView extends AbstractChartView implements ComboLineColumnChartDataProvider {
    private static final String TAG = "ComboLineColumnChartView";
    protected ComboLineColumnChartData data;
    protected ColumnChartDataProvider columnChartDataProvider = new ComboColumnChartDataProvider();
    protected LineChartDataProvider lineChartDataProvider = new ComboLineChartDataProvider();
    protected ComboLineColumnChartOnValueSelectListener onValueTouchListener = new
            DummyCompoLineColumnChartOnValueSelectListener();

    public ComboLineColumnChartView(Context context) {
        this(context, null, 0);
    }

    public ComboLineColumnChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ComboLineColumnChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setChartRenderer(new ComboLineColumnChartRenderer(context, this, columnChartDataProvider,
                lineChartDataProvider));
        setComboLineColumnChartData(ComboLineColumnChartData.generateDummyData());
    }

    @Override
    public ComboLineColumnChartData getComboLineColumnChartData() {
        return data;
    }

    @Override
    public void setComboLineColumnChartData(ComboLineColumnChartData data) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Setting data for ComboLineColumnChartView");
        }

        if (null == data) {
            this.data = null;// generateDummyData();
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

            if (SelectedValue.SelectedValueType.COLUMN.equals(selectedValue.getType())) {

                SubcolumnValue value = data.getColumnChartData().getColumns().get(selectedValue.getFirstIndex())
                        .getValues().get(selectedValue.getSecondIndex());
                onValueTouchListener.onColumnValueSelected(selectedValue.getFirstIndex(),
                        selectedValue.getSecondIndex(), value);

            } else if (SelectedValue.SelectedValueType.LINE.equals(selectedValue.getType())) {

                PointValue value = data.getLineChartData().getLines().get(selectedValue.getFirstIndex()).getValues()
                        .get(selectedValue.getSecondIndex());
                onValueTouchListener.onPointValueSelected(selectedValue.getFirstIndex(), selectedValue.getSecondIndex(),
                        value);

            } else {
                throw new IllegalArgumentException("Invalid selected value type " + selectedValue.getType().name());
            }
        } else {
            onValueTouchListener.onValueDeselected();
        }
    }

    public ComboLineColumnChartOnValueSelectListener getOnValueTouchListener() {
        return onValueTouchListener;
    }

    public void setOnValueTouchListener(ComboLineColumnChartOnValueSelectListener touchListener) {
        if (null != touchListener) {
            this.onValueTouchListener = touchListener;
        }
    }

    public void setColumnChartRenderer(Context context, ColumnChartRenderer columnChartRenderer){
        setChartRenderer(new ComboLineColumnChartRenderer(context, this , columnChartRenderer, lineChartDataProvider));
    }

    public void setLineChartRenderer(Context context, LineChartRenderer lineChartRenderer){
        setChartRenderer(new ComboLineColumnChartRenderer(context, this, columnChartDataProvider, lineChartRenderer));
    }

    private class ComboLineChartDataProvider implements LineChartDataProvider {

        @Override
        public LineChartData getLineChartData() {
            return ComboLineColumnChartView.this.data.getLineChartData();
        }

        @Override
        public void setLineChartData(LineChartData data) {
            ComboLineColumnChartView.this.data.setLineChartData(data);

        }

    }

    private class ComboColumnChartDataProvider implements ColumnChartDataProvider {

        @Override
        public ColumnChartData getColumnChartData() {
            return ComboLineColumnChartView.this.data.getColumnChartData();
        }

        @Override
        public void setColumnChartData(ColumnChartData data) {
            ComboLineColumnChartView.this.data.setColumnChartData(data);

        }

    }

}
