package com.denyszaiats.myreactions;

import com.denyszaiats.myreactions.ChartView.ChartView;
import com.denyszaiats.myreactions.ChartView.LabelAdapter;
import com.denyszaiats.myreactions.ChartView.LinearSeries;

public class ChartBuilder {

    public static void buildChart(LinearSeries series, ChartView chartView, int vertical, int horisontal, LabelAdapter leftAdapter, LabelAdapter bottomAdapter){

        chartView.clearSeries();

        series.setLineColor(0xFF0099CC);
        series.setLineWidth(4);
        series.setPointWidth(7);

        chartView.addSeries(series);
        chartView.setHorizontalScrollBarEnabled(true);
        chartView.setGridLinesVertical(vertical);
        chartView.setGridLinesHorizontal(horisontal);
        chartView.setLeftLabelAdapter(leftAdapter);
        chartView.setBottomLabelAdapter(bottomAdapter);
        chartView.animate();
    }

}
