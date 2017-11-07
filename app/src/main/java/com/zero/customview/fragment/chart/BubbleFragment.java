package com.zero.customview.fragment.chart;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.Space;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.github.hellocharts.listener.BubbleChartOnValueSelectListener;
import com.github.hellocharts.model.Axis;
import com.github.hellocharts.model.BubbleChartData;
import com.github.hellocharts.model.BubbleValue;
import com.github.hellocharts.model.ValueShape;
import com.github.hellocharts.model.Viewport;
import com.github.hellocharts.util.ChartUtils;
import com.github.hellocharts.view.BubbleChartView;
import com.zero.customview.R;
import com.zero.customview.utils.DisplayUtils;
import com.zero.customview.utils.TipUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BubbleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BubbleFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final String TAG = this.getClass().getSimpleName() + " @wumin";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.bubble_view)
    BubbleChartView bubbleView;
    Unbinder unbinder;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final int BUBBLES_NUM = 100;

    private BubbleChartData data;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean hasLabels = true;
    private float width;
    private float height;
    public BubbleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BubbleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BubbleFragment newInstance(String param1, String param2) {
        BubbleFragment fragment = new BubbleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bubble, container, false);
        unbinder = ButterKnife.bind(this, view);

        bubbleView.setOnValueTouchListener(new ValueTouchListener());

        generateData();
        return view;
    }

    private void generateData() {

        List<BubbleValue> values = new ArrayList<BubbleValue>();
        Random random = new Random();
        Viewport maxView = bubbleView.getChartRenderer().getMaximumViewport();
        bubbleView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                bubbleView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                width = bubbleView.getMeasuredWidth();
                height = bubbleView.getMeasuredHeight();
            }
        });
        Log.d(TAG, "generateData: widht -> " + width + " height -> " + height);
        for (int i = 0; i < BUBBLES_NUM; ++i) {
            BubbleValue value = new BubbleValue((float)random.nextInt(400),
                    (float)random.nextInt((int)600), 8f);
            value.setColor(ChartUtils.pickColor());
            value.setShape(shape);
            value.setLabel(String.valueOf(i));
            values.add(value);
        }

        data = new BubbleChartData(values);
        data.setHasLabels(hasLabels);
        data.setAxisXBottom(null);
        data.setAxisYLeft(null);
        data.setValueLabelBackgroundEnabled(false);
        bubbleView.setBubbleChartData(data);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private class ValueTouchListener implements BubbleChartOnValueSelectListener {

        @Override
        public void onValueSelected(int bubbleIndex, BubbleValue value) {
            Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }
    }
}
