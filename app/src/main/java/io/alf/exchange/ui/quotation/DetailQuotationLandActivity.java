package io.alf.exchange.ui.quotation;

import static com.guoziwei.klinelib.chart.KLineView.CANDEL_CHART;
import static com.guoziwei.klinelib.chart.KLineView.TIME_CHART;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.guoziwei.klinelib.chart.KLineView;
import com.guoziwei.klinelib.chart.OnSelectListener;
import com.guoziwei.klinelib.model.HisData;
import com.guoziwei.klinelib.model.SelectData;
import com.guoziwei.klinelib.util.DoubleUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import io.alf.exchange.Constant;
import io.alf.exchange.MqttTopicManager;
import io.alf.exchange.R;
import io.alf.exchange.dialog.PopuKlineType;
import io.alf.exchange.mvp.bean.QuotationHistoryBean;
import io.alf.exchange.mvp.presenter.QuotationKLineDetailPresenter;
import io.alf.exchange.mvp.view.QuotationKLineDetailView;
import io.alf.exchange.ui.adapter.BuyEightAdapter;
import io.alf.exchange.ui.adapter.SellEightAdapter;
import io.alf.exchange.util.CexDataPersistenceUtils;
import io.alf.exchange.util.PriceConvertUtil;
import io.cex.exchange.kotlin.coreutil.LegalAndValuationKt;
import io.cex.mqtt.bean.DepthBean;
import io.cex.mqtt.bean.KLineBean;
import io.cex.mqtt.bean.QuotationBean;
import io.cex.mqtt.bean.SellInfo;
import io.tick.base.eventbus.EventBusCenter;
import io.tick.base.eventbus.EventCode;
import io.tick.base.mvp.MvpActivity;
import io.tick.base.util.DateUtil;
import io.tick.base.util.RxBindingUtils;
import me.jessyan.autosize.internal.CancelAdapt;

