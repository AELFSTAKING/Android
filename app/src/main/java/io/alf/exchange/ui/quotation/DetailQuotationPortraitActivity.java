package io.alf.exchange.ui.quotation;

import static io.tick.base.eventbus.EventCode.MQTT_TRANSACTION_DEAL;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.tabs.TabLayout;
import com.guoziwei.klinelib.chart.DeepChartView;
import com.guoziwei.klinelib.chart.OnSelectListener;
import com.guoziwei.klinelib.chart.PortraitKLineView;
import com.guoziwei.klinelib.model.HisData;
import com.guoziwei.klinelib.util.DoubleUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import io.alf.exchange.Constant;
import io.alf.exchange.MqttTopicManager;
import io.alf.exchange.R;
import io.alf.exchange.bean.TradeEvent;
import io.alf.exchange.mvp.bean.CurrencyInfoBean;
import io.alf.exchange.mvp.bean.Favorites;
import io.alf.exchange.mvp.bean.QuotationHistoryBean;
import io.alf.exchange.mvp.presenter.FavoritesPresenter;
import io.alf.exchange.mvp.presenter.QueryCurrencyInfoPresenter;
import io.alf.exchange.mvp.presenter.QueryDealListPresenter;
import io.alf.exchange.mvp.presenter.QueryDepthDataPresenter;
import io.alf.exchange.mvp.presenter.QueryDepthStepPresenter;
import io.alf.exchange.mvp.presenter.QueryOrderBookPresenter;
import io.alf.exchange.mvp.presenter.QuotationKLineDetailPresenter;
import io.alf.exchange.mvp.presenter.QuotationsPresenter;
import io.alf.exchange.mvp.view.FavoritesView;
import io.alf.exchange.mvp.view.QueryCurrencyInfoView;
import io.alf.exchange.mvp.view.QueryDealListView;
import io.alf.exchange.mvp.view.QueryDepthDataView;
import io.alf.exchange.mvp.view.QueryDepthStepView;
import io.alf.exchange.mvp.view.QueryOrderBookView;
import io.alf.exchange.mvp.view.QuotationKLineDetailView;
import io.alf.exchange.mvp.view.QuotationsView;
import io.alf.exchange.util.CexDataPersistenceUtils;
import io.alf.exchange.util.FilterHideQuotationUtil;
import io.alf.exchange.util.FilterOrderBookUtil;
import io.alf.exchange.util.PriceConvertUtil;
import io.alf.exchange.util.StringUtils;
import io.alf.exchange.widget.TradeDepthView;
import io.cex.mqtt.bean.DealBean;
import io.cex.mqtt.bean.DepthBean;
import io.cex.mqtt.bean.KLineBean;
import io.cex.mqtt.bean.OrderBookBean;
import io.cex.mqtt.bean.OrderMarketPrice;
import io.cex.mqtt.bean.QuotationBean;
import io.cex.mqtt.bean.SellInfo;
import io.cex.mqtt.bean.StepBean;
import io.tick.base.eventbus.EventBusCenter;
import io.tick.base.eventbus.EventCode;
import io.tick.base.mvp.MvpActivity;
import io.tick.base.ui.ActionSheet;
import io.tick.base.util.BigDecimalUtil;
import io.tick.base.util.DateUtil;
import io.tick.base.util.JsonUtils;
import io.tick.base.util.NetUtils;
import io.tick.base.util.RxBindingUtils;

