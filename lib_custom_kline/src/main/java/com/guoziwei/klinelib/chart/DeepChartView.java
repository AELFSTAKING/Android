package com.guoziwei.klinelib.chart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.guoziwei.klinelib.R;
import com.guoziwei.klinelib.model.HisData;
import com.guoziwei.klinelib.util.DataUtils;
import com.guoziwei.klinelib.util.DisplayUtils;
import com.guoziwei.klinelib.util.DoubleUtil;

import java.util.ArrayList;
import java.util.List;

import io.tick.base.util.JsonUtils;

/**
 * Created by guoziwei on 2017/10/26.
 */
public class DeepChartView extends BaseView implements
        CoupleChartGestureListener.OnAxisChangeListener {


    public static final int NORMAL_LINE = 0;
    /**
     * average line
     */
    public static final int AVE_LINE = 1;

    /**
     * hide line 看见的线
     */
    public static final int INVISIABLE_LINE = 6;


    public static final int BUY_DEEP = 42;
    public static final int SELL_DEEP = 43;


    protected AppCombinedChart mChartPrice;

    protected Context mContext;

    protected OnSelectListener mOnSelectListener;
    XAxis xAxisPrice;
    int separateSize = 0;
    /**
     * last price
     */
    private double mLastPrice;
    /**
     * yesterday close price
     */
    private double mLastClose;
    /**
     * 初始化日线图
     */

    private boolean hasInit;
    private boolean isSmall;

    /**
     * the digits of the symbol
     */

    public DeepChartView(Context context) {
        this(context, null);
    }

    public DeepChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeepChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_deep, this);

        mChartPrice = (AppCombinedChart) findViewById(R.id.price_chart);
        // mChartPrice.setNoDataText(context.getString(R.string.loading));

        //mChartPrice.setNoDataText(mContext.getString(R.string.temporarily_no_data));


    }

    /**
     * @param isSmall true ：大图，在行情界面显示，false：小图，在交易主界面的左下角显示
     */
    public void initDeepChart(boolean isSmall) {
        this.isSmall = isSmall;
        if (hasInit) return;
        initChartPrice();
        setOffset();//chart 偏移量
        if (!isSmall) initChartListener();//手势监听
        hasInit = true;
    }

    public void refreshDigits() {
        LineChartXMarkerView mvx = new LineChartXMarkerView(mContext, mData, mPriceDigits);
        mvx.setChartView(mChartPrice);
        mChartPrice.setXMarker(mvx);
    }

    public void setSelectListener(OnSelectListener listener) {
        mOnSelectListener = listener;
        initChartListener();
    }

    protected void initChartPrice() {

        mChartPrice.setScaleEnabled(true);
        mChartPrice.setDrawBorders(false);
        mChartPrice.setHighlightFullBarEnabled(false);
       /* mMasterChart.setBorderWidth(1);
        mMasterChart.setBorderColor(getResources().getColor(R.color.k_dark_gray_bg));//边线颜色*/
        mChartPrice.setDragEnabled(false);
        mChartPrice.setScaleYEnabled(false);
        mChartPrice.setAutoScaleMinMaxEnabled(true);
        //mMasterChart.setClickable(false);
        mChartPrice.setDoubleTapToZoomEnabled(false);
        mChartPrice.setDragDecelerationEnabled(false);


        if (isSmall) {
            mChartPrice.setTouchEnabled(false);
        } else {
            LineChartXMarkerView mvx = new LineChartXMarkerView(mContext, mData, mPriceDigits);
            mvx.setChartView(mChartPrice);
            mChartPrice.setXMarker(mvx);
            LineChartYMarkerView mv = new LineChartYMarkerView(mContext, mData, mVolDigits, 1);
            mv.setChartView(mChartPrice);

            mChartPrice.setMarker(mv);

        }


        Legend lineChartLegend = mChartPrice.getLegend();
        lineChartLegend.setEnabled(false);


        // x轴
        xAxisPrice = mChartPrice.getXAxis();


        //是否显示X坐标轴上的刻度竖线，默认是true  **格线
        if (isSmall) {
            xAxisPrice.setDrawLabels(false);
            xAxisPrice.setDrawGridLines(false);

        } else {
            xAxisPrice.setDrawLabels(true);
            xAxisPrice.setDrawAxisLine(false);
            xAxisPrice.setDrawGridLines(true);
            xAxisPrice.setGridColor(getResources().getColor(R.color.k_dark_gray_bg));
            xAxisPrice.setGridLineWidth(0.5f);
            xAxisPrice.enableGridDashedLine(10f, 0f,
                    5f);//虚线表示X轴上的刻度竖线(float lineLength, float spaceLength, float phase)三个参数，1
            // .线长，2.虚线间距，3.虚线开始坐标

        }


        xAxisPrice.setTextColor(mAxisColor);
        xAxisPrice.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisPrice.setLabelCount(5, true);
        xAxisPrice.setAvoidFirstLastClipping(true);
        xAxisPrice.setAxisMinimum(-0.5f);

        xAxisPrice.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (mData.isEmpty()) {
                    return "";
                }
                if (value < 0) {
                    value = 0;
                }
                if (value < mData.size()) {
                    Log.i("Tick", "getStringByDigits[" + ((int) value) + "] = " + DoubleUtil.getStringByDigits(
                            mData.get((int) value).getVolDeepPrice(), mPriceDigits));
                    return DoubleUtil.getStringByDigits(mData.get((int) value).getVolDeepPrice(),
                            mPriceDigits);
                }
                return "";
            }
        });


        YAxis axisLeftPrice = mChartPrice.getAxisLeft();

        if (isSmall) {
            axisLeftPrice.setEnabled(false);
            axisLeftPrice.setDrawLabels(false);
            axisLeftPrice.setDrawGridLines(false);
            axisLeftPrice.setLabelCount(4, true);

        } else {
            axisLeftPrice.setDrawGridLines(true);
            axisLeftPrice.setLabelCount(5, true);

        }


        axisLeftPrice.setGridColor(getResources().getColor(R.color.k_dark_gray_bg));
        axisLeftPrice.setGridLineWidth(0.5f);
        axisLeftPrice.enableGridDashedLine(10f, 0f, 5f);
        axisLeftPrice.setStartAtZero(true);

        axisLeftPrice.setDrawAxisLine(false);
        axisLeftPrice.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        axisLeftPrice.setTextColor(mAxisColor);
        axisLeftPrice.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (mData.isEmpty()) {
                    return "";
                }
                if (value == 0) {
                    return "0.0";
                }
                return DoubleUtil.getStringByDigits(value, mVolDigits);
            }
        });

        int[] colorArray = {mAxisColor, mAxisColor, mAxisColor};
        Transformer leftYTransformer = mChartPrice.getRendererLeftYAxis().getTransformer();
        ColorContentYAxisRenderer leftColorContentYAxisRenderer = new ColorContentYAxisRenderer(
                mChartPrice.getViewPortHandler(), mChartPrice.getAxisLeft(), leftYTransformer);
        leftColorContentYAxisRenderer.setLabelColor(colorArray);
        leftColorContentYAxisRenderer.setLabelInContent(true);
        leftColorContentYAxisRenderer.setUseDefaultLabelXOffset(false);
        mChartPrice.setRendererLeftYAxis(leftColorContentYAxisRenderer);


        YAxis axisRightPrice = mChartPrice.getAxisRight();
        if (isSmall) axisRightPrice.setEnabled(false);
        axisRightPrice.setDrawLabels(false);
        //  axisRightPrice.setLabelCount(3, true);

        axisRightPrice.setDrawGridLines(false);
        axisRightPrice.setDrawAxisLine(false);
        axisRightPrice.setTextColor(mAxisColor);
        axisRightPrice.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);


        //        设置标签Y渲染器
        Transformer rightYTransformer = mChartPrice.getRendererRightYAxis().getTransformer();
        ColorContentYAxisRenderer rightColorContentYAxisRenderer = new ColorContentYAxisRenderer(
                mChartPrice.getViewPortHandler(), mChartPrice.getAxisRight(), rightYTransformer);
        rightColorContentYAxisRenderer.setLabelInContent(true);
        rightColorContentYAxisRenderer.setUseDefaultLabelXOffset(false);
        rightColorContentYAxisRenderer.setLabelColor(colorArray);
        mChartPrice.setRendererRightYAxis(rightColorContentYAxisRenderer);

    }

    private void initChartListener() {

        mChartPrice.setOnChartGestureListener(new CoupleChartGestureListener(this, mChartPrice));

        mChartPrice.setOnChartValueSelectedListener(
                new InfoViewListener(mContext, mOnSelectListener, mLastClose, mData));

        mChartPrice.setOnTouchListener(new ChartInfoViewHandler(mChartPrice, mOnSelectListener));
    }

    /**
     * 默认显示分时线
     */
    public void initData(List<HisData> hisDatas, int separateSize) {

        mData.clear();
        mChartPrice.clear();

        mData.addAll(DataUtils.calculateHisData(hisDatas));
        Log.i("Tick", "initData, hisDatas : " + JsonUtils.toJsonString(hisDatas));

        this.separateSize = separateSize;

        setDescription(mChartPrice, "");

    }


    private BarDataSet setBar(ArrayList<BarEntry> barEntries, int type) {
        BarDataSet barDataSet = new BarDataSet(barEntries, "vol");
        barDataSet.setHighLightAlpha(255);
        barDataSet.setHighLightColor(getResources().getColor(R.color.white));
        barDataSet.setDrawValues(false);
        barDataSet.setVisible(type != INVISIABLE_LINE);
        barDataSet.setHighlightEnabled(type != INVISIABLE_LINE);
        barDataSet.setColors(getResources().getColor(R.color.increasing_color),
                getResources().getColor(R.color.decreasing_color));
        return barDataSet;
    }

    @NonNull
    private LineDataSet setLine(int type, ArrayList<Entry> lineEntries) {

        LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "ma" + type);
        lineDataSetMa.setHighLightColor(getResources().getColor(R.color.white));
        lineDataSetMa.setHighlightLineWidth(0.5f);
        lineDataSetMa.setDrawHorizontalHighlightIndicator(true);
        lineDataSetMa.setDrawValues(false);

        if (type == BUY_DEEP) {
            // lineDataSetMa.setColor(getResources().getColor(R.color.clo_00d599));
            lineDataSetMa.setColor(getResources().getColor(R.color.increasing_color));
            lineDataSetMa.setDrawFilled(true);
            //  lineDataSetMa.setFillColor(getResources().getColor(R.color.clo_19594U));
            lineDataSetMa.setFillColor(getResources().getColor(R.color.increasing_color));
            lineDataSetMa.setHighLightColor(Color.WHITE);
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        } else if (type == SELL_DEEP) {
            // lineDataSetMa.setColor(getResources().getColor(R.color.clo_ff542a));
            lineDataSetMa.setColor(getResources().getColor(R.color.decreasing_color));
            lineDataSetMa.setDrawFilled(true);
            //  lineDataSetMa.setFillColor(getResources().getColor(R.color.clo_7c372a));
            lineDataSetMa.setFillColor(getResources().getColor(R.color.decreasing_color));
            lineDataSetMa.setHighLightColor(Color.WHITE);
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
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


    public void showDeep() {
        showDeepView();
    }


    /**
     * deep
     */
    private void showDeepView() {


        //买数据源
        ArrayList<Entry> buyLineEntries = new ArrayList<>();

        //卖数据
        ArrayList<Entry> sellLineEntries = new ArrayList<>();


        // 买数据（x 价格，y数量）
        // for (int i = 0; i < mData.size(); i++)
        for (int i = 0; i < mData.size(); i++) {
            if (i < separateSize) {

                HisData buyHisData = mData.get(i);

                //double buyDate = buyHisData.getClose();   // x
                // 转换了以后
                float buyVol = (float) buyHisData.getVolDeepQuantity(); // y
                Log.i("Tick", "buyVol : " + buyVol);


                Entry buyEntry = new Entry(i, buyVol);


                buyLineEntries.add(buyEntry);


            } else {
                HisData sellHisData = mData.get(i);

                // 转换了以后
                //  double sellDate = sellHisData.getClose();   // x
                float sellVol = (float) sellHisData.getVolDeepQuantity(); // y


                Entry sellEntry = new Entry(i, sellVol);

                sellLineEntries.add(sellEntry);


            }


        }


        ArrayList<ILineDataSet> sets = new ArrayList<>();


        // 买的线
        sets.add(setLine(BUY_DEEP, buyLineEntries));

        // 卖的线
        sets.add(setLine(SELL_DEEP, sellLineEntries));


        LineData lineData = new LineData(sets);

        CombinedData combinedData = new CombinedData();
        combinedData.setData(lineData);

        mChartPrice.setData(combinedData);


        mChartPrice.notifyDataSetChanged();
        mChartPrice.invalidate();
        mChartPrice.setVisibleXRange(mData.size(), INIT_COUNT);


        /*if (mData.size() > MAX_COUNT) {
            setHandler(mMasterChart);
            moveToLast(mMasterChart);
        } else {
            mMasterChart.moveViewToX(0);
        }*/


    }


    /**
     * according to the price to refresh the last data of the chart
     */
    public void refreshData(float price) {
        if (price <= 0 || price == mLastPrice) {
            return;
        }
        mLastPrice = price;
        CombinedData data = mChartPrice.getData();
        if (data == null) {
            return;
        }
        LineData lineData = data.getLineData();
        if (lineData != null) {
            ILineDataSet set = lineData.getDataSetByIndex(0);
            if (set.removeLast()) {
                set.addEntry(new Entry(set.getEntryCount(), price));
            }
        }
        CandleData candleData = data.getCandleData();
        if (candleData != null) {
            ICandleDataSet set = candleData.getDataSetByIndex(0);
            if (set.removeLast()) {
                HisData hisData = mData.get(mData.size() - 1);
                hisData.setClose(price);
                hisData.setHigh(Math.max(hisData.getHigh(), price));
                hisData.setLow(Math.min(hisData.getLow(), price));
                set.addEntry(new CandleEntry(set.getEntryCount(), (float) hisData.getHigh(),
                        (float) hisData.getLow(), (float) hisData.getOpen(), price));

            }
        }
        mChartPrice.notifyDataSetChanged();
        mChartPrice.invalidate();
    }


    /**
     * align two chart
     */
    private void setOffset() {
        int xAlx = DisplayUtils.dip2px(mContext, 20);
        if (isSmall) xAlx = 0;
        mChartPrice.setViewPortOffsets(0, 0, 0, xAlx);
    }


    /**
     * add limit line to chart
     */
    public void setLimitLine(double lastClose) {
        LimitLine limitLine = new LimitLine((float) lastClose);
        limitLine.enableDashedLine(2, 10, 0);
        limitLine.setLineColor(getResources().getColor(R.color.k_dark_gray_bg));
        mChartPrice.getAxisLeft().addLimitLine(limitLine);
    }

    public void setLimitLine() {
        setLimitLine(mLastClose);
    }

    /*public void setLastClose(double lastClose) {
        mLastClose = lastClose;
        mMasterChart.setOnChartValueSelectedListener(new InfoViewListener(mContext, mLastClose,
        mData, mChartInfoView, mSubChart, mChartMacd, mChartKdj));
        mSubChart.setOnChartValueSelectedListener(new InfoViewListener(mContext, mLastClose,
        mData, mChartInfoView, mMasterChart, mChartMacd, mChartKdj));
        mChartMacd.setOnChartValueSelectedListener(new InfoViewListener(mContext, mLastClose,
        mData, mChartInfoView, mMasterChart, mSubChart, mChartKdj));
        mChartKdj.setOnChartValueSelectedListener(new InfoViewListener(mContext, mLastClose,
        mData, mChartInfoView, mMasterChart, mSubChart, mChartMacd));

    }*/


    @Override
    public void onAxisChange(BarLineChartBase chart) {
        float lowestVisibleX = chart.getLowestVisibleX();
        if (lowestVisibleX <= chart.getXAxis().getAxisMinimum()) {
            return;
        }
        int maxX = (int) chart.getHighestVisibleX();
        int x = Math.min(maxX, mData.size() - 1);
        //        HisData hisData = mData.get(x < 0 ? 0 : x);
        //        setDescription(mMasterChart, String.format(Locale.getDefault(), "MA5:%.2f
        //        MA10:%.2f  MA20:%.2f  MA30:%.2f",
        //                hisData.getMa5(), hisData.getMa10(), hisData.getMa20(), hisData.getMa30
        //                ()));
        //        setDescription(mSubChart, "成交量 " + hisData.getVol());
        //        setDescription(mChartMacd, String.format(Locale.getDefault(), "MACD:%.2f  DEA:%
        //        .2f  DIF:%.2f",
        //                hisData.getMacd(), hisData.getDea(), hisData.getDif()));
        //        setDescription(mChartKdj, String.format(Locale.getDefault(), "K:%.2f  D:%.2f
        //        J:%.2f",
        //                hisData.getK(), hisData.getD(), hisData.getJ()));
    }

    private void setHandler(AppCombinedChart combinedChart) {
        final ViewPortHandler viewPortHandlerBar = combinedChart.getViewPortHandler();
        //viewPortHandlerBar.setMaximumScaleX(mData.size());
        Matrix touchmatrix = viewPortHandlerBar.getMatrixTouch();

        if (mData.size() < 150) {
            return;
        }

        final float xscale = mData.size() / 150;
        touchmatrix.postScale(xscale, 1f);
    }

    private float culcMaxscale(float count) {
        float max = 1;
        max = count / 127 * 20;
        return max;
    }

    public void clearDeepViewData() {
        // Tick.Du: Fix deepCharView still keep data of old symbol after change to new symbol
        mData.clear();
        mChartPrice.clear();
    }
}
