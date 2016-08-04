package cn.yinxun.boshixuan.fragment;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.jjoe64.graphview.CustomLabelFormatter;
import cn.yinxun.boshixuan.jjoe64.graphview.GraphView;
import cn.yinxun.boshixuan.jjoe64.graphview.GraphViewSeries;
import cn.yinxun.boshixuan.jjoe64.graphview.LineGraphView;
import cn.yinxun.boshixuan.util.DensityUtil;

/**
 * Created by Administrator on 2016/7/17 0017.
 */
public class CurrentFragment extends BaseFragment{
    private View mView;
    @Bind(R.id.graph)
    LinearLayout layout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_current, container, false);
        ButterKnife.bind(this, this.mView);
        return this.mView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initGraphView();
    }
    private void initGraphView() {
        // init example series data
        long now = new Date().getTime();
        long t = 86400000;

        GraphViewSeries exampleSeries;
        exampleSeries = new GraphViewSeries("", new GraphViewSeries.GraphViewSeriesStyle(Color.rgb(250, 98, 65), 4),
                new GraphView.GraphViewData[] {new GraphView.GraphViewData(now - 6 * t, 0),
                        new GraphView.GraphViewData(now - 5 * t, 10d), new GraphView.GraphViewData(now - 4 * t, 23d),
                        new GraphView.GraphViewData(now - 3 * t, 49d), new GraphView.GraphViewData(now - 2 * t, 68d),
                        new GraphView.GraphViewData(now - 1 * t, 120d), new GraphView.GraphViewData(now, 94d)});

        LineGraphView graphView;
        graphView = new LineGraphView(getActivity(), "");
        graphView.setDrawBackground(true);
        // 线条色
        graphView.setBackgroundColor(Color.argb(128, 254, 232, 226));
        graphView.setDataPointsRadius(0);

        // 字体色
        int fontColor = R.color.graph_gray;
        // 风格色
        graphView.getGraphViewStyle().setGridColor(R.color.modify_gray);
        graphView.getGraphViewStyle().setHorizontalLabelsColor(fontColor);
        graphView.getGraphViewStyle().setVerticalLabelsColor(fontColor);
        // x轴标签数
        graphView.getGraphViewStyle().setNumHorizontalLabels(7);
        // y轴标签数
        graphView.getGraphViewStyle().setNumVerticalLabels(5);
        // 字号
        graphView.getGraphViewStyle().setTextSize(DensityUtil.sp2px(getActivity(), 12));
        graphView.getGraphViewStyle().setVerticalLabelsAlign(Paint.Align.RIGHT);
        graphView.getGraphViewStyle().setVerticalLabelsWidth(DensityUtil.sp2px(getActivity(), 20));

        graphView.addSeries(exampleSeries);

        final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd", Locale.CHINESE);
        graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    Date d = new Date((long) value);
                    return dateFormat.format(d);
                }
                return null;
            }
        });

        layout.addView(graphView);
    }
}
