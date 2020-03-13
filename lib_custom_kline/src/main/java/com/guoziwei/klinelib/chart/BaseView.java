package com.guoziwei.klinelib.chart;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.Transformer;
import com.guoziwei.klinelib.R;
import com.guoziwei.klinelib.model.HisData;
import com.guoziwei.klinelib.util.DoubleUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/14 0014.
 */

public class BaseView extends LinearLayout {

    public int MAX_COUNT = 200;
    public int MIN_COUNT = 50;
    public int INIT_COUNT = 100;
    protected String mDateFormat = "yyyy-MM-dd HH:mm";
    protected int mDecreasingColor;
    protected int mIncreasingColor;
    protected int mAxisColor;
    protected int mTransparentColor;
    /**
     * the digits of the symbol
     */
    protected int mPriceDigits = 8;
    protected int mVolDigits = 8;
    protected List<HisData> mData = new ArrayList<>(300);
    private Context mContext;

    public BaseView(Context context) {
        this(context, null);
    }

    public BaseView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mAxisColor = ContextCompat.getColor(getContext(), R.color.klineAxis);
        mTransparentColor = ContextCompat.getColor(getContext(), android.R.color.transparent);
        mDecreasingColor = ContextCompat.getColor(getContext(), R.color.decreasing_color);
        mIncreasingColor = ContextCompat.getColor(getContext(), R.color.increasing_color);
    }

    public void setDigits(int digits, int volDigits) {
        mPriceDigits = digits;
        mVolDigits = volDigits;
    }


    protected void initBottomChart(AppCombinedChart chart) {
        chart.setScaleEnabled(true);
        chart.setDrawBorders(true);
        chart.setBorderWidth(1);
        chart.setBorderColor(getResources().getColor(R.color.k_dark_gray_bg));//边线颜色
        chart.setDragEnabled(true);
        chart.setScaleYEnabled(false);
        chart.setAutoScaleMinMaxEnabled(true);
        chart.setDragDecelerationEnabled(false);
        chart.setHighlightPerDragEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);

        Legend lineChartLegend = chart.getLegend();
        lineChartLegend.setEnabled(false);

        LineChartXMarkerView mvx = new LineChartXMarkerView(mContext, mData);
        mvx.setChartView(chart);
        chart.setXMarker(mvx);


        XAxis xAxisVolume = chart.getXAxis();

        xAxisVolume.setDrawLabels(false);
        xAxisVolume.setDrawAxisLine(false);
        xAxisVolume.setDrawGridLines(false);
        xAxisVolume.setAxisMinimum(-0.5f);
       /* xAxisVolume.setDrawLabels(true);
        xAxisVolume.setDrawAxisLine(false);
        xAxisVolume.setDrawGridLines(false);
        xAxisVolume.setTextColor(mAxisColor);
        xAxisVolume.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisVolume.setLabelCount(3, true);
        xAxisVolume.setAvoidFirstLastClipping(true);
        xAxisVolume.setAxisMinimum(-0.5f);

        xAxisVolume.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (mData.isEmpty()) {
                    return "";
                }
                if (value < 0) {
                    value = 0;
                }
                if (value < mData.size()) {
                    return KLineDateUtils.formatDate(mData.get((int) value).getDate(), mDateFormat);
                }
                return "";
            }
        });
*/
        YAxis axisLeftVolume = chart.getAxisLeft();
        axisLeftVolume.setDrawLabels(true);
        axisLeftVolume.setDrawGridLines(false);
       /* if (k == 0) {
            axisLeftVolume.setStartAtZero(true);

        }else{

        }*/

        axisLeftVolume.setLabelCount(2, true);

        axisLeftVolume.setDrawAxisLine(false);
        axisLeftVolume.setTextColor(mAxisColor);
        axisLeftVolume.setSpaceTop(10);
        axisLeftVolume.setSpaceBottom(0);
        axisLeftVolume.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);

        axisLeftVolume.setValueFormatter(new IAxisValueFormatter() {//y轴数据的 format
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return DoubleUtil.getStringByDigits(value, mPriceDigits);
            }
        });

        /*axisLeftVolume.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String s;
                if (value > 10000) {
                    s = (int) (value / 10000) + "w";
                } else if (value > 1000) {
                    s = (int) (value / 1000) + "k";
                } else {
                    s = (int) value + "";
                }
                return String.format(Locale.getDefault(), "%1$5s", s);
            }
        });
*/
        Transformer leftYTransformer = chart.getRendererLeftYAxis().getTransformer();
        ColorContentYAxisRenderer leftColorContentYAxisRenderer = new ColorContentYAxisRenderer(chart.getViewPortHandler(), chart.getAxisLeft(), leftYTransformer);
        leftColorContentYAxisRenderer.setLabelInContent(true);
        leftColorContentYAxisRenderer.setUseDefaultLabelXOffset(false);
        chart.setRendererLeftYAxis(leftColorContentYAxisRenderer);

        //右边y
        YAxis axisRightVolume = chart.getAxisRight();
        axisRightVolume.setDrawLabels(false);
        axisRightVolume.setDrawGridLines(false);
        axisRightVolume.setDrawAxisLine(false);

    }


    protected void moveChart(AppCombinedChart chart) {
        if (mData == null) return;
        if (mData.size() > INIT_COUNT) {
            chart.moveViewToX(mData.size());
        } else {
            chart.moveViewToX(0);
        }
    }


    /**
     * set the count of k chart
     */
    public void setCount(int init, int max, int min) {
        INIT_COUNT = init;
        MAX_COUNT = max;
        MIN_COUNT = min;
    }

    protected void setDescription(Chart chart, String text) {
        Description description = chart.getDescription();
        float dx = chart.getWidth() - chart.getViewPortHandler().offsetRight() - description.getXOffset();
        description.setPosition(dx, description.getTextSize());
        description.setText(text);

        description.setTextColor(getResources().getColor(R.color.ma10));
    }


    public HisData getLastData() {
        if (mData != null && !mData.isEmpty()) {
            return mData.get(mData.size() - 1);
        }
        return null;
    }

    public void setDateFormat(String mDateFormat) {
        this.mDateFormat = mDateFormat;
    }
}
