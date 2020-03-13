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

/**
 * kline
 * Created by guoziwei on 2017/10/26.
 */
public class KLineView extends BaseView implements CoupleChartGestureListener.OnAxisChangeListener, OnSelectListener {


    public static final int NORMAL_LINE = 0;
    /**
     * average line
     */
    public static final int AVG_LINE = 1;
    /**
     * hide line
     */
    public static final int INVISIABLE_LINE = 6;


    public static final int MA5 = 5;
    public static final int MA10 = 10;
    public static final int MA20 = 20;

    public static final int EMA7 = 7;
    public static final int EMA30 = 30;

    public static final int K = 31;
    public static final int D = 32;
    public static final int J = 33;

    public static final int DIF = 34;
    public static final int DEA = 35;

    public static final int UPS = 36;
    public static final int MBS = 37;
    public static final int DNS = 38;

    public static final int RSI6 = 39;
    public static final int RSI12 = 40;
    public static final int RSI24 = 41;
    public static final int CANDEL_CHART = 0;
    public static final int TIME_CHART = 1;
    protected AppCombinedChart mMasterChart;//主图
    protected AppCombinedChart mSubChart;//副图
    protected TextView tv_index, tv_index01, tv_index02, tv_index03;
    protected Context mContext;
    protected OnSelectListener mOnSelectListener;
    /**
     * 初始化日线图
     */

    boolean hasInit;
    private LinearLayout llMaView;
    /**
     * last price
     */
    private double mLastPrice;
    /**
     * yesterday close price
     */
    private double mLastClose;
    private int isTimeChart = TIME_CHART;//分时图
    private int chart01Type = 0;//指标类型
    private int chart02Type = 3;//指标类型

    public KLineView(Context context) {
        this(context, null);
    }