public class DetailQuotationPortraitActivity extends MvpActivity implements QuotationsView,
        QuotationKLineDetailView, QueryOrderBookView, QueryDealListView, QueryDepthStepView,
        QueryDepthDataView, FavoritesView, QueryCurrencyInfoView, OnSelectListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_favorite)
    ImageView ivFavorite;
    @BindView(R.id.tv_new_price)
    TextView tvNewPrice;
    @BindView(R.id.tv_new_usdPrice)
    TextView tvNewUsdPrice;
    @BindView(R.id.tv_wave_price)
    TextView tvWavePrice;
    @BindView(R.id.tv_wave_percent)
    TextView tvWavePercent;
    @BindView(R.id.tv_per_day_minPrice)
    TextView tvPerDayMinPrice;
    @BindView(R.id.tv_per_day_max_price)
    TextView tvPerDayMaxPrice;
    @BindView(R.id.tv_per_day_quantity)
    TextView tvPerDayQuantity;
    @BindView(R.id.pb_port_k_line_loading)
    ProgressBar pbProgressBar;
    @BindView(R.id.kline_land_chart)
    PortraitKLineView klineView;
    @BindView(R.id.tv_xiaoshu)
    TextView tvXiaoShu;
    //委托订单买
    @BindView(R.id.ll_page1_child1)
    LinearLayout llPage1Child1;
    //委托订单卖
    @BindView(R.id.ll_page1_child2)
    LinearLayout llPage1Child2;
    //最新成交卡页
    @BindView(R.id.ll_real_content)
    LinearLayout llRealContent;
    //委托订单卡页
    @BindView(R.id.ll_page1)
    LinearLayout llPage1;
    //最新成交卡页
    @BindView(R.id.ll_page2)
    LinearLayout llPage2;
    @BindView(R.id.ll_page3)
    RelativeLayout llPage3;
    @BindView(R.id.btn_buy)
    TextView btnBuy;
    @BindView(R.id.btn_sell)
    TextView btnSell;

    @BindView(R.id.rg_kline_type)
    RadioGroup rgKlineType;

    @BindView(R.id.iv_currency_icon)
    SimpleDraweeView ivCurrencyIcon;
    @BindView(R.id.tv_currency_name)
    TextView tvCurrencyName;
    @BindView(R.id.tv_issue_quantity)
    TextView tvIssueQuantity;
    @BindView(R.id.tv_current_quantity)
    TextView tvCurrentQuantity;
    @BindView(R.id.tv_web_link)
    TextView tvWebLink;
    @BindView(R.id.tv_currency_introduction)
    TextView tvCurrencyIntroduction;

    // 深度图
    @BindView(R.id.k_deep_chart)
    DeepChartView mChartDeep;
    //深度图百分比view
    @BindView(R.id.depth_view_desc)
    TradeDepthView mDeepChartDes;
    @BindView(R.id.pb_deep_loading)
    ProgressBar pbDeepLoading;
    // 主图价格Ma
    @BindView(R.id.ll_kine_ma_data)
    LinearLayout llKineMaData;
    @BindView(R.id.iv_fullscreen)
    ImageView ivFullScreen;
    @BindView(R.id.tv_price_title)
    TextView tvPriceTitle;
    @BindView(R.id.tv_buy_quantity_title)
    TextView tvBuyQuantityTitle;
    @BindView(R.id.tv_sell_quantity_title)
    TextView tvSellQuantityTitle;
    @BindView(R.id.tv_buy_sell_price_title)
    TextView tvBuySellPriceTitle;


    private String symbol;
    private QuotationBean quotationBean;
    //步长列表
    private List<StepBean> depthStepList = new ArrayList<>();
    private String depthCode = "00001";
    // 当Kine 中 时间类型 (默认是：分时 这个很重要 mqtt 也会用到)
    private String klineTimeRange = Constant.MIN_15;
    private ArrayList<SellInfo> mBuyBeanList = new ArrayList<>();
    private ArrayList<SellInfo> mSellBeanList = new ArrayList<>();
    // 是否为分时Kline
    private boolean isMinHourKline = false;
    private int currentDepthPosition = 0;
    // 是否有深度图数据
    private boolean isHaveDeepDdata = false;
    private List<String> mFavorites = new ArrayList<>();
    public static final int REQUEST_CODE_FULL_SCREEN = 9999;

    public static final String SYMBOL = "symbol";
    public static final String DIRECTION = "direction";
    public static final String PRICE = "price";
    public static final String USD_PRICE = "usdPrice";
    public static final String WAVE_PRICE = "wavePrice";
    public static final String WAVE_PERCENT = "wavePercent";
    public static final String LOWEST_PRICE = "lowestPrice";
    public static final String HIGHEST_PRICE = "highestPrice";
    public static final String QUANTITY = "quantity";

    private QuotationsPresenter mQuotationsPresenter;
    private QuotationKLineDetailPresenter mQuotationKLineDetailPresenter;
    private QueryOrderBookPresenter mQueryOrderBookPresenter;
    private QueryDealListPresenter mQueryDealListPresenter;
    private QueryDepthStepPresenter mQueryDepthStepPresenter;
    private QueryDepthDataPresenter mQueryDepthDataPresenter;
    private FavoritesPresenter mFavoritesPresenter;
    private QueryCurrencyInfoPresenter mQueryCurrencyInfoPresenter;


    public static void startUp(Context context, String symbol) {
        if (TextUtils.isEmpty(symbol)) return;
        Intent intent = new Intent(context, DetailQuotationPortraitActivity.class);
        intent.putExtra(SYMBOL, symbol);
        context.startActivity(intent);
    }

    public static void startUp(Context context,
            String symbol,
            String price,
            String usdPrice,
            String wavePrice,
            String wavePercent,
            String direction,
            String lowestPrice,
            String highestPrice,
            String quantity) {
        if (TextUtils.isEmpty(symbol)) return;
        Intent intent = new Intent(context, DetailQuotationPortraitActivity.class);
        buildIntent(intent, symbol, price, usdPrice, wavePrice, wavePercent, direction, lowestPrice,
                highestPrice, quantity);
        context.startActivity(intent);
    }

    public static void buildIntent(Intent intent,
            String symbol,
            String price,
            String usdPrice,
            String wavePrice,
            String wavePercent,
            String direction,
            String lowestPrice,
            String highestPrice,
            String quantity) {
        intent.putExtra(SYMBOL, symbol);
        intent.putExtra(PRICE, price);
        intent.putExtra(USD_PRICE, usdPrice);
        intent.putExtra(WAVE_PRICE, wavePrice);
        intent.putExtra(WAVE_PERCENT, wavePercent);
        intent.putExtra(DIRECTION, direction);
        intent.putExtra(HIGHEST_PRICE, highestPrice);
        intent.putExtra(LOWEST_PRICE, lowestPrice);
        intent.putExtra(QUANTITY, quantity);
    }

    @Override
    protected void initPresenter() {
        mQuotationsPresenter = registerPresenter(new QuotationsPresenter(), this);
        mQuotationKLineDetailPresenter = registerPresenter(new QuotationKLineDetailPresenter(),
                this);
        mQueryOrderBookPresenter = registerPresenter(new QueryOrderBookPresenter(), this);
        mQueryDealListPresenter = registerPresenter(new QueryDealListPresenter(), this);
        mQueryDepthStepPresenter = registerPresenter(new QueryDepthStepPresenter(), this);
        mQueryDepthDataPresenter = registerPresenter(new QueryDepthDataPresenter(), this);
        mFavoritesPresenter = registerPresenter(new FavoritesPresenter(), this);
        mQueryCurrencyInfoPresenter = registerPresenter(new QueryCurrencyInfoPresenter(), this);
    }

    private void startUpdateKLine(String symbol, String kLinTimeRange) {
        processKLineChartStatus(false);
        mQuotationKLineDetailPresenter.queryQuotationHistory(symbol, kLinTimeRange);
        MqttTopicManager.subscribeKLineData(this.symbol, kLinTimeRange);
    }

    private void startUpdateDeepView() {
        //processDeepChartStatus(false);
        mQueryDepthDataPresenter.queryDepthData(symbol);
        MqttTopicManager.subscribeDepthData(this.symbol);
    }

    private void startUpdateOrderBook(int pos) {
        if (depthStepList != null && depthStepList.size() > 0) {
            tvXiaoShu.setText(depthStepList.get(pos).decimalValue + "");
            //mChartDeep.setDigits(depthStepList.get(pos).decimalValue,
            //        depthStepList.get(pos).decimalValue);
            this.depthCode = depthStepList.get(pos).code;
            mQueryOrderBookPresenter.queryOrderBook(this.symbol, this.depthCode);
            MqttTopicManager.subscribeOrderBookData(this.symbol, this.depthCode);
        }
    }

    @Override
    protected void initData() {
        // 恢复上次选择的k线指标
        klineTimeRange = mQuotationKLineDetailPresenter.getSelectedKLineItem();
        isMinHourKline = mQuotationKLineDetailPresenter.isMinuteHour();
        initKlineType();
        initDepthStepList(symbol);
        if (depthStepList != null && depthStepList.size() > 0) {
            this.depthCode = depthStepList.get(0).code;
        }
        mQueryDepthStepPresenter.queryDepthStep(symbol);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_quo_detail_portrait;
    }

    @BindView(R.id.tl_tab)
    TabLayout tabLayout;

    private List<String> tabs = new ArrayList<>();

    private void initTabView() {
        tabs.add("最新成交");
        tabs.add("深度");
        tabs.add("简介");
        TextView tabTitle;
        for (int i = 0; i < tabs.size(); i++) {
            //获取tab
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            //给tab设置自定义布局
            Objects.requireNonNull(tab).setCustomView(R.layout.item_tab_home);
            tabTitle = Objects.requireNonNull(tab.getCustomView()).findViewById(R.id.tv_title);
            //填充数据
            tabTitle.setText(tabs.get(i));
            //默认选择第一项
            if (i == 0) {
                tabTitle.setSelected(true);
                tabTitle.setTextSize(16);
                //tabTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tabTitle.setTextColor(getResources().getColor(R.color.textPrimary));
                changeTab();
            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView tabTitle = Objects.requireNonNull(tab.getCustomView()).findViewById(
                        R.id.tv_title);
                tabTitle.setSelected(true);
                tabTitle.setTextSize(16);
                //tabTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tabTitle.setTextColor(getResources().getColor(R.color.textPrimary));
                changeTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tabTitle = Objects.requireNonNull(tab.getCustomView()).findViewById(
                        R.id.tv_title);
                tabTitle.setSelected(true);
                tabTitle.setTextSize(13);
                //tabTitle.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                tabTitle.setTextColor(getResources().getColor(R.color.textSecondary));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void changeTab() {
        if (tabLayout.getSelectedTabPosition() == 0) {
            llPage1.setVisibility(View.VISIBLE);
            llPage2.setVisibility(View.GONE);
            llPage3.setVisibility(View.GONE);
        } else if (tabLayout.getSelectedTabPosition() == 1) {
            llPage1.setVisibility(View.GONE);
            llPage2.setVisibility(View.VISIBLE);
            llPage3.setVisibility(View.GONE);
        } else if (tabLayout.getSelectedTabPosition() == 2) {
            llPage1.setVisibility(View.GONE);
            llPage2.setVisibility(View.GONE);
            llPage3.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        // 返回
        RxBindingUtils.clicks(aVoid -> onBackPressed(), ivBack);
        initSymbol(getIntent());
        initTabView();
    }

    private void initSymbol(Intent intent) {
        this.symbol = intent.getStringExtra(SYMBOL);
        if (!StringUtils.isEmpty(symbol) && (symbol.split("/").length == 2)) {
            String[] currencies = symbol.split("/");
            tvPriceTitle.setText(String.format("价格(%s)", currencies[1]));
            tvBuyQuantityTitle.setText(String.format("买盘 数量(%s)", currencies[0]));
            tvSellQuantityTitle.setText(String.format("数量(%s) 卖盘", currencies[0]));
            tvBuySellPriceTitle.setText(String.format("价格(%s)", currencies[1]));
        }
        MqttTopicManager.subscribeQuotationData(this.symbol);
        MqttTopicManager.subscribeDealData(this.symbol);
        tvTitle.setText(this.symbol);
        ivFavorite.setImageResource(
                isFavorite(symbol) ? R.mipmap.ic_favorite_selected : R.mipmap.ic_favorite_normal);

        String direction = intent.getStringExtra(DIRECTION);
        String price = intent.getStringExtra(PRICE);
        String wavePrice = intent.getStringExtra(WAVE_PRICE);
        if (!TextUtils.isEmpty(direction)) {
            tvWavePrice.setText(TextUtils.isEmpty(wavePrice) ? "--"
                    : PriceConvertUtil.getWavePrice(direction, wavePrice));
            tvWavePrice.setTextColor(PriceConvertUtil.getTextColor(direction));
        } else {
            tvWavePrice.setText(TextUtils.isEmpty(wavePrice) ? "--" : wavePrice);
        }

        String wavePercent = intent.getStringExtra(WAVE_PERCENT);
        if (!TextUtils.isEmpty(wavePercent)) {
            tvWavePercent.setText(PriceConvertUtil.getWavePercent(direction, wavePercent));
            tvWavePercent.setTextColor(PriceConvertUtil.getTextColor(direction));
        } else {
            tvWavePercent.setText(TextUtils.isEmpty(wavePercent) ? "--" : wavePercent);
        }

        String lowestPrice = intent.getStringExtra(LOWEST_PRICE);
        tvPerDayMinPrice.setText(TextUtils.isEmpty(lowestPrice) ? "--" : lowestPrice);

        String highestPrice = intent.getStringExtra(HIGHEST_PRICE);
        tvPerDayMaxPrice.setText(TextUtils.isEmpty(highestPrice) ? "--" : highestPrice);

        String quantity = intent.getStringExtra(QUANTITY);
        tvPerDayQuantity.setText(TextUtils.isEmpty(quantity) ? "--" : quantity);

        //k线图蜡烛图
        klineView.initDayChart();
        klineView.setSelectListener(this);
        // 深度图
        mChartDeep.initDeepChart(false);
        //initPage1();
        //initPage2();
        // 设置字体风格
        //setTxtStyle();
        //processTargetToDeepView(isTargetToDeepView);
    }

    private void showDialogFragment() {
        QuotationsDialogFragment dialogFragment = new QuotationsDialogFragment();
        dialogFragment.show(getSupportFragmentManager(),
                QuotationsDialogFragment.class.getSimpleName());
    }

    @Override
    protected void initEvents() {
        RxBindingUtils.clicks(v -> showDialogFragment(), tvTitle);
        RxBindingUtils.clicks(aVoid -> {
            if (isFavorite(symbol)) {
                mFavoritesPresenter.deleteFavorite(symbol);
            } else {
                mFavoritesPresenter.addFavorite(symbol);
            }
        }, ivFavorite);
        // 买入
        RxBindingUtils.clicks(aVoid -> {
            //重置选中的交易对
            CexDataPersistenceUtils.putCurrentSymbol(this.symbol);
            EventBusCenter.post(EventCode.TRADE, TradeEvent.newBuyEvent(symbol));
            onBackPressed();
        }, btnBuy);

        // 卖出
        RxBindingUtils.clicks(aVoid -> {
            //重置选中的交易对
            CexDataPersistenceUtils.putCurrentSymbol(this.symbol);
            EventBusCenter.post(EventCode.TRADE, TradeEvent.newSellEvent(symbol));
            onBackPressed();
        }, btnSell);
        RxBindingUtils.clicks(v -> {
            if (depthStepList == null || depthStepList.size() == 0) {
                return;
            }
            String[] titles = new String[depthStepList.size()];
            for (int i = 0; i < depthStepList.size(); i++) {
                titles[i] = depthStepList.get(i).decimalValue + getString(R.string.jiweixiaoshu);
            }
            ActionSheet.createBuilder(this, getSupportFragmentManager())
                    .setCancelButtonTitle(R.string.cancel)
                    .setOtherButtonTitles(titles)
                    .setCancelableOnTouchOutside(true)
                    .setListener(new ActionSheet.SampleActionSheetListener() {
                        @Override
                        public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                            if (NetUtils.isNetworkConnected()) {
                                currentDepthPosition = index;
                                startUpdateOrderBook(currentDepthPosition);
                            }
                        }
                    }).show();
        }, tvXiaoShu);

        rgKlineType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_time: {
                        isMinHourKline = true;
                        klineTimeRange = Constant.MIN_1;
                        break;
                    }
                    case R.id.rb_15min: {
                        isMinHourKline = false;
                        klineTimeRange = Constant.MIN_15;
                        break;
                    }
                    case R.id.rb_30min: {
                        isMinHourKline = false;
                        klineTimeRange = Constant.MIN_30;
                        break;
                    }
                    case R.id.rb_1hour: {
                        isMinHourKline = false;
                        klineTimeRange = Constant.HOUR_1;
                        break;
                    }
                    case R.id.rb_4hour: {
                        isMinHourKline = false;
                        klineTimeRange = Constant.HOUR_4;
                        break;
                    }
                    case R.id.rb_1day: {
                        isMinHourKline = false;
                        klineTimeRange = Constant.DAY_1;
                        break;
                    }
                    case R.id.rb_1week: {
                        isMinHourKline = false;
                        klineTimeRange = Constant.WEEK_1;
                        break;
                    }
                }
                startUpdateKLine(symbol, klineTimeRange);
                // 保存选择的k线指标
                mQuotationKLineDetailPresenter.saveSelectedKLineItem(klineTimeRange,
                        isMinHourKline);
            }
        });

        // 全屏
        RxBindingUtils.clicks(aVoid -> {
            DetailQuotationLandActivity.startUpForResult(DetailQuotationPortraitActivity.this,
                    symbol,
                    quotationBean, REQUEST_CODE_FULL_SCREEN);
        }, ivFullScreen);
    }

    private void initKlineType() {
        klineTimeRange = mQuotationKLineDetailPresenter.getSelectedKLineItem();
        switch (klineTimeRange) {
            case Constant.MIN_1: {
                isMinHourKline = true;
                rgKlineType.check(R.id.rb_time);
                break;
            }
            case Constant.MIN_15: {
                isMinHourKline = false;
                rgKlineType.check(R.id.rb_15min);
                break;
            }
            case Constant.MIN_30: {
                isMinHourKline = false;
                rgKlineType.check(R.id.rb_30min);
                break;
            }
            case Constant.HOUR_1: {
                isMinHourKline = false;
                rgKlineType.check(R.id.rb_1hour);
                break;
            }
            case Constant.HOUR_4: {
                isMinHourKline = false;
                rgKlineType.check(R.id.rb_4hour);
                break;
            }
            case Constant.DAY_1: {
                isMinHourKline = false;
                rgKlineType.check(R.id.rb_1day);
                break;
            }
            case Constant.WEEK_1: {
                isMinHourKline = false;
                rgKlineType.check(R.id.rb_1week);
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFavoritesPresenter.queryFavorites();
        mQuotationsPresenter.querySymbolQuotation();
        startUpdateKLine(symbol, klineTimeRange);
        startUpdateDeepView();
        startUpdateOrderBook(currentDepthPosition);
        mQueryDealListPresenter.queryDealList(symbol);
        mQueryDepthStepPresenter.queryDepthStep(symbol);
        if (!StringUtils.isEmpty(symbol)) {
            mQueryCurrencyInfoPresenter.queryCurrencyInfo(symbol.split("/")[0]);
        }
    }

    @Override
    public void onQuerySymbolQuotation(List<QuotationBean> quotationList) {
        if (quotationList != null && quotationList.size() > 0) {
            for (QuotationBean bean : quotationList) {
                if (TextUtils.equals(bean.symbol, this.symbol)) {
                    quotationBean = bean;
                    updateQuotation(bean);
                    break;
                }
            }
        }
    }

    private void updateQuotation(QuotationBean bean) {
        if (bean != null) {
            tvTitle.setText(bean.symbol);
            tvNewPrice.setText(bean.lastPrice);
            tvNewPrice.setTextColor(PriceConvertUtil.getTextColor(bean.direction));
            tvNewUsdPrice.setText("≈ " + PriceConvertUtil.getUsdtAmount(bean.lastUsdPrice));
            tvWavePrice.setText(PriceConvertUtil.getWavePrice(bean.direction, bean.wavePrice));
            tvWavePrice.setTextColor(PriceConvertUtil.getTextColor(bean.direction));
            tvWavePercent.setText(
                    PriceConvertUtil.getWavePercent(bean.direction, bean.wavePercent));
            tvWavePercent.setTextColor(PriceConvertUtil.getTextColor(bean.direction));
            tvPerDayMinPrice.setText(bean.lowestPrice);
            tvPerDayMaxPrice.setText(bean.highestPrice);
            tvPerDayQuantity.setText(bean.quantity);
        }
    }

    @Override
    public void onQueryQuotationHistory(QuotationHistoryBean historyBean) {
        if (historyBean != null && historyBean.quotationHistory.size() > 0) {
            List<HisData> hisDataList = getHisData(historyBean.quotationHistory);
            klineView.initData(hisDataList, isMinHourKline);
            if (TextUtils.equals(klineTimeRange, Constant.DAY_1)
                    || TextUtils.equals(klineTimeRange, Constant.WEEK_1)) {
                klineView.setDateFormat("MM-dd");
            } else {
                klineView.setDateFormat("HH:mm");
            }
            klineView.setLimitLine();
            processKLineChartStatus(true);
        } else {
            processKLineChartStatus(true);
            klineView.setKLineViewNoData();
        }
    }

    @Override
    public void onQueryQuotationHistoryError() {
        klineView.clearKLineViewData();//请求错误清除数据
    }

    @Override
    public void onChangeRefreshStatus(boolean isRefresh) {
        processLoadingStatus(isRefresh);
    }

    private void processLoadingStatus(boolean loading) {
        pbProgressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (loading) {
            klineView.setVisibility(View.GONE);
        } else {
            klineView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 处理Kilne状态
     */
    private void processKLineChartStatus(boolean showKine) {
        pbProgressBar.setVisibility(showKine ? View.GONE : View.VISIBLE);
        klineView.setVisibility(showKine ? View.VISIBLE : View.GONE);
    }

    /**
     * 处理深度图状态
     */
    private void processDeepChartStatus(boolean isShowDeep) {
        pbDeepLoading.setVisibility(isShowDeep && isHaveDeepDdata ? View.GONE : View.VISIBLE);
        mChartDeep.setVisibility(isShowDeep && isHaveDeepDdata ? View.VISIBLE : View.GONE);
        mDeepChartDes.setVisibility(isShowDeep && isHaveDeepDdata ? View.VISIBLE : View.GONE);
    }

    @NonNull
    private List<HisData> getHisData(List<KLineBean> dataBeanList) {
        List<HisData> hisData = new ArrayList<>(dataBeanList.size());
        if (dataBeanList.size() > 0) {
            for (int i = 0; i < dataBeanList.size(); i++) {
                KLineBean bean = dataBeanList.get(i);
                HisData data = new HisData();
                if (!TextUtils.isEmpty(bean.last)) {
                    data.setClose(Double.parseDouble(String.valueOf(bean.last)));
                }
                if (!TextUtils.isEmpty(bean.first)) {
                    data.setOpen(Double.parseDouble(String.valueOf(bean.first)));
                }
                if (!TextUtils.isEmpty(bean.max)) {
                    data.setHigh(Double.parseDouble(String.valueOf(bean.max)));
                }
                if (!TextUtils.isEmpty(bean.min)) {
                    data.setLow(Double.parseDouble(String.valueOf(bean.min)));
                }
                if (!TextUtils.isEmpty(bean.quantity)) {
                    data.setVol(Double.parseDouble(String.valueOf(bean.quantity)));
                }
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
    public void onQueryOrderBook(OrderBookBean orderBookBean) {
        orderBookBean = FilterOrderBookUtil.filterOrderBook(this.symbol, this.depthCode,
                orderBookBean);
        if (orderBookBean == null) {
            return;
        }
        updateLatestPrice(orderBookBean);
        if (llPage1Child1.getChildCount() != 15) {
            initPage1();
        }
        if (llPage1Child2.getChildCount() != 15) {
            initPage1();
        }
        if ((orderBookBean.bidList == null || orderBookBean.bidList.size() == 0)
                && (orderBookBean.askList == null || orderBookBean.askList.size() == 0)) {
            return;
        }
        List<OrderMarketPrice> buyList = null;
        orderBookBean.bidList.removeAll(Collections.singleton(null));
        int deful = 15;
        if (orderBookBean.bidList.size() >= deful) {
            buyList = orderBookBean.bidList.subList(0, deful);
        } else {
            int bidSize = deful - orderBookBean.bidList.size();
            for (int i = 0; i < bidSize; i++) {
                orderBookBean.bidList.add(new OrderMarketPrice());
            }
            buyList = orderBookBean.bidList;
        }
        List<OrderMarketPrice> sellList = null;
        orderBookBean.askList.removeAll(Collections.singleton(null));
        Collections.sort(orderBookBean.askList, (o1, o2) -> {
            if (o1 != null && o2 != null) {
                double v1 = 0;
                double v2 = 0;
                if (!TextUtils.isEmpty(o1.limitPrice)) {
                    v1 = Double.parseDouble(o1.limitPrice);
                }
                if (!TextUtils.isEmpty(o2.limitPrice)) {
                    v2 = Double.parseDouble(o2.limitPrice);
                }
                return Double.compare(v2, v1);
            } else {
                return 0;
            }
        });

        if (orderBookBean.askList.size() >= deful) {
            sellList = orderBookBean.askList.subList(0, deful);
            Collections.reverse(sellList);
        } else {
            Collections.reverse(orderBookBean.askList);
            int askSize = deful - orderBookBean.askList.size();
            for (int i = 0; i < askSize; i++) {
                orderBookBean.askList.add(new OrderMarketPrice());
            }
            sellList = orderBookBean.askList;
        }

        double sumTotalBid = 0;
        for (OrderMarketPrice value : buyList) {
            if (value != null && !TextUtils.isEmpty(value.quantity)) {
                sumTotalBid = Double.parseDouble(value.quantity) + sumTotalBid;
                value.sum = sumTotalBid;
            }
        }

        double sumTotalAsk = 0;
        for (OrderMarketPrice value : sellList) {
            if (value != null && !TextUtils.isEmpty(value.quantity)) {
                sumTotalAsk = Double.parseDouble(value.quantity) + sumTotalAsk;
                value.sum = sumTotalAsk;
            }
        }

        double totalNum = 0;
        if (sumTotalBid > sumTotalAsk) {
            totalNum = sumTotalBid;
        } else {
            totalNum = sumTotalAsk;
        }
        for (int i = 0; i < llPage1Child1.getChildCount(); i++) {
            View view = llPage1Child1.getChildAt(i);
            ProgressBar bar = view.findViewById(R.id.progressBar);
            TextView tv_price = view.findViewById(R.id.tv_price);
            TextView tv_price_right = view.findViewById(R.id.tv_price_right);
            //FontUtil.setDinMediumFont(tv_price);
            //FontUtil.setDinMediumFont(tv_price_right);
            OrderMarketPrice value = buyList.get(i);
            if (value == null) {
                tv_price.setText("--");
                tv_price_right.setText("--");
                bar.setProgress(0);
            } else {
                tv_price.setText(TextUtils.isEmpty(value.quantity) ? "--" : value.quantity);
                tv_price_right.setText(
                        TextUtils.isEmpty(value.limitPrice) ? "--" : value.limitPrice);
                // double curNum = value.sum;
                double curNum = 0;
                if (!TextUtils.isEmpty(value.quantity)) {
                    curNum = Double.parseDouble(value.quantity);
                }
                if (totalNum != 0) {
                    try {
                        int currentProgress = (int) (Double.parseDouble(
                                BigDecimalUtil.div(curNum + "", totalNum + "", 2)) * 100);
                        bar.setProgress(currentProgress);
                    } catch (Exception e) {
                        bar.setProgress(0);
                        bar.setProgressDrawable(
                                getResources().getDrawable(R.drawable.progress_area_statistic3));
                    }
                } else {
                    bar.setProgress(0);
                    bar.setProgressDrawable(
                            getResources().getDrawable(R.drawable.progress_area_statistic3));
                }
            }
        }

        for (int i = 0; i < llPage1Child2.getChildCount(); i++) {
            View view = llPage1Child2.getChildAt(i);
            ProgressBar bar = view.findViewById(R.id.progressBar);
            TextView tv_price = view.findViewById(R.id.tv_price);
            TextView tv_price_right = view.findViewById(R.id.tv_price_right);
            //FontUtil.setDinMediumFont(tv_price);
            //FontUtil.setDinMediumFont(tv_price_right);
            OrderMarketPrice value = sellList.get(i);

            if (value == null) {
                tv_price_right.setText("--");
                tv_price.setText("--");
                bar.setProgress(0);
            } else {
                tv_price_right.setText(TextUtils.isEmpty(value.quantity) ? "--" : value.quantity);
                tv_price.setText(TextUtils.isEmpty(value.limitPrice) ? "--" : value.limitPrice);
                //double curNum = value.sum;
                double curNum = 0;
                if (!TextUtils.isEmpty(value.quantity)) {
                    curNum = Double.parseDouble(value.quantity);
                }
                if (totalNum != 0) {
                    try {
                        int currentProgress = (int) (Double.parseDouble(
                                BigDecimalUtil.div(curNum + "", totalNum + "", 2)) * 100);
                        bar.setProgress(currentProgress);
                        bar.setProgressDrawable(
                                getResources().getDrawable(R.drawable.progress_area_statistic4));
                    } catch (Exception e) {
                        bar.setProgress(0);
                        bar.setProgressDrawable(
                                getResources().getDrawable(R.drawable.progress_area_statistic3));
                    }
                } else {
                    bar.setProgress(0);
                    bar.setProgressDrawable(
                            getResources().getDrawable(R.drawable.progress_area_statistic3));
                }
            }
        }
    }

    private void initPage1() {
        LayoutInflater inflater = LayoutInflater.from(this);
        llPage1Child1.removeAllViews();
        llPage1Child2.removeAllViews();
        for (int i = 0; i < 15; i++) {
            View child = inflater.inflate(R.layout.item_kline_order_child, null);
            View child2 = inflater.inflate(R.layout.item_kline_order_child2, null);
            llPage1Child1.addView(child);
            llPage1Child2.addView(child2);
        }
    }

    private void initPage2() {
        LayoutInflater inflater = LayoutInflater.from(this);
        llRealContent.removeAllViews();
        for (int i = 0; i < 12; i++) {
            View child = inflater.inflate(R.layout.item_kline_real_child, null);
            llRealContent.addView(child);
        }
    }

    private void updateLatestPrice(OrderBookBean orderBookBean) {
        String price = orderBookBean.latestDeal.price;
        tvNewPrice.setText(TextUtils.isEmpty(price) ? "--" : price);
        tvNewPrice.setTextColor(
                PriceConvertUtil.getTextColor("" + orderBookBean.latestDeal.direction));
        String usdPrice = orderBookBean.latestDeal.usdPrice;
        tvNewUsdPrice.setText(TextUtils.isEmpty(usdPrice) ? "--"
                : "≈ " + PriceConvertUtil.getUsdtAmount(usdPrice));
    }

    @Override
    public void onQueryDealList(String symbol, List<DealBean> dealBeanList) {
        if (llRealContent.getChildCount() != 12) {
            initPage2();
        }
        List<DealBean> mlist = new ArrayList<>();
        int deful = 12;
        if (dealBeanList == null) {
            for (int i = 0; i < 12; i++) {
                clearPage2Data();
            }
        } else {
            if (dealBeanList.size() >= deful) {
                mlist = dealBeanList.subList(0, deful);
            } else {
                for (int i = 0; i < deful; i++) {
                    if (i < dealBeanList.size()) {
                        mlist.add(dealBeanList.get(i));
                    } else {
                        mlist.add(new DealBean());
                    }
                }
            }
            for (int i = 0; i < mlist.size(); i++) {
                View view = llRealContent.getChildAt(i);

                TextView tv_time = (TextView) view.findViewById(R.id.tv_time);
                TextView tv_action = (TextView) view.findViewById(R.id.tv_action);
                TextView tv_price = (TextView) view.findViewById(R.id.tv_price);
                TextView tv_number = (TextView) view.findViewById(R.id.tv_number);

                DealBean item = mlist.get(i);

                if (item == null || TextUtils.isEmpty(item.action)) {
                    tv_action.setTextColor(getResources().getColor(R.color.color_decrease));
                    tv_action.setText("--");
                    tv_number.setText("--");
                    tv_time.setText("--");
                    tv_price.setText("--");
                } else {
                    String action = item.action;
                    Resources resources = getResources();
                    if ("BUY".equals(action)) {
                        tv_action.setText("买入");
                        tv_action.setTextColor(resources.getColor(R.color.color_increase));
                    } else if ("SELL".equals(action)) {
                        tv_action.setText("卖出");
                        tv_action.setTextColor(resources.getColor(R.color.color_decrease));
                    }
                    String price = item.price;
                    long utcDeal = item.utcDeal;
                    String quantity = item.quantity;
                    tv_number.setText(TextUtils.isEmpty(quantity) ? "--" : quantity);
                    tv_time.setText(utcDeal == 0 ? "--"
                            : DateUtil.formatDateTime(DateUtil.FMT_STD_TIME, utcDeal));
                    tv_price.setText(TextUtils.isEmpty(price) ? "--" : price);
                }
            }
        }
    }

    private void clearPage2Data() {
        for (int i = 0; i < llRealContent.getChildCount(); i++) {
            View view = llRealContent.getChildAt(i);
            TextView tv_price = (TextView) view.findViewById(R.id.tv_price);
            TextView tv_time = (TextView) view.findViewById(R.id.tv_time);
            TextView tv_number = (TextView) view.findViewById(R.id.tv_number);
            tv_number.setText("--");
            tv_time.setText("--");
            tv_price.setText("--");
            tv_price.setTextColor(getResources().getColor(R.color.color_decrease));
        }
    }

    @Override
    public void onQueryDepthStep(List<String> data) {
        initDepthStepList(symbol);
        if (depthStepList != null && depthStepList.size() > 0) {
            currentDepthPosition = depthStepList.size() - 1;
            startUpdateOrderBook(currentDepthPosition);
        }
    }

    private void initDepthStepList(String symbol) {
        List<String> stepsList = CexDataPersistenceUtils.getDepthStepList(symbol);
        if (stepsList != null && stepsList.size() > 0) {
            List<StepBean> stepBeans = new ArrayList<>();
            for (String step : stepsList) {
                if (!TextUtils.isEmpty(step)) {
                    StepBean bean = new StepBean();
                    bean.code = step;
                    bean.decimalValue = step.length() - 1;
                    bean.type = 2;
                    stepBeans.add(bean);
                }
            }
            this.depthStepList.clear();
            this.depthStepList.addAll(stepBeans);
        }
    }

    @Override
    public void onQueryDepthData(DepthBean depthBean) {
        Log.i("onQueryDepthData", "xMinPrice : " + depthBean.xMinPrice + ", xMaxPrice : " + depthBean.xMaxPrice);
        Log.i("onQueryDepthData", "bids : " + JsonUtils.toJsonString(depthBean.bids));
        Log.i("onQueryDepthData", "asks : " + JsonUtils.toJsonString(depthBean.asks));
        List<SellInfo> buyBeanList = depthBean.bids;
        List<SellInfo> sellBeanList = depthBean.asks;
        if (buyBeanList == null || sellBeanList == null) {
            return;
        }
        //显示深度百分比
        String depthPercent = depthBean.depthPercent;
        depthPercent = StringUtils.isEmpty(depthPercent) ? "--" : depthPercent + "%";
        //最大买单价
        String maxPrice = depthBean.bidsMaxPrice;
        maxPrice = StringUtils.isEmpty(maxPrice) ? "--" : maxPrice;
        //最小卖单价
        String minPrice = depthBean.asksMinPrice;
        minPrice = TextUtils.isEmpty(minPrice) ? "--" : minPrice;
        mDeepChartDes.setTvDepth(depthPercent, maxPrice, minPrice);
        mBuyBeanList.clear();
        mSellBeanList.clear();

        if (buyBeanList.size() > 0) mBuyBeanList.addAll(buyBeanList);
        if (sellBeanList.size() > 0) mSellBeanList.addAll(sellBeanList);

        int buySize = mBuyBeanList.size();
        int sellSize = mSellBeanList.size();

        if (buySize > 0 || sellSize > 0) isHaveDeepDdata = true;

        Collections.reverse(mBuyBeanList);

        depthBean.yQuantity = StringUtils.isEmpty(depthBean.yQuantity) ? "0" : depthBean.yQuantity;

        List<HisData> hisDataList = new ArrayList<>();
        int divierSize = mBuyBeanList.size();

        for (SellInfo buyBean : mBuyBeanList) {
            HisData buyHisData = new HisData();
            double buyQuantity = Double.parseDouble(
                    StringUtils.isEmpty(buyBean.quantity) ? "0" : buyBean.quantity);
            double buyPrice = Double.parseDouble(
                    StringUtils.isEmpty(buyBean.price) ? "0" : buyBean.price);
            if (!HisData.stopTest) {
                Log.i("Dupeng", "quantity" + buyBean.quantity);
            }
            buyHisData.setVolDeepQuantity(buyQuantity);
            if (!HisData.stopTest) {
                Log.i("Dupeng", "price" + buyBean.price);
            }
            buyHisData.setVolDeepPrice(buyPrice);
            hisDataList.add(buyHisData);
        }
        HisData.stopTest = true;

        for (SellInfo sellBean : mSellBeanList) {
            HisData sellHisData = new HisData();
            double sellQuantity = Double.parseDouble(
                    StringUtils.isEmpty(sellBean.quantity) ? "0" : sellBean.quantity);
            double sellPrice = Double.parseDouble(
                    StringUtils.isEmpty(sellBean.price) ? "0" : sellBean.price);
            sellHisData.setVolDeepQuantity(sellQuantity);
            sellHisData.setVolDeepPrice(sellPrice);
            hisDataList.add(sellHisData);
        }

        mChartDeep.initData(hisDataList, divierSize);
        mChartDeep.showDeep();
        //processDeepChartStatus(true);
    }

    @Override
    public void onAddFavorite(String symbol, boolean success) {
        mFavoritesPresenter.queryFavorites();
    }

    @Override
    public void onDeleteFavorite(String symbol, boolean success) {
        mFavoritesPresenter.queryFavorites();
    }

    @Override
    public void onQueryFavorites(Favorites data) {
        if (data != null && data.getSymbolList() != null) {
            mFavorites.clear();
            mFavorites.addAll(data.getSymbolList());
        }
        ivFavorite.setImageResource(
                isFavorite(symbol) ? R.mipmap.ic_favorite_selected : R.mipmap.ic_favorite_normal);
    }

    private boolean isFavorite(String symbol) {
        if (mFavorites != null && mFavorites.size() > 0) {
            for (String favorSymbol : mFavorites) {
                if (TextUtils.equals(symbol, favorSymbol)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onEventCallback(EventBusCenter event) {
        super.onEventCallback(event);
        if (EventCode.MQTT_TRANSACTION_DEPTH == event.code) {
            DepthBean depthBean = (DepthBean) event.data;
            if (TextUtils.equals(this.symbol, depthBean.symbol)) {
                onQueryDepthData(depthBean);
            }
        } else if (event.code == MQTT_TRANSACTION_DEAL) {
            List<DealBean> dealBeanList = (List<DealBean>) event.data;
            if (llPage2.isShown() && dealBeanList != null && dealBeanList.size() > 0) {
                if (TextUtils.equals(this.symbol, dealBeanList.get(0).symbol)) {
                    onQueryDealList("", dealBeanList);
                }
            }
        } else if (event.code == EventCode.MQTT_SYMBOL_QUOTATION) {
            QuotationBean bean = (QuotationBean) event.data;
            if (TextUtils.equals(bean.symbol, this.symbol)) {
                if (!FilterHideQuotationUtil.hideQuotation(bean)) {
                    quotationBean = bean;
                    updateQuotation(quotationBean);
                }
            }
        } else if (event.code == EventCode.MQTT_ORDER_BOOK) {
            OrderBookBean orderBookBean = FilterOrderBookUtil.filterOrderBook(this.symbol,
                    this.depthCode, (OrderBookBean) event.data);
            if (orderBookBean != null) {
                updateLatestPrice(orderBookBean);
                if (llPage1.isShown()) {
                    onQueryOrderBook(orderBookBean);
                }
            }
        } else if (event.code == EventCode.MQTT_TRANSACTION_KLINE) {
            KLineBean kLineBean = (KLineBean) event.data;
            if (StringUtils.equals(this.symbol, kLineBean.symbol)) {
                if (klineView.getVisibility() == View.VISIBLE) {
                    processKLineDataFromMqtt(kLineBean);
                }
            }
        } else if (event.code == EventCode.UPDATE_DEFAULT_SYMBOL) {
            Intent intent = new Intent();
            intent.putExtra(SYMBOL, CexDataPersistenceUtils.getCurrentSymbol());
            initSymbol(intent);
            initData();
            onResume();
        }
    }

    private void processKLineDataFromMqtt(KLineBean bean) {
        List<KLineBean> mqttKLineBeans = new ArrayList();
        mqttKLineBeans.add(bean);
        String symbol = mqttKLineBeans.get(0).symbol;
        if (!StringUtils.equals(symbol, this.symbol)) return;
        List<HisData> hisDataList = getHisData(mqttKLineBeans);//数据解析
        Collections.sort(hisDataList, (o1, o2) -> {
            if (o1.getDate() > o2.getDate()) {
                return 1;
            }
            if (o1.getDate() == o2.getDate()) {
                return 0;
            }
            return -1;
        });
        if (isMinHourKline) {
            klineView.addTimeLineData(hisDataList);
        } else {
            klineView.addCandleData(hisDataList);
        }
    }

    @Override
    public void onQueryCurrencyInfo(boolean success, CurrencyInfoBean data) {
        if (success) {
            if (data != null) {
                ivCurrencyIcon.setImageURI(data.currencyTwIntroduction);
                tvCurrencyName.setText(data.currencyCode);
                tvIssueQuantity.setText(data.issueQuantity);
                tvCurrentQuantity.setText(data.circulateQuantity);
                tvWebLink.setText(data.officialWebsite);
                tvCurrencyIntroduction.setText(data.currencyCnIntroduction);
            }
        }
    }

    @Override
    public void onSelect(List<HisData> mData, int index) {
        if (!isMinHourKline) {
            processMaLayoutStaus(true);
        }
        proccessDetailViewStaus(true);// 处理开，高，收，低 的view 状态
        updateText(mData, index);
        setMainMaData(mData, index);
    }

    @Override
    public void unSelect() {

    }

    /**
     * 是否显示均价(ma)布局
     */
    private void processMaLayoutStaus(boolean isShowMa) {
        llKineMaData.setVisibility(isShowMa ? View.VISIBLE : View.INVISIBLE);
    }


    @BindView(R.id.ll_kline_detail_bar)
    LinearLayout llKLineDetailBar;

    /**
     * // 处理开，高，收，低 的view 状态
     *
     * @param isShowDetailBat 开，高，收，低 显示或隐藏
     */
    private void proccessDetailViewStaus(boolean isShowDetailBat) {
        llKLineDetailBar.setVisibility(isShowDetailBat ? View.VISIBLE : View.GONE);
        //mKViewControl.setVisibility(isShowDetailBat ? View.GONE : View.VISIBLE);
    }

    @BindView(R.id.tv_kldo)
    TextView tvKLDO;
    @BindView(R.id.tv_kldh)
    TextView tvKLDH;
    @BindView(R.id.tv_kldl)
    TextView tvKLDL;
    @BindView(R.id.tv_kldc)
    TextView tvKLDC;
    @BindView(R.id.tv_kld_change)
    TextView tvKLDChange;
    @BindView(R.id.tv_kld_txn)
    TextView tvKLDTxn;
    @BindView(R.id.tv_kld_date)
    TextView tvKLDDate;

    // 精度
    private int mPriceDigits = 8;//价格精度

    private void updateText(List<HisData> mData, int index) {
        if (index >= 0 && index < mData.size()) {
            HisData kLlineData = mData.get(index);

            double newFirstPrice = kLlineData.getOpen();// 开盘价
            double newLastPrice = kLlineData.getClose(); // 收盘价
            double newMaxPrice = kLlineData.getHigh();// 最高价
            double newMinPrice = kLlineData.getLow(); // 最低价
            double dealAmount = kLlineData.getDealAmount(); // 成交额
            double lastClosePrice = 0;

            if (index > 1) {
                HisData hisData = mData.get(index - 1);
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

            // 收:选中的candle线的收盘价。
            if (percentChange > 0) {
                dynamicSetKlineGusterTxtColor(tvKLDChange, R.color.increasing_color);
            } else if (percentChange == 0) {
                dynamicSetKlineGusterTxtColor(tvKLDChange, R.color.textSecondary);
            } else if (percentChange < 0) {
                dynamicSetKlineGusterTxtColor(tvKLDChange, R.color.decreasing_color);
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
    }


    protected int chart01Type = 0;
    protected int chart02Type = 3;

    @BindView(R.id.tv_kline_ma_7)
    TextView tvKMa7;
    @BindView(R.id.tv_kline_ma_25)
    TextView tvKMa25;
    @BindView(R.id.tv_kline_ma_99)
    TextView tvKMa99;

    private void setMainMaData(List<HisData> mDatas, int index) {

        int newIndex = index;

        if (!isMinHourKline && null != mDatas && mDatas.size() > 0 && newIndex >= 0
                && newIndex < mDatas.size()) {

            String s7 = "";
            String s25 = "";
            String s99 = "";

            if (chart01Type == 0) {
                s7 = DoubleUtil.getStringByDigits(mDatas.get(newIndex).getMa5(), mPriceDigits);
                if (!TextUtils.isEmpty(s7)) {
                    s7 = String.format(getResources().getString(R.string.k_ma5), s7);
                }
                s25 = DoubleUtil.getStringByDigits(mDatas.get(newIndex).getMa10(), mPriceDigits);
                if (!TextUtils.isEmpty(s25)) {
                    s25 = String.format(getResources().getString(R.string.k_ma10), s25);
                }
                s99 = DoubleUtil.getStringByDigits(mDatas.get(newIndex).getMa20(), mPriceDigits);
                if (!TextUtils.isEmpty(s99)) {
                    s99 = String.format(getResources().getString(R.string.k_ma20), s99);
                }

                tvKMa7.setTextColor(getResources().getColor(R.color.textPrimary));
                tvKMa25.setTextColor(getResources().getColor(R.color.colorAccent));
                tvKMa99.setTextColor(getResources().getColor(R.color.ma20));
            } else if (chart01Type == 1) {
                s7 = DoubleUtil.getStringByDigits(mDatas.get(newIndex).getEma7(), mPriceDigits);
                if (!TextUtils.isEmpty(s7)) {
                    s7 = String.format(getResources().getString(R.string.k_ema7), s7);
                }
                s25 = DoubleUtil.getStringByDigits(mDatas.get(newIndex).getEma30(), mPriceDigits);
                if (!TextUtils.isEmpty(s25)) {
                    s25 = String.format(getResources().getString(R.string.k_ema30), s25);
                }
                tvKMa7.setTextColor(getResources().getColor(R.color.ema7));
                tvKMa25.setTextColor(getResources().getColor(R.color.ema30));
            } else if (chart01Type == 2) {
                s7 = DoubleUtil.getStringByDigits(mDatas.get(newIndex).getUPs(), mPriceDigits);
                if (!TextUtils.isEmpty(s7)) {
                    s7 = String.format(getResources().getString(R.string.k_ups), s7);
                }
                s25 = DoubleUtil.getStringByDigits(mDatas.get(newIndex).getMBs(), mPriceDigits);
                if (!TextUtils.isEmpty(s25)) {
                    s25 = String.format(getResources().getString(R.string.k_mbs), s25);
                }
                s99 = DoubleUtil.getStringByDigits(mDatas.get(newIndex).getDNs(), mPriceDigits);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_FULL_SCREEN) {
                klineTimeRange = mQuotationKLineDetailPresenter.getSelectedKLineItem();
                isMinHourKline = mQuotationKLineDetailPresenter.isMinuteHour();
                initKlineType();
                mQuotationKLineDetailPresenter.queryQuotationHistory(symbol, klineTimeRange);
/*            } else if (requestCode == RQ_SELECT_SYMBOL) {
                if (data != null && !TextUtils.isEmpty(data.getStringExtra(SYMBOL))) {
                    initSymbol(data);
                    //changeSymbol(data.getStringExtra(SYMBOL), true);
                }*/
            }
        }
    }

    private void dynamicSetKlineGusterTxtColor(TextView dynamicTV, int dynamicColor) {
        dynamicTV.setTextColor(ContextCompat.getColor(this, dynamicColor));
    }
}