public class DetailQuotationLandActivity extends MvpActivity implements QuotationKLineDetailView,
        OnSelectListener, CancelAdapt {

    protected TextView tvMhLine, tvDayLine, tvWeekLine, tvHourLine, tvMinuteLine;

    @BindView(R.id.tv_symbol_title)
    TextView tvSymbol;
    @BindView(R.id.tv_rice)
    TextView tvRice;
    @BindView(R.id.tv_change_percent)
    TextView tvChangePercent;
    @BindView(R.id.tv_valuated_price)
    TextView tvValuatedPrice;
    @BindView(R.id.tv_date)
    TextView tvDate;
    // 买八档
    @BindView(R.id.rv_buy_eight_type)
    RecyclerView rvBuyEightType;
    // 卖八档
    @BindView(R.id.rv_sell_eight_type)
    RecyclerView rvSellEightType;
    @BindView(R.id.cb_land_quo_k_line_sma)
    CheckBox cbSma;
    @BindView(R.id.cb_land_quo_k_line_ema)
    CheckBox cbEma;
    @BindView(R.id.cb_land_quo_k_line_boll)
    CheckBox cbBoll;
    @BindView(R.id.cb_land_quo_k_line_vol)
    CheckBox cbVol;
    @BindView(R.id.cb_land_quo_k_line_macd)
    CheckBox cbMacd;
    @BindView(R.id.cb_land_quo_k_line_kdj)
    CheckBox cbKdj;
    @BindView(R.id.cb_land_quo_k_line_risi)
    CheckBox cbRisi;
    @BindView(R.id.cb_land_mh_type)
    CheckBox cbLandMhType;
    @BindView(R.id.cb_land_day_type)
    CheckBox cbLandDayType;
    @BindView(R.id.cb_land_week_type)
    CheckBox cbLandWeekType;
    @BindView(R.id.cb_land_hour_type)
    CheckBox cbLandHourType;
    @BindView(R.id.cb_one_minutes_type)
    CheckBox cbOneMinutesType;
    @BindView(R.id.v_base_line1)
    View vBaseLine1;
    @BindView(R.id.v_base_line2)
    View vBaseLine2;
    @BindView(R.id.v_base_line3)
    View vBaseLine3;
    @BindView(R.id.v_base_line4)
    View vBaseLine4;
    @BindView(R.id.v_base_line5)
    View vBaseLine5;
    @BindView(R.id.iv_exit_full)
    ImageView ivExitFull;
    // Kline
    @BindView(R.id.kline_land_chart)
    KLineView kLineView;
    @BindView(R.id.iv_hour_k_line_triangle)
    ImageView ivHourKLineTriangle;
    @BindView(R.id.iv_one_minutes_k_line_triangle)
    ImageView ivOneMinutesKLineTriangle;
    // mai'mai
    @BindView(R.id.ll_quo_eight_type)
    LinearLayout llQuoKLineEightType;
    // K线类型（ma）
    @BindView(R.id.rl_land_quo_k_line_type)
    RelativeLayout rlLandQuoKLineType;
    @BindView(R.id.ll_ma)
    LinearLayout llMa;
    @BindView(R.id.tv_select_ll)
    LinearLayout llSelectTv;
    @BindView(R.id.kline_tv_open)
    TextView tvKLDO;
    @BindView(R.id.kline_tv_max)
    TextView tvKLDH;
    @BindView(R.id.kline_tv_min)
    TextView tvKLDL;
    @BindView(R.id.kline_tv_close)
    TextView tvKLDC;
    @BindView(R.id.kline_tv_percent)
    TextView tvKLDChange;
    @BindView(R.id.kline_tv_num)
    TextView tvKLDTxn;
    @BindView(R.id.kline_tv_time)
    TextView tvKLDDate;
    @BindView(R.id.view_kline_tv_ma7)
    TextView tvKMa7;
    @BindView(R.id.view_kline_tv_ma25)
    TextView tvKMa25;
    @BindView(R.id.view_kline_tv_ma99)
    TextView tvKMa99;
    // 右侧布局（Kline 类型）
    @BindView(R.id.rl_kline_right_area)
    RelativeLayout rlKineRightView;
    // Kline 布局
    @BindView(R.id.ll_kline_land_main)
    LinearLayout llChartMainView;
    @BindView(R.id.pb_k_line_loading)
    ProgressBar pbKineLoadView;

    // k线 小时popuwindow
    private PopuKlineType mHourPopWindow;
    // k线 分钟popuwindow
    private PopuKlineType mMinutePopWindow;

    private List<SelectData> hourData, minuteData;
    private List<CheckBox> mTimeTypeCboxList;
    private List<View> bottomLineViewList;
    private List<CheckBox> mKlineTypeCboxList;
    protected List<TextView> mListTvLine = new ArrayList<>();

    // mqtt转化的数据（目前是两条）
    private List<HisData> mqttHisData;
    private List<HisData> mHisData;
    private List<KLineBean> mqttKineData;

    // 当前交易类型
    private QuotationBean quotationBean;
    private String symbol;
    // 买卖八档数据源
    private List<SellInfo> mBuyData;
    private List<SellInfo> mSellData;
    private BuyEightAdapter mBuyAdapter;// 买
    private SellEightAdapter mSellAdapter;// 卖

    private int chart01Type = 0;
    private int chart02Type = 3;
    // 精度
    private int mPriceDigits = 8;//价格精度


    // 当Kine 中 时间类型 (默认是：分时 )
    private String mCurrKLineTimeType = Constant.MIN_15;
    private boolean isMinuteHour;

    private static final String QUOTATION_BEAN = "quotationBean";

    private QuotationKLineDetailPresenter mQuotationKLineDetailPresenter;


    public static void startUpForResult(Activity sourceActivity, String symbol,
            QuotationBean quotationBean, int requestCode) {
        if (TextUtils.isEmpty(symbol)) return;
        Intent intent = new Intent(sourceActivity, DetailQuotationLandActivity.class);
        intent.putExtra(QUOTATION_BEAN, quotationBean);
        sourceActivity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void initPresenter() {
        mQuotationKLineDetailPresenter = registerPresenter(new QuotationKLineDetailPresenter(),
                this);
    }

    @Override
    protected void initData() {
        quotationBean = (QuotationBean) getIntent().getSerializableExtra(QUOTATION_BEAN);
        if (quotationBean == null || TextUtils.isEmpty(quotationBean.symbol)) {
            finish();
            return;
        }
        symbol = quotationBean.symbol;
        //
        tvSymbol.setText(symbol);
        // 限价 24h后涨跌值，234小时涨跌幅
        tvRice.setText(quotationBean.lastPrice);
        //  涨跌价格
        switch (quotationBean.direction) {
            case "-1":
                tvChangePercent.setText(String.format("-%s%%", quotationBean.wavePercent));
                tvChangePercent.setTextColor(ContextCompat.getColor(this, R.color.color_decrease));
                break;
            case "0":
                tvChangePercent.setText(String.format("%s%%", quotationBean.wavePercent));
                tvChangePercent.setTextColor(ContextCompat.getColor(this, R.color.textPrimary));
                break;
            case "1":
                tvChangePercent.setText(String.format("+%s%%", quotationBean.wavePercent));
                tvChangePercent.setTextColor(ContextCompat.getColor(this, R.color.color_increase));
                break;
        }
        if (LegalAndValuationKt.shouldDisplayValuatedPrice(symbol)) {
            tvValuatedPrice.setText(TextUtils.isEmpty(quotationBean.lastUsdPrice) ? "--"
                    : "≈ " + PriceConvertUtil.getUsdtAmount(quotationBean.lastUsdPrice));
            tvValuatedPrice.setVisibility(View.VISIBLE);
        } else {
            tvValuatedPrice.setVisibility(View.GONE);
        }
        tvDate.setText(
                DateUtil.formatDateTime(DateUtil.FMT_STD_DATE_TIME, System.currentTimeMillis()));

        mBuyData = new ArrayList<>();
        mSellData = new ArrayList<>();
        // 放 8个假的数据
        for (int i = 0; i < 8; i++) {
            SellInfo sellBean = new SellInfo();
            SellInfo buyBean = new SellInfo();
            mBuyData.add(buyBean);
            mSellData.add(sellBean);
        }
        // 买
        mBuyAdapter = new BuyEightAdapter();
        mBuyAdapter.setNewData(mBuyData);
        // 卖
        mSellAdapter = new SellEightAdapter();
        mSellAdapter.setNewData(mSellData);
        rvBuyEightType.setLayoutManager(new LinearLayoutManager(this));
        rvBuyEightType.setAdapter(mBuyAdapter);
        rvSellEightType.setLayoutManager(new LinearLayoutManager(this));
        rvSellEightType.setAdapter(mSellAdapter);

        // 初始选中的指标
        mCurrKLineTimeType = CexDataPersistenceUtils.getLatestKLineRange();
        isMinuteHour = CexDataPersistenceUtils.isMinuteHourKLine();
        // 初始指标Tab选中
        initSelectedItem();
        // 服务器获取Kline 数据
        getKLineDataFromServer(symbol, mCurrKLineTimeType);

    }

    private void initSelectedItem() {
        int position = 0;
        if (isMinuteHour) {
            position = 0;
        } else {
            if (TextUtils.equals(mCurrKLineTimeType, Constant.MIN_1)
                    || TextUtils.equals(mCurrKLineTimeType, Constant.MIN_5)
                    || TextUtils.equals(mCurrKLineTimeType, Constant.MIN_15)
                    || TextUtils.equals(mCurrKLineTimeType, Constant.MIN_30)) {
                position = 4;
            } else if (TextUtils.equals(mCurrKLineTimeType, Constant.HOUR_1)
                    || TextUtils.equals(mCurrKLineTimeType, Constant.HOUR_2)
                    || TextUtils.equals(mCurrKLineTimeType, Constant.HOUR_4)
                    || TextUtils.equals(mCurrKLineTimeType, Constant.HOUR_6)
                    || TextUtils.equals(mCurrKLineTimeType, Constant.HOUR_12)) {
                position = 3;
            } else if (TextUtils.equals(mCurrKLineTimeType, Constant.WEEK_1)) {
                position = 2;
            } else if (TextUtils.equals(mCurrKLineTimeType, Constant.DAY_1)) {
                position = 1;
            }
        }
        mTimeTypeCboxList.get(position).setText(
                mQuotationKLineDetailPresenter.getLatestKLineItemTitleRes());
        changeTimeCbBackground(position);
        changeTimeCbBottomLineStatus(position);
    }

    private void changeTimeCbBackground(int checkPos) {
        int size = mTimeTypeCboxList.size();
        for (int i = 0; i < size; i++) {
            if (checkPos == i) {
                mTimeTypeCboxList.get(i).setChecked(true);
            } else {
                mTimeTypeCboxList.get(i).setChecked(false);
            }
        }
    }

    private void changeTimeCbBottomLineStatus(int checkPos) {
        int size = bottomLineViewList.size();
        for (int i = 0; i < size; i++) {
            if (checkPos == i) {
                bottomLineViewList.get(i).setVisibility(View.VISIBLE);
            } else {
                bottomLineViewList.get(i).setVisibility(View.GONE);
            }
        }
    }

    /**
     * 从服务器获取KLine数据
     */
    private void getKLineDataFromServer(String symbol, String kLinTimeRange) {
        mQuotationKLineDetailPresenter.queryQuotationHistory(symbol, kLinTimeRange);
        MqttTopicManager.subscribeKLineData(symbol, kLinTimeRange);
    }

    @Override
    protected int getLayoutId() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        return R.layout.activity_quo_detail_land;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        hourData = new ArrayList<>();
        hourData.add(new SelectData(getResources().getString(R.string.k_1h), Constant.HOUR_1));
        hourData.add(new SelectData(getResources().getString(R.string.k_2h), Constant.HOUR_2));
        hourData.add(new SelectData(getResources().getString(R.string.k_4h), Constant.HOUR_4));
        hourData.add(new SelectData(getResources().getString(R.string.k_6h), Constant.HOUR_6));
        hourData.add(new SelectData(getResources().getString(R.string.k_12h), Constant.HOUR_12));

        minuteData = new ArrayList<>();
        minuteData.add(new SelectData(getResources().getString(R.string.k_1min), Constant.MIN_1));
        minuteData.add(new SelectData(getResources().getString(R.string.k_5min), Constant.MIN_5));
        minuteData.add(new SelectData(getResources().getString(R.string.k_15min), Constant.MIN_15));
        minuteData.add(new SelectData(getResources().getString(R.string.k_30min), Constant.MIN_30));

/*        mListTvLine.add(tvMhLine);
        mListTvLine.add(tvDayLine);
        mListTvLine.add(tvWeekLine);
        mListTvLine.add(tvHourLine);
        mListTvLine.add(tvMinuteLine);*/

        mKlineTypeCboxList = new ArrayList<>();
        mKlineTypeCboxList.add(cbSma);
        mKlineTypeCboxList.add(cbEma);
        mKlineTypeCboxList.add(cbBoll);
        mKlineTypeCboxList.add(cbVol);
        mKlineTypeCboxList.add(cbMacd);
        mKlineTypeCboxList.add(cbKdj);
        mKlineTypeCboxList.add(cbRisi);

        mTimeTypeCboxList = new ArrayList<>();
        mTimeTypeCboxList.add(cbLandMhType);
        mTimeTypeCboxList.add(cbLandDayType);
        mTimeTypeCboxList.add(cbLandWeekType);
        mTimeTypeCboxList.add(cbLandHourType);
        mTimeTypeCboxList.add(cbOneMinutesType);

        bottomLineViewList = new ArrayList<>();
        bottomLineViewList.add(vBaseLine1);
        bottomLineViewList.add(vBaseLine2);
        bottomLineViewList.add(vBaseLine3);
        bottomLineViewList.add(vBaseLine4);
        bottomLineViewList.add(vBaseLine5);
    }

    @Override
    protected void initEvents() {
        // 返回
        RxBindingUtils.clicks(aVoid -> onBackPressed(), ivExitFull);
        // sma (ma5,ma10,ma20)
        RxBindingUtils.clicks(aVoid -> processKLineTypeClickResult(0), cbSma);
        // Ema
        RxBindingUtils.clicks(aVoid -> processKLineTypeClickResult(1), cbEma);
        // mCbBoll
        RxBindingUtils.clicks(aVoid -> processKLineTypeClickResult(2), cbBoll);
        // mVol // 附图：交易量
        RxBindingUtils.clicks((Object aVoid) -> processKLineTypeClickResult(3), cbVol);
        // mCbMacd
        RxBindingUtils.clicks(aVoid -> processKLineTypeClickResult(4), cbMacd);
        // mKdj
        RxBindingUtils.clicks(aVoid -> processKLineTypeClickResult(5), cbKdj);
        // rsi
        RxBindingUtils.clicks(aVoid -> processKLineTypeClickResult(6), cbRisi);
        // 分时
        RxBindingUtils.clicks(aVoid -> {
            if (!isMinuteHour) {
                mQuotationKLineDetailPresenter.saveSelectedKLineItem(
                        mCurrKLineTimeType = Constant.MIN_1, isMinuteHour = true);
                getKLineDataFromServer(symbol, Constant.MIN_1);
            }
            changeTimeCbBackground(0);
            changeTimeCbBottomLineStatus(0);
        }, cbLandMhType);
        // 日线
        RxBindingUtils.clicks(aVoid -> {
            if (!isMatchCurrent(Constant.DAY_1)) {
                mQuotationKLineDetailPresenter.saveSelectedKLineItem(
                        mCurrKLineTimeType = Constant.DAY_1, isMinuteHour = false);
                getKLineDataFromServer(symbol, mCurrKLineTimeType);
            }
            changeTimeCbBackground(1);
            changeTimeCbBottomLineStatus(1);
        }, cbLandDayType);
        // 周线
        RxBindingUtils.clicks(aVoid -> {
            if (!isMatchCurrent(Constant.WEEK_1)) {
                mQuotationKLineDetailPresenter.saveSelectedKLineItem(
                        mCurrKLineTimeType = Constant.WEEK_1, isMinuteHour = false);
                getKLineDataFromServer(symbol, mCurrKLineTimeType);
            }
            changeTimeCbBackground(2);
            changeTimeCbBottomLineStatus(2);
        }, cbLandWeekType);
        // 时线
        RxBindingUtils.clicks(aVoid -> {
            // k线 小时popuwindow
            mHourPopWindow = new PopuKlineType(this, hourData, cbLandHourType, ivHourKLineTriangle,
                    true, true);
            cbLandHourType.setChecked(false);
            mHourPopWindow.showAtLocationTop(cbLandHourType);
            mHourPopWindow.setKlineTimePopListener(new PopuKlineType.KlineTimeTypePopListener() {
                @Override
                public void ontKlineTimeTypeClick(String selTime) {
                    changeTimeCbBackground(3);
                    changeTimeCbBottomLineStatus(3);
                    if (!isMatchCurrent(selTime)) {
                        mQuotationKLineDetailPresenter.saveSelectedKLineItem(
                                mCurrKLineTimeType = selTime, isMinuteHour = false);
                        getKLineDataFromServer(symbol, selTime);
                    }
                }
            });
        }, cbLandHourType);
        RxBindingUtils.clicks(aVoid -> {
            // k线 分钟popuwindow
            mMinutePopWindow = new PopuKlineType(this, minuteData, cbOneMinutesType,
                    ivOneMinutesKLineTriangle, true, true);
            cbOneMinutesType.setChecked(false);
            mMinutePopWindow.showAtLocationTop(cbOneMinutesType);
            mMinutePopWindow.setKlineTimePopListener(new PopuKlineType.KlineTimeTypePopListener() {
                @Override
                public void ontKlineTimeTypeClick(String selTime) {
                    changeTimeCbBackground(4);
                    changeTimeCbBottomLineStatus(4);
                    if (!isMatchCurrent(selTime) || isMinuteHour) {
                        mQuotationKLineDetailPresenter.saveSelectedKLineItem(
                                mCurrKLineTimeType = selTime, isMinuteHour = false);
                        getKLineDataFromServer(symbol, selTime);
                    }
                }
            });
        }, cbOneMinutesType);
    }

    private boolean isMatchCurrent(String targetType) {
        return TextUtils.equals(mCurrKLineTimeType, targetType);
    }

    /**
     * 处理kline 类型 点击结果
     */
    private void processKLineTypeClickResult(int checkPos) {
        // 更改checkbox 的背景色
        changeKLineTypeCbBackground(checkPos);
        // 显示不不同的kline
        showDiffChartView(checkPos);
    }

    private void changeKLineTypeCbBackground(int checkPos) {
        int size = mKlineTypeCboxList.size();
        if (checkPos < 3) {
            for (int i = 0; i < 3; i++) {
                if (checkPos == i) {
                    mKlineTypeCboxList.get(i).setChecked(true);
                } else {
                    mKlineTypeCboxList.get(i).setChecked(false);
                }
            }
        } else {
            for (int i = 3; i < size; i++) {
                if (checkPos == i) {
                    mKlineTypeCboxList.get(i).setChecked(true);
                } else {
                    mKlineTypeCboxList.get(i).setChecked(false);
                }
            }
        }
    }

    /**
     * 显示不同的K线类型
     */
    private void showDiffChartView(int showKLineType) {
        kLineView.post(() -> {
            switch (showKLineType) {
                case 0:
                    chart01Type = 0;
                    kLineView.showSMAKline(false);
                    break;
                case 1:
                    chart01Type = 1;
                    kLineView.showEMAKline();
                    break;
                case 2:
                    chart01Type = 2;
                    kLineView.showBollKline();
                    break;
                case 3:
                    chart02Type = 3;
                    kLineView.showVolume();
                    break;
                case 4:
                    chart02Type = 4;
                    kLineView.showMacd();
                    break;

                case 5:
                    chart02Type = 5;
                    kLineView.showKdj();
                    break;

                case 6:
                    chart02Type = 6;
                    kLineView.showRsi();
                    break;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    public void refreshData() {//refresh
        mQuotationKLineDetailPresenter.queryQuotationHistory(symbol, mCurrKLineTimeType);
    }

    private void processKlineLoadingStatus(boolean isLoading) {
        pbKineLoadView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        rlKineRightView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        llChartMainView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onEventCallback(EventBusCenter event) {
        super.onEventCallback(event);
        if (EventCode.MQTT_TRANSACTION_DEPTH == event.code) {
            processDepthDataFromMqtt((DepthBean) event.data);
        } else if (event.code == EventCode.MQTT_TRANSACTION_KLINE) {
            processKLineDataFromMqtt((KLineBean) event.data);
        }
    }

    private void processKLineDataFromMqtt(KLineBean bean) {
        List<KLineBean> kLineBeanList = new ArrayList();
        kLineBeanList.add(bean);
        String symbol = kLineBeanList.get(0).symbol;
        if (!TextUtils.equals(symbol, this.symbol)) return;
        if (TextUtils.equals(mCurrKLineTimeType, mCurrKLineTimeType)) {//选择当前对应的时间刻度
            mqttKineData = kLineBeanList;
            mqttHisData = getHisData(mqttKineData);//数据解析
            Collections.sort(mqttHisData, (o1, o2) -> {
                if (o1.getDate() > o2.getDate()) {
                    return 1;
                }
                if (o1.getDate() == o2.getDate()) {
                    return 0;
                }
                return -1;
            });
            if (isMinuteHour) {
                kLineView.addTimeLineData(mqttHisData);
            } else {
                kLineView.addCandleData(mqttHisData);
            }
        }
    }

    private void processDepthDataFromMqtt(DepthBean bean) {
        if (mBuyData != null && mSellData != null) {
            List<SellInfo> sellBeanList = bean.asks;
            List<SellInfo> buyBeanList = bean.bids;
            mBuyData.clear();
            mSellData.clear();
            for (int i = 0; i < buyBeanList.size(); i++) {
                if (i == 8) {
                    break;
                } else {
                    mBuyData.add(buyBeanList.get(i));
                }
            }
            for (int i = 0; i < sellBeanList.size(); i++) {
                if (i == 8) {
                    break;
                } else {
                    mSellData.add(sellBeanList.get(i));
                }
            }
            Collections.reverse(mSellData);

            mBuyAdapter.setTotalNum("8");
            mBuyAdapter.setNewData(mBuyData);
            mBuyAdapter.notifyDataSetChanged();

            mSellAdapter.setTotalNum("8");
            mSellAdapter.setNewData(mSellData);
            mSellAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onQueryQuotationHistory(QuotationHistoryBean historyBean) {
        if (historyBean != null && historyBean.quotationHistory.size() > 0) {
            List<KLineBean> dataBeanList = historyBean.quotationHistory;
            mHisData = getHisData(dataBeanList);
            kLineView.iniChart();
            kLineView.setSelectListener(this);
            if (isMinuteHour) {
                kLineView.initData(mHisData, TIME_CHART);
                llQuoKLineEightType.setVisibility(View.VISIBLE);
                rlLandQuoKLineType.setVisibility(View.GONE);
            } else {
                kLineView.initData(mHisData, CANDEL_CHART);
                llQuoKLineEightType.setVisibility(View.GONE);
                rlLandQuoKLineType.setVisibility(View.VISIBLE);
            }
            if (TextUtils.equals(mCurrKLineTimeType, Constant.DAY_1) || TextUtils.equals(
                    mCurrKLineTimeType, Constant.WEEK_1)) {
                kLineView.setDateFormat("MM-dd");
            } else {
                kLineView.setDateFormat("HH:mm");
            }
            changeKLineTypeCbBackground(0);
            changeKLineTypeCbBackground(3);
            kLineView.setLimitLine();
        }

    }

    @Override
    public void onQueryQuotationHistoryError() {
        rlKineRightView.setVisibility(View.GONE);
        llChartMainView.setVisibility(View.GONE);
        kLineView.cleanAll();//请求错误清除数据
    }

    @Override
    public void onChangeRefreshStatus(boolean refresh) {
        processKlineLoadingStatus(refresh);
    }

    private List<HisData> getHisData(List<KLineBean> beans) {
        List<HisData> hisData = new ArrayList<>(beans.size());
        if (beans.size() > 0) {
            for (int i = 0; i < beans.size(); i++) {
                KLineBean bean = beans.get(i);
                HisData data = new HisData();
                data.setClose(Double.parseDouble(String.valueOf(bean.last)));
                data.setOpen(Double.parseDouble(String.valueOf(bean.first)));
                data.setHigh(Double.parseDouble(String.valueOf(bean.max)));
                data.setLow(Double.parseDouble(String.valueOf(bean.min)));
                data.setVol(Double.parseDouble(String.valueOf(bean.quantity)));
                //data.setDealAmount(Double.parseDouble(String.valueOf(bean.dealAmount)));
                //data.setAvgPrice(Double.parseDouble(String.valueOf(bean.minuteAvg)));
                try {
                    data.setDate(bean.time);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                hisData.add(data);
            }
        }
        return hisData;
    }

    @Override
    public void onSelect(List<HisData> dataList, int index) {
        updateText(dataList, index);
    }

    @Override
    public void unSelect() {
        llSelectTv.setVisibility(View.GONE);
        llMa.setVisibility(View.INVISIBLE);
    }

    private void updateText(List<HisData> dataList, int index) {
        llMa.setVisibility(View.VISIBLE);
        llSelectTv.setVisibility(View.VISIBLE);
        if (index >= 0 && index < dataList.size()) {
            HisData kLlineData = dataList.get(index);
            double newFirstPrice = kLlineData.getOpen();// 开盘价
            double newLastPrice = kLlineData.getClose();// 收盘价
            double newMaxPrice = kLlineData.getHigh(); // 最高价
            double newMinPrice = kLlineData.getLow();  // 最低价
            double dealAmount = kLlineData.getDealAmount();// 成交额
            double lastClosePrice = 0;

            if (index > 1) {
                HisData hisData = dataList.get(index - 1);
                lastClosePrice = hisData.getClose();
            } else {
                lastClosePrice = kLlineData.getClose();
            }
            // 时间
            String time = DateUtil.formatDateTime(DateUtil.FMT_YYYY_MM_DD_HH_MM,
                    kLlineData.getDate());
            double percentChange = (newLastPrice - newFirstPrice) / newFirstPrice * 100;

            // 变量
            double firstChange = newFirstPrice - lastClosePrice;
            double lastChange = newLastPrice - lastClosePrice;
            double maxChange = newMaxPrice - lastClosePrice;
            double minChange = newMinPrice - lastClosePrice;

            // 开:当前candle线的开盘价。
            if (firstChange > 0) {
                dynamicSetKlineGusterTxtColor(tvKLDO, R.color.increasing_color);
            } else if (firstChange == 0) {
                dynamicSetKlineGusterTxtColor(tvKLDO, R.color.textSecondary);
            } else if (firstChange < 0) {
                dynamicSetKlineGusterTxtColor(tvKLDO, R.color.decreasing_color);
            }

            // 高:选中的candle线的最高价。
            if (maxChange > 0) {
                dynamicSetKlineGusterTxtColor(tvKLDH, R.color.increasing_color);
            } else if (maxChange == 0) {
                dynamicSetKlineGusterTxtColor(tvKLDH, R.color.textSecondary);
            } else if (maxChange < 0) {
                dynamicSetKlineGusterTxtColor(tvKLDH, R.color.decreasing_color);
            }

            // 低:选中的candle线的最低价。
            if (minChange > 0) {
                dynamicSetKlineGusterTxtColor(tvKLDL, R.color.increasing_color);
            } else if (minChange == 0) {
                dynamicSetKlineGusterTxtColor(tvKLDL, R.color.textSecondary);
            } else if (minChange < 0) {
                dynamicSetKlineGusterTxtColor(tvKLDL, R.color.decreasing_color);
            }

            // 收:选中的candle线的收盘价。
            if (lastChange > 0) {
                dynamicSetKlineGusterTxtColor(tvKLDC, R.color.increasing_color);
            } else if (lastChange == 0) {
                dynamicSetKlineGusterTxtColor(tvKLDC, R.color.textSecondary);
            } else if (lastChange < 0) {
                dynamicSetKlineGusterTxtColor(tvKLDC, R.color.decreasing_color);
            }

            String signStr = "";
            // 收:选中的candle线的收盘价。
            if (percentChange > 0) {
                dynamicSetKlineGusterTxtColor(tvKLDChange, R.color.increasing_color);
                signStr = "+";
            } else if (percentChange == 0) {
                dynamicSetKlineGusterTxtColor(tvKLDChange, R.color.textSecondary);
            } else if (percentChange < 0) {
                dynamicSetKlineGusterTxtColor(tvKLDChange, R.color.decreasing_color);
            }

            // 当前candle线的开盘价。
            tvKLDO.setText(String.format(getResources().getString(R.string.price_open),
                    DoubleUtil.getStringByDigits(newFirstPrice, mPriceDigits)));
            // 选中的candle线的最高价。
            tvKLDH.setText(String.format(getResources().getString(R.string.price_high),
                    DoubleUtil.getStringByDigits(newMaxPrice, mPriceDigits)));
            // 选中的candle线的最低价。
            tvKLDL.setText(String.format(getResources().getString(R.string.price_low),
                    DoubleUtil.getStringByDigits(newMinPrice, mPriceDigits)));
            // 选中的candle线的收盘价
            tvKLDC.setText(String.format(getResources().getString(R.string.price_closed),
                    DoubleUtil.getStringByDigits(newLastPrice, mPriceDigits)));
            //选中的candle线的涨跌幅
            tvKLDChange.setText(String.format(getResources().getString(R.string.price_range),
                    (signStr + DoubleUtil.format2Decimal(percentChange) + "%")));
            //额
            tvKLDTxn.setText(String.format(getResources().getString(R.string.price_txn),
                    DoubleUtil.getStringByDigits(dealAmount, mPriceDigits)));
            //时间
            tvKLDDate.setText(time + "");
        }

        int newIndex = index;
        if (!isMinuteHour && null != dataList && dataList.size() > 0 && newIndex >= 0
                && newIndex < dataList.size()) {
            String s7 = "";
            String s25 = "";
            String s99 = "";
            if (chart01Type == 0) {
                s7 = DoubleUtil.getStringByDigits(dataList.get(newIndex).getMa5(), mPriceDigits);
                if (!TextUtils.isEmpty(s7)) {
                    s7 = String.format(getResources().getString(R.string.k_ma5), s7);
                }
                s25 = DoubleUtil.getStringByDigits(dataList.get(newIndex).getMa10(), mPriceDigits);
                if (!TextUtils.isEmpty(s25)) {
                    s25 = String.format(getResources().getString(R.string.k_ma10), s25);
                }
                s99 = DoubleUtil.getStringByDigits(dataList.get(newIndex).getMa20(), mPriceDigits);
                if (!TextUtils.isEmpty(s99)) {
                    s99 = String.format(getResources().getString(R.string.k_ma20), s99);
                }
                tvKMa7.setTextColor(getResources().getColor(R.color.textPrimary));
                tvKMa25.setTextColor(getResources().getColor(R.color.colorAccent));
                tvKMa99.setTextColor(getResources().getColor(R.color.ma20));
            } else if (chart01Type == 1) {
                s7 = DoubleUtil.getStringByDigits(dataList.get(newIndex).getEma7(), mPriceDigits);
                if (!TextUtils.isEmpty(s7)) {
                    s7 = String.format(getResources().getString(R.string.k_ema7), s7);
                }
                s25 = DoubleUtil.getStringByDigits(dataList.get(newIndex).getEma30(), mPriceDigits);
                if (!TextUtils.isEmpty(s25)) {
                    s25 = String.format(getResources().getString(R.string.k_ema30), s25);
                }
                tvKMa7.setTextColor(getResources().getColor(R.color.ema7));
                tvKMa25.setTextColor(getResources().getColor(R.color.ema30));
            } else if (chart01Type == 2) {
                s7 = DoubleUtil.getStringByDigits(dataList.get(newIndex).getUPs(), mPriceDigits);
                if (!TextUtils.isEmpty(s7)) {
                    s7 = String.format(getResources().getString(R.string.k_ups), s7);
                }
                s25 = DoubleUtil.getStringByDigits(dataList.get(newIndex).getMBs(), mPriceDigits);
                if (!TextUtils.isEmpty(s25)) {
                    s25 = String.format(getResources().getString(R.string.k_mbs), s25);
                }
                s99 = DoubleUtil.getStringByDigits(dataList.get(newIndex).getDNs(), mPriceDigits);
                if (!TextUtils.isEmpty(s99)) {
                    s99 = String.format(getResources().getString(R.string.k_dns), s99);
                }
                tvKMa7.setTextColor(getResources().getColor(R.color.boll_up));
                tvKMa25.setTextColor(getResources().getColor(R.color.boll_mb));
                tvKMa99.setTextColor(getResources().getColor(R.color.boll_dn));
            }
            if (TextUtils.isEmpty(s7)) {
                tvKMa7.setVisibility(View.INVISIBLE);
            } else {
                tvKMa7.setVisibility(View.VISIBLE);
                tvKMa7.setText(s7);
            }
            if (TextUtils.isEmpty(s25)) {
                tvKMa25.setVisibility(View.INVISIBLE);
            } else {
                tvKMa25.setVisibility(View.VISIBLE);
                tvKMa25.setText(s25);
            }
            if (TextUtils.isEmpty(s99)) {
                tvKMa99.setVisibility(View.INVISIBLE);
            } else {
                tvKMa99.setVisibility(View.VISIBLE);
                tvKMa99.setText(s99);
            }
        } else {
            tvKMa7.setVisibility(View.INVISIBLE);
            tvKMa25.setVisibility(View.INVISIBLE);
            tvKMa99.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 动态设置字体颜色
     */
    private void dynamicSetKlineGusterTxtColor(TextView dynamicTV, int dynamicColor) {
        dynamicTV.setTextColor(ContextCompat.getColor(this, dynamicColor));
    }

}