    public KLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_kline, this);
        mMasterChart = (AppCombinedChart) findViewById(R.id.price_chart);
        mSubChart = (AppCombinedChart) findViewById(R.id.vol_chart);
        tv_index = (TextView) findViewById(R.id.tv_index);
        tv_index01 = (TextView) findViewById(R.id.tv_index01);
        tv_index02 = (TextView) findViewById(R.id.tv_index02);
        tv_index03 = (TextView) findViewById(R.id.tv_index03);


        llMaView = (LinearLayout) findViewById(R.id.ll_ma);


        mMasterChart.setNoDataText(context.getString(R.string.loading));
        mMasterChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.CANDLE,
                CombinedChart.DrawOrder.LINE
        });
    }

    public void iniChart() {
        if (hasInit) return;
        initMasterChart();//初始化主图
        initBottomChart(mSubChart);//初始化副图
        setOffset();
        hasInit = true;
    }

    public void setSelectListener(OnSelectListener listener) {
        mOnSelectListener = listener;
        initChartListener();
    }

    /**
     * 无数据显示的view
     */
    public void setKLineViewNoData() {
        mMasterChart.setNoDataText(mContext.getString(R.string.temporarily_no_data));
        mSubChart.setNoDataText("");
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
        mMasterChart.setDragDecelerationEnabled(true);
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
                if (mData.size() < INIT_COUNT / 3) {
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


    public void showKdj() {
        showChartKdjData();
    }

    public void showRsi() {
        showChartRsiData();
    }

    public void showMacd() {
        showChartMacdData();
    }

    public void showVolume() {
        showChartVolumeData(false);
    }


    /**
     * @param hisDatas
     * @param type
     */
    public void initData(List<HisData> hisDatas, int type) {

        mData.clear();
        mMasterChart.clear();
        mSubChart.clear();

        mData.addAll(DataUtils.calculateAll(hisDatas));
        isTimeChart = type;
        if (type == CANDEL_CHART) {
            showSMAKline(true);//sma
        } else if (type == TIME_CHART) {
            showTimeLineData(true);
        }

        showChartVolumeData(true);

        setDescription(mMasterChart, "");
        setDescription(mSubChart, "");

    }


    private BarDataSet setBar(ArrayList<BarEntry> barEntries, int type) {
        BarDataSet barDataSet = new BarDataSet(barEntries, "vol");
        barDataSet.setHighLightAlpha(255);
        barDataSet.setHighlightLineWidth(0.5f);
        barDataSet.setHighLightColor(getResources().getColor(R.color.white));
        barDataSet.setDrawValues(false);
        barDataSet.setVisible(type != INVISIABLE_LINE);
        barDataSet.setHighlightEnabled(type != INVISIABLE_LINE);
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
            lineDataSetMa.setColor(ContextCompat.getColor(mContext, R.color.klineMinLine));
            lineDataSetMa.setDrawFilled(true);
            lineDataSetMa.setHighlightEnabled(true);
            lineDataSetMa.setDrawHorizontalHighlightIndicator(true);
            lineDataSetMa.setFillColor(ContextCompat.getColor(mContext, R.color.klineMinLine));
        } else if (type == K) {
            lineDataSetMa.setColor(getResources().getColor(R.color.k));
        } else if (type == D) {
            lineDataSetMa.setColor(getResources().getColor(R.color.d));
        } else if (type == J) {
            lineDataSetMa.setColor(getResources().getColor(R.color.j));
        } else if (type == DIF) {
            lineDataSetMa.setColor(getResources().getColor(R.color.dif));
        } else if (type == DEA) {
            lineDataSetMa.setColor(getResources().getColor(R.color.dea));
        } else if (type == AVG_LINE) {
            lineDataSetMa.setColor(getResources().getColor(R.color.klineMa10));
        } else if (type == MA5) {
            lineDataSetMa.setColor(getResources().getColor(R.color.ma5));
        } else if (type == MA10) {
            lineDataSetMa.setColor(getResources().getColor(R.color.ma10));
        } else if (type == MA20) {
            lineDataSetMa.setColor(getResources().getColor(R.color.ma20));
        } else if (type == EMA7) {
            lineDataSetMa.setColor(getResources().getColor(R.color.ma5));
        } else if (type == EMA30) {
            lineDataSetMa.setColor(getResources().getColor(R.color.ma10));
        } else if (type == RSI6) {
            lineDataSetMa.setColor(getResources().getColor(R.color.rsi6));
        } else if (type == RSI12) {
            lineDataSetMa.setColor(getResources().getColor(R.color.rsi12));
        } else if (type == RSI24) {
            lineDataSetMa.setColor(getResources().getColor(R.color.rsi24));
        } else if (type == UPS) {
            lineDataSetMa.setColor(getResources().getColor(R.color.boll_up));
        } else if (type == MBS) {
            lineDataSetMa.setColor(getResources().getColor(R.color.boll_mb));
        } else if (type == DNS) {
            lineDataSetMa.setColor(getResources().getColor(R.color.boll_dn));
        } else {
            lineDataSetMa.setColor(getResources().getColor(R.color.tran));
            // lineDataSetMa.setVisible(false);
        }

        lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSetMa.setLineWidth(1f);
        lineDataSetMa.setCircleRadius(1f);

        lineDataSetMa.setDrawCircles(false);
        lineDataSetMa.setDrawCircleHole(false);

        lineDataSetMa.setCircleColor(mTransparentColor);

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
        ILineDataSet avgLineSet = lineData.getDataSetByIndex(1);

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
                    avgLineSet.removeEntry(j - 1);

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
            avgLineSet.addEntry(new Entry(index0, (float) hisData.getAvgPrice()));    // 均线

            int index = volSet.getEntryCount();
            volSet.addEntry(new BarEntry(index, (float) hisData.getVol(), hisData));  //成交量
            vma5Set.addEntry(new Entry(index, (float) hisData.getVma5()));// ma数据
            vma10Set.addEntry(new Entry(index, (float) hisData.getVma10()));
        }
        mMasterChart.notifyDataSetChanged();
        mMasterChart.invalidate();

        // 主图刷新
        mSubChart.notifyDataSetChanged();
        mSubChart.invalidate();

    }


    public void addCandleData(List<HisData> hisDatas) {

        if (mData == null || mMasterChart == null) return;
        CombinedData combinedData = mMasterChart.getData();
        if (combinedData == null) return;
        CandleData candleData = combinedData.getCandleData();
        ICandleDataSet candleDataSet = candleData.getDataSetByIndex(0);

        ILineDataSet ma5Set = null;
        ILineDataSet ma10Set = null;
        ILineDataSet ma20Set = null;

        ILineDataSet ema7Set = null;
        ILineDataSet ema30Set = null;

        ILineDataSet upSet = null;
        ILineDataSet mbSet = null;
        ILineDataSet dnSet = null;

        LineData lineData = combinedData.getLineData();
        // lineData
        if (chart01Type == 0) {
            ma5Set = lineData.getDataSetByIndex(0);
            ma10Set = lineData.getDataSetByIndex(1);
            ma20Set = lineData.getDataSetByIndex(2);
        } else if (chart01Type == 1) {
            ema7Set = lineData.getDataSetByIndex(0);
            ema30Set = lineData.getDataSetByIndex(1);
        } else if (chart01Type == 2) {
            upSet = lineData.getDataSetByIndex(0);
            mbSet = lineData.getDataSetByIndex(1);
            dnSet = lineData.getDataSetByIndex(2);
        }


        CombinedData subCombinedData = mSubChart.getData();
        BarData subBarData = subCombinedData.getBarData();
        LineData subLineData = subCombinedData.getLineData();

        IBarDataSet volSet = null;
        ILineDataSet vma5Set = null;
        ILineDataSet vma10Set = null;

        IBarDataSet macdSet = null;
        ILineDataSet difSet = null;
        ILineDataSet deaSet = null;

        IBarDataSet defaultSet = null;

        ILineDataSet kSet = null;
        ILineDataSet dSet = null;
        ILineDataSet jSet = null;

        ILineDataSet rsi6Set = null;
        ILineDataSet rsi12Set = null;
        ILineDataSet rsi24Set = null;


        switch (chart02Type) {
            case 3:
                volSet = subBarData.getDataSetByIndex(0);
                vma5Set = subLineData.getDataSetByIndex(0);
                vma10Set = subLineData.getDataSetByIndex(1);
                break;
            case 4:
                macdSet = subBarData.getDataSetByIndex(0);
                difSet = subLineData.getDataSetByIndex(0);
                deaSet = subLineData.getDataSetByIndex(1);
                break;
            case 5:
                defaultSet = subBarData.getDataSetByIndex(0);
                kSet = subLineData.getDataSetByIndex(0);
                dSet = subLineData.getDataSetByIndex(1);
                jSet = subLineData.getDataSetByIndex(2);
                break;
            case 6:
                defaultSet = subBarData.getDataSetByIndex(0);
                rsi6Set = subLineData.getDataSetByIndex(0);
                rsi12Set = subLineData.getDataSetByIndex(1);
                rsi24Set = subLineData.getDataSetByIndex(2);
                break;
        }


        if (mData != null && mData.size() > 0) {
            for (int j = mData.size(); j > 0; j--) {
                if (hisDatas.get(0).getDate() <= mData.get(j - 1).getDate()) {
                    mData.remove(j - 1);
                    candleDataSet.removeLast();
                    if (ma5Set != null) ma5Set.removeLast();
                    if (ma10Set != null) ma10Set.removeLast();
                    if (ma20Set != null) ma20Set.removeLast();

                    if (ema7Set != null) ema7Set.removeLast();
                    if (ema30Set != null) ema30Set.removeLast();

                    if (upSet != null) upSet.removeLast();
                    if (mbSet != null) mbSet.removeLast();
                    if (dnSet != null) dnSet.removeLast();
                    //-------------副图 指标
                    if (volSet != null) volSet.removeLast();
                    if (vma5Set != null) vma5Set.removeLast();
                    if (vma10Set != null) vma10Set.removeLast();

                    if (macdSet != null) macdSet.removeLast();
                    if (difSet != null) difSet.removeLast();
                    if (deaSet != null) deaSet.removeLast();

                    if (defaultSet != null) defaultSet.removeLast();

                    if (kSet != null) kSet.removeLast();
                    if (dSet != null) dSet.removeLast();
                    if (jSet != null) jSet.removeLast();

                    //rsi
                    if (rsi6Set != null) rsi6Set.removeLast();
                    if (rsi12Set != null) rsi12Set.removeLast();
                    if (rsi24Set != null) rsi24Set.removeLast();

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
            int masterIndex = candleDataSet.getEntryCount();
            candleDataSet.addEntry(new CandleEntry(masterIndex, (float) hisData.getHigh(), (float) hisData.getLow(), (float) hisData.getOpen(), (float) hisData.getClose()));
            if (ma5Set != null) ma5Set.addEntry(new Entry(masterIndex, (float) hisData.getMa5()));
            if (ma10Set != null)
                ma10Set.addEntry(new Entry(masterIndex, (float) hisData.getMa10()));
            if (ma20Set != null)
                ma20Set.addEntry(new Entry(masterIndex, (float) hisData.getMa20()));


            if (ema7Set != null)
                ema7Set.addEntry(new Entry(masterIndex, (float) hisData.getEma7()));
            if (ema30Set != null)
                ema30Set.addEntry(new Entry(masterIndex, (float) hisData.getEma30()));

            if (upSet != null) upSet.addEntry(new Entry(masterIndex, (float) hisData.getUPs()));
            if (mbSet != null) mbSet.addEntry(new Entry(masterIndex, (float) hisData.getMBs()));
            if (dnSet != null) dnSet.addEntry(new Entry(masterIndex, (float) hisData.getDNs()));

            //副图
            if (volSet != null)
                volSet.addEntry(new BarEntry(masterIndex, (float) hisData.getVol(), hisData));
            if (vma5Set != null)
                vma5Set.addEntry(new Entry(masterIndex, (float) hisData.getVma5()));
            if (vma10Set != null)
                vma10Set.addEntry(new Entry(masterIndex, (float) hisData.getVma10()));

            if (macdSet != null)
                macdSet.addEntry(new BarEntry(masterIndex, (float) hisData.getMacd()));
            if (difSet != null) difSet.addEntry(new Entry(masterIndex, (float) hisData.getDif()));
            if (deaSet != null) deaSet.addEntry(new Entry(masterIndex, (float) hisData.getDea()));


            if (defaultSet != null) defaultSet.addEntry(new BarEntry(masterIndex, 0));

            if (kSet != null) kSet.addEntry(new Entry(masterIndex, (float) hisData.getK()));
            if (dSet != null) dSet.addEntry(new Entry(masterIndex, (float) hisData.getD()));
            if (jSet != null) jSet.addEntry(new Entry(masterIndex, (float) hisData.getJ()));

            //rsi
            if (rsi6Set != null)
                rsi6Set.addEntry(new Entry(masterIndex, (float) hisData.getRsiData6()));
            if (rsi12Set != null)
                rsi12Set.addEntry(new Entry(masterIndex, (float) hisData.getRsiData12()));
            if (rsi24Set != null)
                rsi24Set.addEntry(new Entry(masterIndex, (float) hisData.getRsiData24()));

        }
        mMasterChart.notifyDataSetChanged();
        mMasterChart.invalidate();

        // 主图刷新
        mSubChart.notifyDataSetChanged();
        mSubChart.invalidate();

    }


    public void resetMasterChart(boolean resetTouch) {//重置master chart
        mMasterChart.clear();
        mMasterChart.getAxisLeft().resetAxisMaximum();//Y轴重制
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
        LineData lineData = new LineData(
                setLine(NORMAL_LINE, priceEntries),
                setLine(AVG_LINE, aveEntries),
                creatPaddingData());

        CombinedData combinedData = new CombinedData();
        combinedData.setData(lineData);
        mMasterChart.setData(combinedData);

        mMasterChart.notifyDataSetChanged();
        mMasterChart.invalidate();

        setHandler(mMasterChart);

    }

    /**
     * 显示 MA
     */
    public void showSMAKline(boolean resetTouch) {

        chart01Type = 0;
        resetMasterChart(resetTouch);
        ArrayList<Entry> ma5Entries = new ArrayList<>();
        ArrayList<Entry> ma10Entries = new ArrayList<>();
        ArrayList<Entry> ma20Entries = new ArrayList<>();

        for (int i = 0; i < mData.size(); i++) {
            HisData hisData = mData.get(i);

            if (!Double.isNaN(hisData.getMa5())) {
                ma5Entries.add(new Entry(i, (float) hisData.getMa5()));
            }

            if (!Double.isNaN(hisData.getMa10())) {
                ma10Entries.add(new Entry(i, (float) hisData.getMa10()));
            }

            if (!Double.isNaN(hisData.getMa20())) {
                ma20Entries.add(new Entry(i, (float) hisData.getMa20()));
            }

        }

        LineData lineData = new LineData(
                setLine(MA5, ma5Entries),
                setLine(MA10, ma10Entries),
                setLine(MA20, ma20Entries),
                creatPaddingData());

        CombinedData combinedData = new CombinedData();
        combinedData.setData(lineData);
        combinedData.setData(creatCandleData());

        mMasterChart.setData(combinedData);

        mMasterChart.notifyDataSetChanged();
        mMasterChart.invalidate();

        if (resetTouch) setHandler(mMasterChart);

    }

    /**
     * 显示 EMA
     */
    public void showEMAKline() {

        chart01Type = 1;
        resetMasterChart(false);
        ArrayList<Entry> EMA7Entries = new ArrayList<>();
        ArrayList<Entry> EMA30Entries = new ArrayList<>();

        for (int i = 0; i < mData.size(); i++) {
            HisData hisData = mData.get(i);
            EMA7Entries.add(new Entry(i, (float) hisData.getEma7()));
            EMA30Entries.add(new Entry(i, (float) hisData.getEma30()));
        }

        LineData lineData = new LineData(
                setLine(EMA7, EMA7Entries),
                setLine(EMA30, EMA30Entries),
                creatPaddingData());
        CombinedData combinedData = new CombinedData();
        combinedData.setData(lineData);
        combinedData.setData(creatCandleData());
        mMasterChart.setData(combinedData);

        mMasterChart.notifyDataSetChanged();
        mMasterChart.invalidate();


    }

    /**
     * 显示 Bool
     */
    public void showBollKline() {

        chart01Type = 2;
        resetMasterChart(false);
        ArrayList<Entry> upsEntries = new ArrayList<>();
        ArrayList<Entry> mbsEntries = new ArrayList<>();
        ArrayList<Entry> dnsEntries = new ArrayList<>();

        for (int i = 0; i < mData.size(); i++) {
            HisData hisData = mData.get(i);
            if (!Double.isNaN(hisData.getUPs())) {
                upsEntries.add(new Entry(i, (float) hisData.getUPs()));
            }
            if (!Double.isNaN(hisData.getMBs())) {
                mbsEntries.add(new Entry(i, (float) hisData.getMBs()));
            }
            if (!Double.isNaN(hisData.getDNs())) {
                dnsEntries.add(new Entry(i, (float) hisData.getDNs()));
            }
        }

        LineData lineData = new LineData(
                setLine(UPS, upsEntries),
                setLine(MBS, mbsEntries),
                setLine(DNS, dnsEntries),
                creatPaddingData());
        CombinedData combinedData = new CombinedData();
        combinedData.setData(lineData);
        combinedData.setData(creatCandleData());
        mMasterChart.setData(combinedData);

        mMasterChart.notifyDataSetChanged();
        mMasterChart.invalidate();


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
        ArrayList<Entry> ma5Entries = new ArrayList<>();
        ArrayList<Entry> vma10Entries = new ArrayList<>();

        for (int i = 0; i < mData.size(); i++) {
            HisData hisData = mData.get(i);

            if (!Double.isNaN(hisData.getVma5())) {
                ma5Entries.add(new Entry(i, (float) hisData.getVma5()));
            }

            if (!Double.isNaN(hisData.getMa10())) {
                vma10Entries.add(new Entry(i, (float) hisData.getVma10()));
            }
            barEntries.add(new BarEntry(i, (float) hisData.getVol(), hisData));
        }

        BarData barData = new BarData(setBar(barEntries, NORMAL_LINE));

        LineData lineData = new LineData(
                setLine(MA5, ma5Entries),
                setLine(MA10, vma10Entries),
                creatPaddingData());

        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        combinedData.setData(lineData);
        mSubChart.setData(combinedData);


        mSubChart.notifyDataSetChanged();
        mSubChart.invalidate();

        if (resetTouch) setHandler(mSubChart);
    }

    private void showChartMacdData() {
        chart02Type = 4;
        resetSubChart(false);
        mSubChart.getAxisLeft().setStartAtZero(false);
        mSubChart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {//y轴数据的 format
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return DoubleUtil.getStringByDigits(value, mPriceDigits);
            }
        });
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<BarEntry> paddingEntries = new ArrayList<>();
        ArrayList<Entry> difEntries = new ArrayList<>();
        ArrayList<Entry> deaEntries = new ArrayList<>();
        for (int i = 0; i < mData.size(); i++) {
            HisData t = mData.get(i);
            barEntries.add(new BarEntry(i, (float) t.getMacd()));
            difEntries.add(new Entry(i, (float) t.getDif()));
            deaEntries.add(new Entry(i, (float) t.getDea()));
        }
        if (!mData.isEmpty() && mData.size() < MAX_COUNT) {
            for (int i = mData.size(); i < MAX_COUNT; i++) {
                paddingEntries.add(new BarEntry(i, 0));
            }
        }

        BarData barData = new BarData(setBar(barEntries, NORMAL_LINE), setBar(paddingEntries, INVISIABLE_LINE));
        barData.setBarWidth(0.75f);
        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        LineData lineData = new LineData(setLine(DIF, difEntries), setLine(DEA, deaEntries));
        combinedData.setData(lineData);
        mSubChart.setData(combinedData);

        mSubChart.notifyDataSetChanged();
        mSubChart.invalidate();


    }

    private void showChartKdjData() {
        chart02Type = 5;
        resetSubChart(false);
        mSubChart.getAxisLeft().setStartAtZero(false);
        mSubChart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {//y轴数据的 format
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return DoubleUtil.getStringByDigits(value, mPriceDigits);
            }
        });
        ArrayList<Entry> kEntries = new ArrayList<>();
        ArrayList<Entry> dEntries = new ArrayList<>();
        ArrayList<Entry> jEntries = new ArrayList<>();
        ArrayList<BarEntry> barDatasRsiEntries = new ArrayList<>();

        for (int i = 0; i < mData.size(); i++) {
            if (!Double.isNaN(mData.get(i).getK()))
                kEntries.add(new Entry(i, (float) mData.get(i).getK()));
            if (!Double.isNaN(mData.get(i).getD()))
                dEntries.add(new Entry(i, (float) mData.get(i).getD()));
            if (!Double.isNaN(mData.get(i).getJ()))
                jEntries.add(new Entry(i, (float) mData.get(i).getJ()));
            barDatasRsiEntries.add(new BarEntry(i, 0));
        }

        LineData lineData = new LineData(
                setLine(K, kEntries),
                setLine(D, dEntries),
                setLine(J, jEntries),
                creatPaddingData());

        BarData barData = new BarData(setBar(barDatasRsiEntries, NORMAL_LINE));

        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        combinedData.setData(lineData);
        mSubChart.setData(combinedData);

        mSubChart.notifyDataSetChanged();
        mSubChart.invalidate();


    }

    private void showChartRsiData() {
        chart02Type = 6;
        resetSubChart(false);
        mSubChart.getAxisLeft().setAxisMaximum(100.0f);
        mSubChart.getAxisLeft().setStartAtZero(false);
        mSubChart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {//y轴数据的 format
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return DoubleUtil.getStringByDigits(value, mPriceDigits);
            }
        });
        ArrayList<Entry> rsi6Entries = new ArrayList<>();
        ArrayList<Entry> rsi12Entries = new ArrayList<>();
        ArrayList<Entry> rsi24Entries = new ArrayList<>();
        ArrayList<BarEntry> barDatasRsiEntries = new ArrayList<>();

        for (int i = 0; i < mData.size(); i++) {
            if (!Double.isNaN(mData.get(i).getRsiData6()))
                rsi6Entries.add(new Entry(i, (float) mData.get(i).getRsiData6()));
            if (!Double.isNaN(mData.get(i).getRsiData12()))
                rsi12Entries.add(new Entry(i, (float) mData.get(i).getRsiData12()));
            if (!Double.isNaN(mData.get(i).getRsiData24()))
                rsi24Entries.add(new Entry(i, (float) mData.get(i).getRsiData24()));
            barDatasRsiEntries.add(new BarEntry(i, 0));
        }


        BarData barData = new BarData(setBar(barDatasRsiEntries, NORMAL_LINE));

        LineData lineData = new LineData(setLine(RSI6, rsi6Entries),
                setLine(RSI12, rsi12Entries),
                setLine(RSI24, rsi24Entries),
                creatPaddingData());

        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        combinedData.setData(lineData);
        mSubChart.setData(combinedData);


        mSubChart.notifyDataSetChanged();
        mSubChart.invalidate();

    }


    /**
     * align two chart
     */
    private void setOffset() {
        int chartHeight = getResources().getDimensionPixelSize(R.dimen.bottom_chart_height);

        int xAlx = DisplayUtils.dip2px(mContext, 20);
        int right = DisplayUtils.dip2px(mContext, 5);
        mMasterChart.setViewPortOffsets(0, 0, right, chartHeight + xAlx);
        mSubChart.setViewPortOffsets(0, 0, right, 0);
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
//        float lowestVisibleX = chart.getLowestVisibleX();
//        if (lowestVisibleX <= chart.getXAxis().getAxisMinimum()) return;
//        int maxX = (int) chart.getHighestVisibleX();
//        int x = Math.min(maxX, mData.size() - 1);
//        HisData hisData = mData.get(x < 0 ? 0 : x);
//        setDescription(mMasterChart, String.format(Locale.getDefault(), "MA5:%.2f  MA10:%.2f  MA20:%.2f  MA30:%.2f",
//                hisData.getMa5(), hisData.getMa10(), hisData.getMa20(), hisData.getMa30()));
//        setDescription(mMasterChart, "Ave: " + hisData.getAvePrice());
//        setDescription(mChartMacd, String.format(Locale.getDefault(), "MACD:%.2f  DEA:%.2f  DIF:%.2f",
//                hisData.getMacd(), hisData.getDea(), hisData.getDif()));
//        setDescription(mChartKdj, String.format(Locale.getDefault(), "K:%.2f  D:%.2f  J:%.2f",
//                hisData.getK(), hisData.getD(), hisData.getJ()));
    }

    //float lastScale = 1f;

    private void setHandler(AppCombinedChart combinedChart) {

        ViewPortHandler viewPortHandlerBar = combinedChart.getViewPortHandler();
        Matrix matrix = viewPortHandlerBar.getMatrixTouch();

        float scale = 1f;
        if (mData.size() > MAX_COUNT) {
            scale = 2f;
        } else if (mData.size() > INIT_COUNT) {
            scale = mData.size() / (float) INIT_COUNT;
        }
        matrix.postScale(scale, 1f);

        moveChart(combinedChart);

    }


    @Override
    public void onSelect(List<HisData> mDatas, int index) {

        mOnSelectListener.onSelect(mDatas, index);
        int newIndex = index;

        if (null != mDatas && mDatas.size() > 0 && newIndex >= 0 && newIndex < mDatas.size()) {

            llMaView.setVisibility(VISIBLE);

            if (isTimeChart == 1)
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
            } else if (chart02Type == 4) {
                indexStr = DoubleUtil.getStringByDigits(mDatas.get(newIndex).getMacd(), mPriceDigits);
                if (!TextUtils.isEmpty(indexStr))
                    indexStr = String.format(getResources().getString(R.string.k_macd), indexStr);
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
        llMaView.setVisibility(GONE);
        setDescription(mMasterChart, "");//ave
        tv_index.setVisibility(View.GONE);
        tv_index01.setVisibility(View.GONE);
        tv_index02.setVisibility(View.GONE);
        tv_index03.setVisibility(View.GONE);
        mOnSelectListener.unSelect();
    }

    public void cleanAll() {
        mData.clear();
        mMasterChart.clear();
        mSubChart.clear();
    }
}
