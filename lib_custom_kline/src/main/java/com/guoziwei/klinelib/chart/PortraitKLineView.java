package com.guoziwei.klinelib.chart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.guoziwei.klinelib.R;
import com.guoziwei.klinelib.model.HisData;
import com.guoziwei.klinelib.util.DataUtils;
import com.guoziwei.klinelib.util.DisplayUtils;
import com.guoziwei.klinelib.util.DoubleUtil;
import com.guoziwei.klinelib.util.KLineDateUtils;

import java.util.ArrayList;
import java.util.List;

public class PortraitKLineView extends BaseView implements CoupleChartGestureListener.OnAxisChangeListener, OnSelectListener {


    public static final int NORMAL_LINE = 0;
    /**
     * average line
     */
    public static final int AVE_LINE = 1;
    /**
     * hide line
     */
    public static final int INVISIABLE_LINE = 6;

    public static final int MA5 = 5;
    public static final int MA10 = 10;
    public static final int MA20 = 20;


    protected AppCombinedChart mMasterChart;//主图
    protected AppCombinedChart mSubChart;//副图

    protected TextView tv_index, tv_index01, tv_index02, tv_index03;

    protected Context mContext;

    protected OnSelectListener mOnSelectListener;

    // ma布局
    protected LinearLayout mMaLayout;
    /**
     * 初始化日线图
     */

    boolean hasInit;
    /**
     * last price
     */
    private double mLastPrice;
    /**
     * yesterday close price
     */
    private double mLastClose;
    private boolean isTimeChart;//分时图
    /**
     * the mDigits of the symbol
     */
    //private int defaultDigits = 8;//显示精度

    private int chart02Type = 3;//指标类型

    public PortraitKLineView(Context context) {
        this(context, null);
    }

