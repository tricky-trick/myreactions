package com.denyszaiats.myreactions;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ChartFragment extends Activity {
    private GraphicalView mChart;

    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();

    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

    private XYSeries mCurrentSeries;

    private XYSeriesRenderer mCurrentRenderer;
    private SharedPreferences prefs;

    private void initChart() {
        mCurrentSeries = new XYSeries("Click to see more details");
        mDataset.addSeries(mCurrentSeries);
        mCurrentRenderer = new XYSeriesRenderer();
        mCurrentRenderer.setLineWidth(8);
        //mCurrentRenderer.setDisplayBoundingPoints(true);
        //mCurrentRenderer.setPointStrokeWidth(15);
        mCurrentRenderer.setFillPoints(true);
        mCurrentRenderer.setPointStyle(PointStyle.CIRCLE);
        mCurrentRenderer.setColor(getResources().getColor(R.color.blue));
        mRenderer.addSeriesRenderer(mCurrentRenderer);
        mRenderer.setLabelsTextSize(20);
        mRenderer.setLegendTextSize(30);
        mRenderer.setAxesColor(getResources().getColor(R.color.black));
        mRenderer.setGridColor(getResources().getColor(R.color.silver));
        mRenderer.setLabelsColor(getResources().getColor(R.color.black));
        mRenderer.setExternalZoomEnabled(true);
        mRenderer.setShowGrid(true);
        mRenderer.setMarginsColor(getResources().getColor(R.color.white));
        mRenderer.setPointSize(15);
        mRenderer.setYAxisAlign(Paint.Align.LEFT, 0);
        mRenderer.setYLabelsAlign(Paint.Align.RIGHT, 0);
        mRenderer.setMargins(new int[] { 20, 80, 100, 10 });
        mRenderer.setYLabelsColor(0, getResources().getColor(R.color.black));
        mRenderer.setXLabelsColor(getResources().getColor(R.color.black));
        mRenderer.setClickEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_fragment_all_results);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    protected void onResume() {
        super.onResume();
        LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
        if (mChart == null) {
            initChart();
            setSeriesForAllResults();
            mChart = ChartFactory.getTimeChartView(this, mDataset, mRenderer, "dd MMM yy\nHH:mm");
            mChart.setClickable(true);
            mChart.setBackgroundColor(getResources().getColor(R.color.white));
            mChart.setZoomRate(0.5f);
            layout.addView(mChart);
        } else {
            mChart.repaint();
        }

        mChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeriesSelection seriesSelection = mChart.getCurrentSeriesAndPoint();
                if (seriesSelection == null) {

                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm");
                    Date date = new Date();
                    String s = String.valueOf(seriesSelection.getXValue()).replace(".","").replace("E","") + "0";
                    date.setTime(Long.parseLong(s));
                    String formattedDate = dateFormat.format(date);
                    Toast.makeText(
                            ChartFragment.this, String.format("Date: %s\nClicks: %s", formattedDate, String.valueOf(seriesSelection.getValue()).replace(".0","")), Toast.LENGTH_LONG).show();
                }
            }
        });

        ImageButton btnZoomIn= (ImageButton) findViewById(R.id.zoomIn);
        ImageButton btnZoomOut= (ImageButton) findViewById(R.id.zoomOut);
        ImageButton btnZoomReset= (ImageButton) findViewById(R.id.zoomReset);

        btnZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChart.zoomOut();
            }
        });

        btnZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChart.zoomIn();
            }
        });

        btnZoomReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChart.zoomReset();

            }
        });
    }

    private void setSeriesForAllResults() {
        String filter = prefs.getString("FILTER", "");
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith(filter)) {
                double date = Double.parseDouble(entry.getKey().split("-")[2]);
                double amount = entry.getValue().toString().split(",").length;
                mCurrentSeries.add(date * 1000, amount);
            }
        }
    }
}