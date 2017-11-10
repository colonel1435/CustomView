package com.github.hellocharts.model;

import com.github.hellocharts.formatter.NetChartValueFormatter;
import com.github.hellocharts.formatter.SimpleNetChartValueFormatter;
import com.github.hellocharts.view.Chart;

import java.util.ArrayList;
import java.util.List;

/**
 * Data for BubbleChart.
 */
public class NetChartData extends AbstractChartData {
    public static final int DEFAULT_MIN_BUBBLE_RADIUS_DP = 2;
    public static final int DEFAULT_MAX_BUBBLE_RADIUS_DP = 8;
    public static final float DEFAULT_BUBBLE_SCALE = 0.5f;
    private NetChartValueFormatter formatter = new SimpleNetChartValueFormatter();
    private boolean hasLabels = false;
    private boolean hasLabelsOnlyForSelected = false;
    private boolean hasLines = false;
    private int maxBubbleRadius = DEFAULT_MAX_BUBBLE_RADIUS_DP;
    private int minBubbleRadius = DEFAULT_MIN_BUBBLE_RADIUS_DP;
    private float bubbleScale = DEFAULT_BUBBLE_SCALE;
    // TODO: consider Collections.emptyList()
    private List<BubbleValue> values = new ArrayList<BubbleValue>();

    public NetChartData() {
    }

    public NetChartData(List<BubbleValue> values) {
        setValues(values);
    }

    /**
     * Copy constructor for deep copy.
     */
    public NetChartData(NetChartData data) {
        super(data);
        this.formatter = data.formatter;
        this.hasLabels = data.hasLabels;
        this.hasLabelsOnlyForSelected = data.hasLabelsOnlyForSelected;
        this.minBubbleRadius = data.minBubbleRadius;
        this.bubbleScale = data.bubbleScale;

        for (BubbleValue bubbleValue : data.getValues()) {
            this.values.add(new BubbleValue(bubbleValue));
        }
    }

    public static NetChartData generateDummyData() {
        final int numValues = 4;
        final int numMax = 12;
        final float faceRadius = 120;
        final float circleRadius = 3.14f;
        NetChartData data = new NetChartData();
        List<BubbleValue> values = new ArrayList<BubbleValue>(numValues);
        /***    Draw smile face     ***/
        for (int i = 0; i < numMax; i++) {
            float x = (float)(faceRadius * Math.cos(i * Math.PI / 6));
            float y = (float)(faceRadius * Math.sin(i * Math.PI / 6));
            values.add(new BubbleValue(x, y, circleRadius).setLabel(String .valueOf(i)));

            if (i == 8 || i == 9 || i == 10) {
                values.add(new BubbleValue(
                        (float) (0.5 * faceRadius * Math.cos(i * Math.PI / 6)),
                        (float) (0.5 * faceRadius * Math.sin(i * Math.PI / 6)),
                        circleRadius
                ).setLabel(String.valueOf(i)));
            }
        }

        values.add(new BubbleValue(
                (float) (0.5 * faceRadius * Math.cos(Math.PI / 4)),
                (float) (0.5 * faceRadius * Math.sin(Math.PI / 4)),
                2 * circleRadius
        ).setLabel(""));
        values.add(new BubbleValue(
                (float) (0.5 * faceRadius * Math.cos(3 *Math.PI / 4)),
                (float) (0.5 * faceRadius * Math.sin(3 * Math.PI / 4)),
                2 * circleRadius
        ).setLabel(""));

        data.setValues(values);
        return data;
    }

    @Override
    public void update(float scale) {
        for (BubbleValue value : values) {
            value.update(scale);
        }
    }

    @Override
    public void finish() {
        for (BubbleValue value : values) {
            value.finish();
        }
    }

    public List<BubbleValue> getValues() {
        return values;
    }

    public NetChartData setValues(List<BubbleValue> values) {
        if (null == values) {
            this.values = new ArrayList<BubbleValue>();
        } else {
            this.values = values;
        }
        return this;
    }

    public boolean hasLines() {
        return hasLines;
    }

    public void setHasLines(boolean hasLines) {
        this.hasLines = hasLines;
    }

    public boolean hasLabels() {
        return hasLabels;
    }

    public NetChartData setHasLabels(boolean hasLabels) {
        this.hasLabels = hasLabels;
        if (hasLabels) {
            hasLabelsOnlyForSelected = false;
        }
        return this;
    }

    /**
     * @see #setHasLabelsOnlyForSelected(boolean)
     */
    public boolean hasLabelsOnlyForSelected() {
        return hasLabelsOnlyForSelected;
    }

    /**
     * Set true if you want to show value labels only for selected value, works best when chart has
     * isValueSelectionEnabled set to true {@link Chart#setValueSelectionEnabled(boolean)}.
     */
    public NetChartData setHasLabelsOnlyForSelected(boolean hasLabelsOnlyForSelected) {
        this.hasLabelsOnlyForSelected = hasLabelsOnlyForSelected;
        if (hasLabelsOnlyForSelected) {
            this.hasLabels = false;
        }
        return this;
    }

    public int getMaxBubbleRadius() {
        return maxBubbleRadius;
    }
    /**
     * Returns minimal bubble radius in dp.
     *
     * @see #setMinBubbleRadius(int)
     */
    public int getMinBubbleRadius() {
        return minBubbleRadius;
    }

    /**
     * Set minimal bubble radius in dp, helpful when you want small bubbles(bubbles with very small z values compared to
     * other bubbles) to be visible on chart, default 6dp
     */
    public void setMinBubbleRadius(int minBubbleRadius) {
        this.minBubbleRadius = minBubbleRadius;
    }

    /**
     * Returns bubble scale which is used to adjust bubble size.
     *
     * @see #setBubbleScale(float)
     */
    public float getBubbleScale() {
        return bubbleScale;
    }

    /**
     * Set bubble scale which is used to adjust bubble size. If you want smaller bubbles set scale {@code <0, 1>},
     * if you want bigger bubbles set scale greater than 1, default is 1.0f.
     */
    public void setBubbleScale(float bubbleScale) {
        this.bubbleScale = bubbleScale;
    }

    public NetChartValueFormatter getFormatter() {
        return formatter;
    }

    public NetChartData setFormatter(NetChartValueFormatter formatter) {
        if (null != formatter) {
            this.formatter = formatter;
        }
        return this;
    }
}