    public PortraitKLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PortraitKLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_kline_portrait, this);
        mMasterChart = (AppCombinedChart) findViewById(R.id.price_chart);
        mSubChart = (AppCombinedChart) findViewById(R.id.vol_chart);

        tv_index = (TextView) findViewById(R.id.tv_index);
        tv_index01 = (TextView) findViewById(R.id.tv_index01);
        tv_index02 = (TextView) findViewById(R.id.tv_index02);
        tv_index03 = (TextView) findViewById(R.id.tv_index03);
        mMaLayout = (LinearLayout) findViewById(R.id.ll_ma);


        setKLineViewNoData();


        mMasterChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.CANDLE,
                CombinedChart.DrawOrder.LINE
        });
    }

    /**
     * 无数据显示的view
     */
    public void setKLineViewNoData() {
        mMasterChart.setNoDataText(mContext.getString(R.string.temporarily_no_data));
        mSubChart.setNoDataText("");
    }

    public void setSubChartVisible(boolean isVisible) {
        if (isVisible) {
            mSubChart.setVisibility(VISIBLE);
        } else {
            mSubChart.setVisibility(GONE);
        }
    }

    public void initDayChart() {
        if (hasInit) return;
        initMasterChart();//初始化主图
        initBottomChart(mSubChart);//初始化副图
        setOffset();
        hasInit = true;
    }


    public void refreshDigits() {
        mMasterChart.setDigits(mPriceDigits);
        LineChartXMarkerView mvx = new LineChartXMarkerView(mContext, mData);  //markerView X
        mvx.setChartView(mMasterChart);
        mMasterChart.setXMarker(mvx);
        LineChartYMarkerView mv = new LineChartYMarkerView(mContext, mData, mPriceDigits);//markerView Y
        mv.setChartView(mMasterChart);
        mMasterChart.setMarker(mv);
    }

    public void setSelectListener(OnSelectListener listener) {
        mOnSelectListener = listener;
        initChartListener();
    }

    protected void initMasterChart() {
        mMasterChart.setScaleEnabled(true);//缩放
        mMasterChart.setDrawBorders(true);//chart 框线
        mMasterChart.setBorderWidth(1);//框线宽度
        mMasterChart.setBorderColor(getResources().getColor(R.color.k_dark_gray_bg));//边线颜色
        mMasterChart.setDragEnabled(true);//是否可拖动
        mMasterChart.setScaleYEnabled(false);//Y轴是否可以缩放
        mMasterChart.setAutoScaleMinMaxEnabled(true);//支持自动缩放
        mMasterChart.setDoubleTapToZoomEnabled(false);//双击chart缩放
        mMasterChart.setDragDecelerationEnabled(true);//拖拽滚动时,手放开是否会持续滚动
        mMasterChart.setDigits(mPriceDigits);
        LineChartXMarkerView mvx = new LineChartXMarkerView(mContext, mData);  //markerView X
        mvx.setChartView(mMasterChart);
        mMasterChart.setXMarker(mvx);
        LineChartYMarkerView mv = new LineChartYMarkerView(mContext, mData, mPriceDigits);//markerView Y
        mv.setChartView(mMasterChart);

        mMasterChart.setMarker(mv);

        Legend lineChartLegend = mMasterChart.getLegend();
        lineChartLegend.setEnabled(false);


        XAxis xAxisPrice = mMasterChart.getXAxis();//X轴控制
        xAxisPrice.setDrawLabels(true);//是否显示x轴的坐标
        xAxisPrice.setDrawAxisLine(false);//
        xAxisPrice.setDrawGridLines(true);//是否显示X坐标轴上的刻度竖线，默认是true  **格线
        xAxisPrice.setGridColor(getResources().getColor(R.color.k_dark_gray_bg));
        xAxisPrice.setGridLineWidth(0.5f);
        xAxisPrice.enableGridDashedLine(10f, 0f, 5f);//虚线表示X轴上的刻度竖线(float lineLength, float spaceLength, float phase)三个参数，1.线长，2.虚线间距，3.虚线开始坐标
        xAxisPrice.setTextColor(mAxisColor);//X轴 坐标颜色
        xAxisPrice.setPosition(XAxis.XAxisPosition.BOTTOM);//X轴 坐标位置
        xAxisPrice.setLabelCount(3, false);//X轴坐标数量
        xAxisPrice.setAvoidFirstLastClipping(true);//第一个坐标覆盖问题
        xAxisPrice.setAxisMinimum(-0.5f);
        xAxisPrice.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {//X轴 数据format
                if (mData.isEmpty()) return "";
                if (mData.size() < MAX_COUNT / 3) {
                    if (value == 0)
                        return KLineDateUtils.formatDate(mData.get((int) value).getDate(), mDateFormat);
                    return "";
                }
                if (value < 0) value = 0;
                if (value < mData.size()) {
                    return KLineDateUtils.formatDate(mData.get((int) value).getDate(), mDateFormat);
                }
                return "";
            }
        });


        YAxis axisLeftPrice = mMasterChart.getAxisLeft();//Y轴左边
        axisLeftPrice.setLabelCount(3, true);
        axisLeftPrice.setDrawLabels(true);
        axisLeftPrice.setDrawGridLines(true);
        axisLeftPrice.setGridColor(getResources().getColor(R.color.k_dark_gray_bg));
        axisLeftPrice.setGridLineWidth(0.5f);
        axisLeftPrice.enableGridDashedLine(5f, 10f, 0f);//虚线表示Y轴上的刻度横线(float lineLength, float spaceLength, float phase)三个参数，1.线长，2.虚线间距，3.虚线开始坐标
        axisLeftPrice.setDrawAxisLine(false);
        axisLeftPrice.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        axisLeftPrice.setTextColor(mAxisColor);
        axisLeftPrice.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return DoubleUtil.getStringByDigits(value, mPriceDigits);
            }
        });

        int[] colorArray = {mAxisColor, mAxisColor, mAxisColor};
        Transformer leftYTransformer = mMasterChart.getRendererLeftYAxis().getTransformer();
        ColorContentYAxisRenderer leftColorContentYAxisRenderer = new ColorContentYAxisRenderer(mMasterChart.getViewPortHandler(), mMasterChart.getAxisLeft(), leftYTransformer);
        leftColorContentYAxisRenderer.setLabelColor(colorArray);
        leftColorContentYAxisRenderer.setLabelInContent(true);
        leftColorContentYAxisRenderer.setUseDefaultLabelXOffset(false);
        mMasterChart.setRendererLeftYAxis(leftColorContentYAxisRenderer);


        YAxis axisRightPrice = mMasterChart.getAxisRight();
        axisRightPrice.setDrawLabels(false);
        axisRightPrice.setDrawGridLines(false);
        axisRightPrice.setDrawAxisLine(false);

    }

    private void initChartListener() {

        mMasterChart.setOnChartGestureListener(new CoupleChartGestureListener(this, mMasterChart, mSubChart));
        mSubChart.setOnChartGestureListener(new CoupleChartGestureListener(this, mSubChart, mMasterChart));

        mMasterChart.setOnChartValueSelectedListener(new InfoViewListener(mContext, this, mLastClose, mData, null, mSubChart));
        mSubChart.setOnChartValueSelectedListener(new InfoViewListener(mContext, this, mLastClose, mData, null, mMasterChart));

        mMasterChart.setOnTouchListener(new ChartInfoViewHandler(mMasterChart, this, mSubChart));
    }


    /**
     * @param hisDatas
     */
    public void initData(List<HisData> hisDatas, boolean isTimeChart) {

        this.isTimeChart = isTimeChart;
        mData.clear();
        mMasterChart.clear();
        mSubChart.clear();

        mData.addAll(DataUtils.calculateAll(hisDatas));
        if (isTimeChart) {
            showTimeLineData(true);
        } else {
            showSMAKline(true);//sma
        }

        // 成交量
        showChartVolumeData(true);

        setDescription(mMasterChart, "");
        setDescription(mSubChart, "");

    }


    private BarDataSet setBar(ArrayList<BarEntry> barEntries, int type) {
        BarDataSet barDataSet = new BarDataSet(barEntries, "vol");
        barDataSet.setHighLightAlpha(255);
        barDataSet.setHighLightColor(getResources().getColor(R.color.white));
        barDataSet.setDrawValues(false);
        barDataSet.setVisible(type != INVISIABLE_LINE);
        barDataSet.setHighlightEnabled(type != INVISIABLE_LINE);


        // 红跌绿跌
        barDataSet.setColors(getResources().getColor(R.color.increasing_color), getResources().getColor(R.color.decreasing_color));
        return barDataSet;
    }

    @NonNull
    private LineDataSet setLine(int type, ArrayList<Entry> lineEntries) {

        LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "ma" + type);
        lineDataSetMa.setHighLightColor(getResources().getColor(R.color.white));
        lineDataSetMa.setHighlightLineWidth(0.5f);
        lineDataSetMa.setDrawValues(false);
        lineDataSetMa.setHighlightEnabled(false);
        lineDataSetMa.setDrawHorizontalHighlightIndicator(false);
        if (type == NORMAL_LINE) {
            lineDataSetMa.setCircleColor(ContextCompat.getColor(mContext, R.color.normal_line_color));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setColor(ContextCompat.getColor(mContext, R.color.klineMinLine));
            lineDataSetMa.setDrawFilled(true);
            lineDataSetMa.setHighlightEnabled(true);
            lineDataSetMa.setDrawHorizontalHighlightIndicator(true);
            lineDataSetMa.setFillColor(ContextCompat.getColor(mContext, R.color.klineMinLine));
        } else if (type == AVE_LINE) {
            lineDataSetMa.setColor(getResources().getColor(R.color.klineMa10));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
        } else if (type == MA5) {
            lineDataSetMa.setColor(getResources().getColor(R.color.ma5));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
        } else if (type == MA10) {
            lineDataSetMa.setColor(getResources().getColor(R.color.ma10));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
        } else if (type == MA20) {
            lineDataSetMa.setColor(getResources().getColor(R.color.ma20));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
        } else {
            lineDataSetMa.setVisible(false);
            lineDataSetMa.setHighlightEnabled(false);
        }
        lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSetMa.setLineWidth(1f);
        lineDataSetMa.setCircleRadius(1f);

        lineDataSetMa.setDrawCircles(false);
        lineDataSetMa.setDrawCircleHole(false);

        return lineDataSetMa;
    }

    @NonNull
    public CandleDataSet setKLine(int type, ArrayList<CandleEntry> lineEntries) {
        CandleDataSet set = new CandleDataSet(lineEntries, "KLine" + type);
        set.setDrawIcons(false);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setShadowColor(Color.DKGRAY);
        set.setShadowWidth(0.75f);
        set.setDecreasingColor(mDecreasingColor);
        set.setDecreasingPaintStyle(Paint.Style.FILL);
        set.setShadowColorSameAsCandle(true);
        set.setIncreasingColor(mIncreasingColor);
        set.setIncreasingPaintStyle(Paint.Style.FILL);
        set.setNeutralColor(ContextCompat.getColor(getContext(), R.color.increasing_color));
        set.setDrawValues(true);
        set.setValueTextSize(10);
        set.setValueTextColor(getResources().getColor(R.color.white));
        set.setHighlightEnabled(true);
        set.setHighLightColor(getResources().getColor(R.color.white));
        set.setHighlightLineWidth(0.5f);
        if (type != NORMAL_LINE) {
            set.setVisible(false);
        }
        return set;
    }


    public void resetMasterChart(boolean resetTouch) {//重置master chart
        mMasterChart.clear();

        if (resetTouch) {
            mMasterChart.getViewPortHandler().getMatrixTouch().reset();
            int maxMum = mData.size() < INIT_COUNT ? INIT_COUNT : mData.size();
            mMasterChart.getXAxis().setAxisMaximum(maxMum - 0.5f);
            mMasterChart.setVisibleXRange(MAX_COUNT, MIN_COUNT);
        }
    }

    public void resetSubChart(boolean resetTouch) {//重置sub chart
        mSubChart.clear();
        mSubChart.getAxisLeft().resetAxisMaximum();//Y轴重制
        if (resetTouch) {
            mSubChart.getViewPortHandler().getMatrixTouch().reset();
            int maxMum = mData.size() < INIT_COUNT ? INIT_COUNT : mData.size();
            mSubChart.getXAxis().setAxisMaximum(maxMum - 0.5f);
            mSubChart.setVisibleXRange(MAX_COUNT, MIN_COUNT);
        }
    }


    public CandleData creatCandleData() {// 蜡烛图 数据
        ArrayList<CandleEntry> lineCJEntries = new ArrayList<>();
        for (int i = 0; i < mData.size(); i++) {
            HisData hisData = mData.get(i);
            lineCJEntries.add(new CandleEntry(i, (float) hisData.getHigh(), (float) hisData.getLow(), (float) hisData.getOpen(), (float) hisData.getClose()));
        }
        return new CandleData(setKLine(NORMAL_LINE, lineCJEntries));
    }

    public LineDataSet creatPaddingData() {// 填充数据

        ArrayList<Entry> paddingEntries = new ArrayList<>();
        if (!mData.isEmpty() && mData.size() < INIT_COUNT) {
            for (int i = mData.size(); i < INIT_COUNT; i++) {
                paddingEntries.add(new Entry(i, (float) mData.get(0).getClose()));
            }
        }
        return setLine(INVISIABLE_LINE, paddingEntries);
    }


    /**
     * 分时线
     */
    public void showTimeLineData(boolean resetTouch) {

        resetMasterChart(resetTouch);

        ArrayList<Entry> priceEntries = new ArrayList<>();
        ArrayList<Entry> aveEntries = new ArrayList<>();

        for (int i = 0; i < mData.size(); i++) {
            priceEntries.add(new Entry(i, (float) mData.get(i).getClose()));
            aveEntries.add(new Entry(i, (float) mData.get(i).getAvgPrice()));
        }

        ArrayList<ILineDataSet> sets = new ArrayList<>();

        sets.add(setLine(NORMAL_LINE, priceEntries));
        sets.add(setLine(AVE_LINE, aveEntries));
        sets.add(creatPaddingData());
        LineData lineData = new LineData(sets);

        CombinedData combinedData = new CombinedData();
        combinedData.setData(lineData);
        mMasterChart.setData(combinedData);

        mMasterChart.notifyDataSetChanged();
        mMasterChart.invalidate();


        if (resetTouch) setHandler(mMasterChart);

    }

    /**
     * 显示 MA
     */
    public void showSMAKline(boolean resetTouch) {

        resetMasterChart(resetTouch);

        ArrayList<Entry> ma7Entries = new ArrayList<>();
        ArrayList<Entry> ma25Entries = new ArrayList<>();
        ArrayList<Entry> ma99Entries = new ArrayList<>();

        for (int i = 0; i < mData.size(); i++) {
            HisData hisData = mData.get(i);

            if (!Double.isNaN(hisData.getMa5())) {
                ma7Entries.add(new Entry(i, (float) hisData.getMa5()));
            }

            if (!Double.isNaN(hisData.getMa10())) {
                ma25Entries.add(new Entry(i, (float) hisData.getMa10()));
            }

            if (!Double.isNaN(hisData.getMa20())) {
                ma99Entries.add(new Entry(i, (float) hisData.getMa20()));
            }
        }

        LineData lineData = new LineData(
                setLine(MA5, ma7Entries),
                setLine(MA10, ma25Entries),
                setLine(MA20, ma99Entries),
                creatPaddingData());

        CombinedData combinedData = new CombinedData();
        combinedData.setData(lineData);
        combinedData.setData(creatCandleData());
        mMasterChart.setData(combinedData);


        mMasterChart.notifyDataSetChanged();
        mMasterChart.invalidate();

        if (resetTouch) setHandler(mMasterChart);

    }


    private void showChartVolumeData(boolean resetTouch) {
        chart02Type = 3;
        resetSubChart(resetTouch);
        mSubChart.getAxisLeft().setStartAtZero(true);
        mSubChart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {//y轴数据的 format
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value == 0) return "";
                return DoubleUtil.getStringByDigits(value, mVolDigits);
            }
        });

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<Entry> vma5Entries = new ArrayList<>();
        ArrayList<Entry> vma10Entries = new ArrayList<>();

        for (int i = 0; i < mData.size(); i++) {
            HisData hisData = mData.get(i);

            if (!Double.isNaN(hisData.getVma5())) {
                vma5Entries.add(new Entry(i, (float) hisData.getVma5()));
            }

            if (!Double.isNaN(hisData.getMa10())) {
                vma10Entries.add(new Entry(i, (float) hisData.getVma10()));
            }

            barEntries.add(new BarEntry(i, (float) hisData.getVol(), hisData));
        }

        BarData barData = new BarData(setBar(barEntries, NORMAL_LINE));
        barData.setBarWidth(0.75f);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(setLine(MA5, vma5Entries));
        sets.add(setLine(MA10, vma10Entries));
        sets.add(creatPaddingData());

        LineData lineData = new LineData(sets);

        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        combinedData.setData(lineData);
        mSubChart.setData(combinedData);


        mSubChart.notifyDataSetChanged();
        mSubChart.invalidate();

        if (resetTouch) setHandler(mSubChart);

    }

    /**
     * 添加分时图
     */
    public void addTimeLineData(List<HisData> hisDatas) {

        if (mData == null) return;

        CombinedData combinedData = mMasterChart.getData();

        if (combinedData == null) return;
        // lineData
        LineData lineData = combinedData.getLineData();

        ILineDataSet normalLineSet = lineData.getDataSetByIndex(0);
        ILineDataSet aveLineSet = lineData.getDataSetByIndex(1);

        // 成交量数据
        // 柱状图
        CombinedData subCombinedData = mSubChart.getData();
        BarData subBarData = subCombinedData.getBarData();

        IBarDataSet volSet = subBarData.getDataSetByIndex(0);

        LineData subLineData = subCombinedData.getLineData();
        ILineDataSet vma5Set = subLineData.getDataSetByIndex(0);
        ILineDataSet vma10Set = subLineData.getDataSetByIndex(1);
        // ILineDataSet ma20Set = subLineData.getDataSetByIndex(3);

        if (mData != null && mData.size() > 0) {
            for (int j = mData.size(); j > 0; j--) {
                if (hisDatas.get(0).getDate() <= mData.get(j - 1).getDate()) {
                    mData.remove(j - 1);
                    normalLineSet.removeEntry(j - 1);
                    aveLineSet.removeEntry(j - 1);

                    vma5Set.removeLast(); // ma比较特殊，entry数量和k线的不一致，移除最后一个
                    vma10Set.removeLast();
                    volSet.removeLast();

                } else {
                    break;
                }
            }
        } else {
            return;
        }

        mData.addAll(hisDatas);
        mData = DataUtils.calculateAll(mData);//计算指标

        int maxMum = mData.size() < INIT_COUNT ? INIT_COUNT : mData.size();
        mMasterChart.getXAxis().setAxisMaximum(maxMum - 0.5f);
        mSubChart.getXAxis().setAxisMaximum(maxMum - 0.5f);

        int count = hisDatas.size();
        for (int i = 0; i < count; i++) {
            HisData hisData = mData.get(mData.size() - count + i);
            int index0 = normalLineSet.getEntryCount();
            normalLineSet.addEntry(new Entry(index0, (float) hisData.getClose())); // 分时
            aveLineSet.addEntry(new Entry(index0, (float) hisData.getAvgPrice()));  // 均线
            int index1 = volSet.getEntryCount();  //成交量
            volSet.addEntry(new BarEntry(index1, (float) hisData.getVol(), hisData)); // ma数据
            vma5Set.addEntry(new Entry(index1, (float) hisData.getVma5()));
            vma10Set.addEntry(new Entry(index1, (float) hisData.getVma10()));
        }
        mMasterChart.notifyDataSetChanged();
        mMasterChart.invalidate();

        // 主图刷新
        mSubChart.notifyDataSetChanged();
        mSubChart.invalidate();

    }


    public void addCandleData(List<HisData> hisDatas) {

        if (mData == null) return;
        if (mMasterChart == null) return;

        CombinedData combinedData = mMasterChart.getData();

        if (combinedData == null) return;

        CandleData candleData = combinedData.getCandleData();

        if (candleData == null) return;

        ICandleDataSet candleDataSet = candleData.getDataSetByIndex(0);

        // lineData
        LineData lineData = combinedData.getLineData();
        ILineDataSet ma5Set = lineData.getDataSetByIndex(0);
        ILineDataSet ma10Set = lineData.getDataSetByIndex(1);
        ILineDataSet ma20Set = lineData.getDataSetByIndex(2);


        CombinedData subCombinedData = mSubChart.getData();
        BarData subBarData = subCombinedData.getBarData();

        IBarDataSet volSet = subBarData.getDataSetByIndex(0);

        LineData subLineData = subCombinedData.getLineData();
        ILineDataSet vma5Set = subLineData.getDataSetByIndex(0);
        ILineDataSet vma10Set = subLineData.getDataSetByIndex(1);


        if (mData != null && mData.size() > 0) {
            for (int j = mData.size(); j > 0; j--) {
                if (hisDatas.get(0).getDate() <= mData.get(j - 1).getDate()) {
                    mData.remove(j - 1);
                    candleDataSet.removeLast();
                    ma5Set.removeLast();
                    ma10Set.removeLast();
                    ma20Set.removeLast();

                    vma5Set.removeLast(); // ma比较特殊，entry数量和k线的不一致，移除最后一个
                    vma10Set.removeLast();
                    volSet.removeLast();
                } else {
                    break;
                }
            }
        } else {
            return;
        }

        mData.addAll(hisDatas);
        mData = DataUtils.calculateAll(mData);//计算指标

        int maxMum = mData.size() < INIT_COUNT ? INIT_COUNT : mData.size();
        mMasterChart.getXAxis().setAxisMaximum(maxMum - 0.5f);
        mSubChart.getXAxis().setAxisMaximum(maxMum - 0.5f);

        int count = hisDatas.size();
        for (int i = 0; i < count; i++) {

            HisData hisData = mData.get(mData.size() - count + i);
            // 蜡烛图
            int index0 = candleDataSet.getEntryCount();
            candleDataSet.addEntry(new CandleEntry(index0, (float) hisData.getHigh(), (float) hisData.getLow(), (float) hisData.getOpen(), (float) hisData.getClose()));
            ma5Set.addEntry(new Entry(index0, (float) hisData.getMa5()));
            ma10Set.addEntry(new Entry(index0, (float) hisData.getMa10()));
            ma20Set.addEntry(new Entry(index0, (float) hisData.getMa20()));

            int index = volSet.getEntryCount();
            volSet.addEntry(new BarEntry(index, (float) hisData.getVol(), hisData)); //成交量
            vma5Set.addEntry(new Entry(index, (float) hisData.getVma5())); // 成交量ma数据
            vma10Set.addEntry(new Entry(index, (float) hisData.getVma10()));
        }
        mMasterChart.notifyDataSetChanged();
        mMasterChart.invalidate();

        // 主图刷新
        mSubChart.notifyDataSetChanged();
        mSubChart.invalidate();
    }

    /**
     * align two chart
     */
    private void setOffset() {
        int chartHeight = getResources().getDimensionPixelSize(R.dimen.bottom_chart_height);
        int xAlx = DisplayUtils.dip2px(mContext, 20);
        mMasterChart.setViewPortOffsets(0, 0, 0, chartHeight + xAlx);
        mSubChart.setViewPortOffsets(0, 0, 0, 0);
    }


    /**
     * add limit line to chart
     */
    public void setLimitLine(double lastClose) {
        LimitLine limitLine = new LimitLine((float) lastClose);
        limitLine.enableDashedLine(2, 10, 0);
        limitLine.setLineColor(getResources().getColor(R.color.k_dark_gray_bg));
        mMasterChart.getAxisLeft().addLimitLine(limitLine);
    }

    public void setLimitLine() {
        setLimitLine(mLastClose);
    }

    @Override
    public void onAxisChange(BarLineChartBase chart) {
    }

    private void setHandler(AppCombinedChart combinedChart) {

        moveChart(combinedChart);

        ViewPortHandler viewPortHandlerBar = combinedChart.getViewPortHandler();
        Matrix matrix = viewPortHandlerBar.getMatrixTouch();
        float scale = 1f;
        if (mData.size() > MAX_COUNT) {
            scale = 2f;
        } else if (mData.size() > INIT_COUNT) {
            scale = mData.size() / (float) INIT_COUNT;
        }
        matrix.postScale(scale, 1f);
    }


    @Override
    public void onSelect(List<HisData> mDatas, int index) {

        mOnSelectListener.onSelect(mDatas, index);
        int newIndex = index;

        mMaLayout.setVisibility(VISIBLE);

        if (null != mDatas && mDatas.size() > 0 && newIndex >= 0 && newIndex < mDatas.size()) {
            if (isTimeChart)
                setDescription(mMasterChart, "Avg: " + DoubleUtil.getStringByDigits(mDatas.get(index).getAvgPrice(), mPriceDigits));
            String indexStr = "";
            String index01Str = "";
            String index02Str = "";
            String index03Str = "";

            if (chart02Type == 3) {
                indexStr = DoubleUtil.getStringByDigits(mDatas.get(newIndex).getVol(), mVolDigits);
                if (!TextUtils.isEmpty(indexStr))
                    indexStr = String.format(getResources().getString(R.string.k_vol), indexStr);
                index01Str = DoubleUtil.getStringByDigits(mDatas.get(newIndex).getVma5(), mVolDigits);
                if (!TextUtils.isEmpty(index01Str))
                    index01Str = String.format(getResources().getString(R.string.k_ma5), index01Str);
                index02Str = DoubleUtil.getStringByDigits(mDatas.get(newIndex).getVma10(), mVolDigits);
                if (!TextUtils.isEmpty(index02Str))
                    index02Str = String.format(getResources().getString(R.string.k_ma10), index02Str);


                tv_index01.setTextColor(getResources().getColor(R.color.ma5));
                tv_index02.setTextColor(getResources().getColor(R.color.ma10));
                // tv_index03.setTextColor(getResources().getColor(R.color.ma20));
            } else if (chart02Type == 4) {
                indexStr = DoubleUtil.getStringByDigits(mDatas.get(newIndex).getMacd(), mPriceDigits);
                if (!TextUtils.isEmpty(indexStr))
                    indexStr = String.format(getResources().getString(R.string.k_vol), indexStr);
                index01Str = DoubleUtil.getStringByDigits(mDatas.get(newIndex).getDif(), mPriceDigits);
                if (!TextUtils.isEmpty(index01Str))
                    index01Str = String.format(getResources().getString(R.string.k_dif), index01Str);
                index02Str = DoubleUtil.getStringByDigits(mDatas.get(newIndex).getDea(), mPriceDigits);
                if (!TextUtils.isEmpty(index02Str))
                    index02Str = String.format(getResources().getString(R.string.k_dea), index02Str);
                tv_index01.setTextColor(getResources().getColor(R.color.dif));
                tv_index02.setTextColor(getResources().getColor(R.color.dea));
            } else if (chart02Type == 5) {
                index01Str = DoubleUtil.getStringByDigits(mDatas.get(newIndex).getK(), mPriceDigits);
                if (!TextUtils.isEmpty(index01Str))
                    index01Str = String.format(getResources().getString(R.string.k_k), index01Str);
                index02Str = DoubleUtil.getStringByDigits(mDatas.get(newIndex).getD(), mPriceDigits);
                if (!TextUtils.isEmpty(index02Str))
                    index02Str = String.format(getResources().getString(R.string.k_d), index02Str);
                index03Str = DoubleUtil.getStringByDigits(mDatas.get(newIndex).getJ(), mPriceDigits);
                if (!TextUtils.isEmpty(index03Str))
                    index03Str = String.format(getResources().getString(R.string.k_j), index03Str);

                tv_index01.setTextColor(getResources().getColor(R.color.k));
                tv_index02.setTextColor(getResources().getColor(R.color.d));
                tv_index03.setTextColor(getResources().getColor(R.color.j));
            } else if (chart02Type == 6) {
                index01Str = DoubleUtil.getStringByDigits(mDatas.get(newIndex).getRsiData6(), mPriceDigits);
                if (!TextUtils.isEmpty(index01Str))
                    index01Str = String.format(getResources().getString(R.string.k_rsi6), index01Str);
                index02Str = DoubleUtil.getStringByDigits(mDatas.get(newIndex).getRsiData12(), mPriceDigits);
                if (!TextUtils.isEmpty(index02Str))
                    index02Str = String.format(getResources().getString(R.string.k_rsi12), index02Str);
                index03Str = DoubleUtil.getStringByDigits(mDatas.get(newIndex).getRsiData24(), mPriceDigits);
                if (!TextUtils.isEmpty(index03Str))
                    index03Str = String.format(getResources().getString(R.string.k_rsi24), index03Str);

                tv_index01.setTextColor(getResources().getColor(R.color.rsi6));
                tv_index02.setTextColor(getResources().getColor(R.color.rsi12));
                tv_index03.setTextColor(getResources().getColor(R.color.rsi24));
            }

            if (TextUtils.isEmpty(indexStr)) {
                tv_index.setVisibility(View.GONE);
            } else {
                tv_index.setVisibility(View.VISIBLE);
                tv_index.setText(indexStr);
            }

            if (TextUtils.isEmpty(index01Str)) {
                tv_index01.setVisibility(View.GONE);
            } else {
                tv_index01.setVisibility(View.VISIBLE);
                tv_index01.setText(index01Str);
            }

            if (TextUtils.isEmpty(index02Str)) {
                tv_index02.setVisibility(View.GONE);
            } else {
                tv_index02.setVisibility(View.VISIBLE);
                tv_index02.setText(index02Str);
            }

            if (TextUtils.isEmpty(index03Str)) {
                tv_index03.setVisibility(View.GONE);
            } else {
                tv_index03.setVisibility(View.VISIBLE);
                tv_index03.setText(index03Str);
            }
        } else {
            tv_index.setVisibility(View.GONE);
            tv_index01.setVisibility(View.GONE);
            tv_index02.setVisibility(View.GONE);
            tv_index03.setVisibility(View.GONE);
        }

    }

    @Override
    public void unSelect() {
        setDescription(mMasterChart, "");//ave
        mMaLayout.setVisibility(GONE);
        tv_index.setVisibility(View.GONE);
        tv_index01.setVisibility(View.GONE);
        tv_index02.setVisibility(View.GONE);
        tv_index03.setVisibility(View.GONE);
        mOnSelectListener.unSelect();
    }


    public void clearKLineViewData() {
        if (mMasterChart != null) mMasterChart.clear();
        if (mSubChart != null) mSubChart.clear();
    }


}
