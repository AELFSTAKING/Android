package io.alf.exchange.widget;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.alf.exchange.R;
import io.alf.exchange.ui.TradeContentFragment;
import io.alf.exchange.ui.adapter.ShowBuyAdapter;
import io.alf.exchange.ui.adapter.ShowSellAdapter;
import io.alf.exchange.util.DataUtls;
import io.alf.exchange.util.PriceConvertUtil;
import io.alf.exchange.util.SpannableUtils;
import io.cex.mqtt.bean.OrderBookBean;
import io.cex.mqtt.bean.OrderMarketPrice;
import io.cex.mqtt.bean.StepBean;
import io.tick.base.ui.ActionSheet;
import io.tick.base.util.DensityUtil;
import io.tick.base.util.NetUtils;
import io.tick.base.util.ScreenUtils;

@Keep
public class TradeDataView extends LinearLayout {
    private int deful = 6;//默认显示的条数
    private int showSingel = 12;//单个显示的条数
    private String defulColor = "defulColor";
    private ShowBuyAdapter adapterBuy;
    private ShowSellAdapter adapterSell;
    private Context context;
    private TextView tv_price_title, tv_number_title, tv_price, tv_xiaoshu, tv_deful;
    private RecyclerView recyclerView_sell, recyclerView_buy;
    private LinearLayout ll_deful_num, ll_deful_show;
    private ImageView iv_xiaoshu, iv_deful, iv_mqtt_signal;
    private List<StepBean> defulNumlist;
    //    类型分为012  0是买  1是卖  2是默认
    private int isSelectTag = 0;
    private String firstSymbol;
    private String lastSymbol;
    private Resources resources;
    private String changeSymbol;
    private int defaultItemNum = 6;
    private boolean hideLegalCurrency = true;
    private OnChangeDepthListener mOnChangeDepthListener;
    private onClickTradeItemListener listener;

    public TradeDataView(Context context) {
        this(context, null);
    }

    public TradeDataView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TradeDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.view_trade_data, this);
        tv_price_title = (TextView) findViewById(R.id.tv_price_title);
        tv_number_title = (TextView) findViewById(R.id.tv_number_title);
        tv_deful = (TextView) findViewById(R.id.tv_deful);
        tv_xiaoshu = (TextView) findViewById(R.id.tv_xiaoshu);
        tv_price = (TextView) findViewById(R.id.tv_price);
        iv_deful = (ImageView) findViewById(R.id.iv_deful);
        iv_xiaoshu = (ImageView) findViewById(R.id.iv_xiaoshu);
        recyclerView_sell = (RecyclerView) findViewById(R.id.recyclerView_sell);
        recyclerView_buy = (RecyclerView) findViewById(R.id.recyclerView_buy);
        ll_deful_num = (LinearLayout) findViewById(R.id.ll_deful_num);
        ll_deful_show = (LinearLayout) findViewById(R.id.ll_deful_show);
        iv_mqtt_signal = (ImageView) findViewById(R.id.iv_mqtt_signal);
        initView();
        resources = context.getResources();
        initSymbolSplitStr(null);
        //initItemNum();
    }

    private void initItemNum() {
        float screenHeight = DensityUtil.px2dp(ScreenUtils.getScreenHeight());
        defaultItemNum = (int) ((screenHeight - 240) / 50);
        defaultItemNum = defaultItemNum > 10 ? 10 : defaultItemNum;
        Log.i("Tick", "defaultItemNum : " + defaultItemNum);
    }

    private void initView() {
        initRecyclerView();
        showNUll();
    }

    public void setMqttSignal(boolean hasSignal) {
        if (iv_mqtt_signal != null) {
            iv_mqtt_signal.setImageResource(hasSignal ? R.drawable.vector_drawable_signal
                    : R.drawable.vector_drawable_signal_lost);
        }
    }

    /**
     * 初始化recyclerView方法
     */
    private void initRecyclerView() {
        adapterSell = new ShowSellAdapter(null);
        adapterBuy = new ShowBuyAdapter(null);
        recyclerView_sell.setHasFixedSize(false);
        recyclerView_sell.setLayoutManager(new LinearLayoutManager(context));
        recyclerView_buy.setHasFixedSize(false);
        recyclerView_buy.setLayoutManager(new LinearLayoutManager(context));
        recyclerView_sell.setAdapter(adapterSell);
        adapterSell.initColor(defulColor);
        recyclerView_buy.setAdapter(adapterBuy);
        adapterSell.setOnItemChildClickListener((adapter, view, position) -> {
            if (listener != null) {
                OrderMarketPrice asksBean = null;
                try {
                    asksBean = adapterSell.getData().get(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (asksBean != null) {
                    String price = asksBean.limitPrice;
                    listener.getClickTradeItemListener(price);
                }

            }
        });
        adapterBuy.setOnItemChildClickListener((adapter, view, position) -> {
            if (listener != null) {
                OrderMarketPrice bidsBean = null;
                try {
                    bidsBean = adapterBuy.getData().get(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (bidsBean != null) {
                    String price = bidsBean.limitPrice;
                    listener.getClickTradeItemListener(price);
                }
            }
        });
    }

    /**
     * 当没有数据或者网络的时候显示null方法
     */
    private void showNUll() {
        initVisible(View.GONE);
        recyclerView_sell.setVisibility(VISIBLE);
        adapterSell.initColor(defulColor);
        adapterSell.setNewData(DataUtls.showTradeList(showSingel));
    }
    /**
     * 初始化
     */
    public void initSymbolSplitStr(String symbol) {
        if (!TextUtils.isEmpty(symbol)) {
            String[] split = symbol.split("/");
            if (split.length == 2) {
                firstSymbol = split[0];
                lastSymbol = split[1];
            }
        }
        tv_price_title.setText(tradeTextHint(1));
        tv_number_title.setText(tradeTextHint(2));
    }

    //根据有没有网络 去显示数据的哪种ui类型
    private void initVisible(int visible) {
        recyclerView_sell.setVisibility(visible);
        recyclerView_buy.setVisibility(visible);
        tv_price.setVisibility(visible);
    }

    /**
     * 监听方法
     */
    public void initListeners(FragmentActivity activity) {
        ll_deful_num.setOnClickListener(v -> {
            String xiaoshu = tv_xiaoshu.getText().toString().trim();
            if (getResources().getString(R.string.price_gang).equals(xiaoshu)) {
                return;
            }
            if (defulNumlist == null || defulNumlist.size() == 0) {
                return;
            }
            String[] titles = new String[defulNumlist.size()];
            for (int i = 0; i < defulNumlist.size(); i++) {
                titles[i] = defulNumlist.get(i).decimalValue + context.getString(
                        R.string.jiweixiaoshu);
            }
            ActionSheet.createBuilder(activity, activity.getSupportFragmentManager())
                    .setCancelButtonTitle(R.string.cancel)
                    .setOtherButtonTitles(titles)
                    .setCancelableOnTouchOutside(true)
                    .setListener(new ActionSheet.SampleActionSheetListener() {
                        @Override
                        public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                            changeRange(index);
                        }
                    }).show();
        });

        //默认
        ll_deful_show.setOnClickListener(view -> {
            String[] array = context.getResources().getStringArray(R.array.trade_deful);
            List<String> asList = Arrays.asList(array);
            List<StepBean> list = new ArrayList<>(asList.size());
            for (String temp : asList) {
                StepBean stepBean = new StepBean();
                stepBean.code = temp;
                stepBean.type = 1;
                list.add(stepBean);
            }
            ActionSheet.createBuilder(activity, activity.getSupportFragmentManager())
                    .setCancelButtonTitle(R.string.cancel)
                    .setOtherButtonTitles(array)
                    .setCancelableOnTouchOutside(true)
                    .setListener(new ActionSheet.SampleActionSheetListener() {

                        @Override
                        public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                            if (NetUtils.isNetworkConnected()) {
                                switch (index) {
                                    case 0:
                                        //默认
                                        TradeContentFragment.IS_SHOW_TYPE =
                                                TradeContentFragment.IS_SHOW_AND_BUY;
                                        if (recyclerView_sell.getVisibility() == GONE
                                                || recyclerView_buy.getVisibility() == GONE) {
                                            recyclerView_sell.setVisibility(VISIBLE);
                                            recyclerView_buy.setVisibility(VISIBLE);
                                        }
                                        isSelectTag = 0;
                                        break;
                                    case 1:
                                        //卖
                                        TradeContentFragment.IS_SHOW_TYPE =
                                                TradeContentFragment.IS_SHOW_SELL;
                                        if (recyclerView_sell.getVisibility() == GONE
                                                || recyclerView_buy.getVisibility() == VISIBLE) {
                                            recyclerView_sell.setVisibility(VISIBLE);
                                            recyclerView_buy.setVisibility(GONE);
                                        }
                                        isSelectTag = 1;
                                        break;
                                    case 2:
                                        //买
                                        TradeContentFragment.IS_SHOW_TYPE =
                                                TradeContentFragment.IS_SHOW_BUY;
                                        if (recyclerView_sell.getVisibility() == VISIBLE
                                                || recyclerView_buy.getVisibility() == GONE) {
                                            recyclerView_buy.setVisibility(VISIBLE);
                                            recyclerView_sell.setVisibility(GONE);
                                        }
                                        isSelectTag = 2;
                                        break;
                                }
                                tv_deful.setText(array[index]);
                            }
                        }
                    }).show();
        });
    }

    /**
     * 设置小位数
     */
    public void setDefulNumber(String symbol) {
        if (!TextUtils.equals(changeSymbol, symbol)) {
            changeSymbol = symbol;//第一次进入的时候  下面的是第一次登陆的时候
        }
    }

    /**
     * 设置adapter的数据方法
     */
    public void setRecyclerViewData(OrderBookBean bean) {
        if (NetUtils.isNetworkConnected()) {
            adapterSell.initColor("");
            tv_price.setVisibility(VISIBLE);
/*            if (view_line_top.getVisibility() == GONE) {
                view_line_top.setVisibility(VISIBLE);
                view_line_bottom.setVisibility(VISIBLE);
                tv_price.setVisibility(VISIBLE);
            }*/
            showData(bean);
        } else {
            showNUll();
        }
    }

    /**
     * 更新五档数据的显示方式
     */
    public void showData(OrderBookBean dataBeanX) {
        List<OrderMarketPrice> bids = dataBeanX.bidList;
        List<OrderMarketPrice> asks = dataBeanX.askList;

        OrderBookBean.OrderMarketDealItem deal = dataBeanX.latestDeal;
        int direction = deal.direction;//1:上涨 -1:下跌 0:不变
        switch (direction) {
            case -1:
                int color = resources.getColor(R.color.color_decrease);
                tv_price.setTextColor(color);
                break;
            case 0:
                color = resources.getColor(R.color.textPrimary);
                tv_price.setTextColor(color);
                break;
            case 1:
                color = resources.getColor(R.color.color_increase);
                tv_price.setTextColor(color);
                break;
        }

/*        if (changeSymbol != null && LegalAndValuationKt.shouldDisplayValuatedPrice
(changeSymbol)) {
            SpannableUtils.setTextView(tv_price, deal.price, LegalAndValuationKt
            .formatAsValuatedPrice(deal.usdPrice));
        } else {
            SpannableUtils.setTextView(tv_price, deal.price, "");
        }*/
        if (hideLegalCurrency) {
            SpannableUtils.setTextView(tv_price, deal.price, "");
        } else {
            SpannableUtils.setTextView(tv_price, deal.price,
                    PriceConvertUtil.getUsdtAmount(deal.usdPrice));
        }
        showRecyclerViewItem(bids, asks);
    }

    public void setOnChangeDepthListener(OnChangeDepthListener mOnChangeDepthListener) {
        this.mOnChangeDepthListener = mOnChangeDepthListener;
    }

    /**
     * 改变了聚合度
     */
    private void changeRange(int position) {
        //TODO订阅聚合度
        tv_xiaoshu.setText(
                defulNumlist.get(position).decimalValue + context.getResources().getString(
                        R.string.jiweixiaoshu));
        // TODO: 2019/3/29
        if (mOnChangeDepthListener != null) {
            mOnChangeDepthListener.onChangeDepth(defulNumlist.get(position).code);
        }
    }

    /**
     * 改变了聚合度
     */
    public void updateRange(List<StepBean> data) {
        if (data != null && data.size() > 0) {
            defulNumlist = data;
            changeRange(data.size() - 1);
        }
    }

    /**
     * @param bids 买
     * @param asks 卖
     */
    private void showRecyclerViewItem(List<OrderMarketPrice> bids, List<OrderMarketPrice> asks) {
        int deful = 0;
        if (isSelectTag == 0) {
            deful = defaultItemNum;
        } else {
            deful = defaultItemNum * 2;
        }
        List<OrderMarketPrice> buyList = null;
        List<OrderMarketPrice> sellList = null;
        Collections.sort(asks, new Comparator<OrderMarketPrice>() {
            @Override
            public int compare(OrderMarketPrice o1, OrderMarketPrice o2) {
                if (o1 != null && o2 != null) {
                    double v1 = 0;
                    double v2 = 0;
                    if (!TextUtils.isEmpty(o1.limitPrice)) {
                        v1 = Double.parseDouble(o1.limitPrice);
                    }
                    if (!TextUtils.isEmpty(o2.limitPrice)) {
                        v2 = Double.parseDouble(o2.limitPrice);
                    }
                    return Double.compare(v1, v2);
                } else {
                    return 0;
                }
            }
        });
        if (bids.size() >= deful) {
            buyList = bids.subList(0, deful);
        } else {
            int bidSize = deful - bids.size();
            for (int i = 0; i < bidSize; i++) {
                bids.add(null);
            }
            buyList = bids;
        }
        if (asks.size() >= deful) {
            sellList = asks.subList(0, deful);
            Collections.reverse(sellList);
        } else {
            int askSize = deful - asks.size();
            for (int i = 0; i < askSize; i++) {
                asks.add(null);
            }
            Collections.reverse(asks);
            sellList = asks;
        }

        double sumTotalBid = 0;
        for (OrderMarketPrice bean : buyList) {
            if (bean != null && !TextUtils.isEmpty(bean.quantity)) {
                sumTotalBid = Double.parseDouble(bean.quantity) + sumTotalBid;
                bean.sum = sumTotalBid;
            }
        }

        double sumTotalAsk = 0;

        for (int i = sellList.size() - 1; i >= 0; i--) {
            OrderMarketPrice bean = sellList.get(i);
            if (bean != null && !TextUtils.isEmpty(bean.quantity)) {
                sumTotalAsk = Double.parseDouble(bean.quantity) + sumTotalAsk;
                bean.sum = sumTotalAsk;
            }
        }

        double totalNum = 0;
        if (sumTotalBid > sumTotalAsk) {
            totalNum = sumTotalBid;
        } else {
            totalNum = sumTotalAsk;
        }

        adapterBuy.setTotalNum(totalNum + "");//定义进度的总长度
        adapterSell.setTotalNum(totalNum + "");

        switch (isSelectTag) {
            case 2:
                //买
                if (recyclerView_sell.getVisibility() == VISIBLE
                        || recyclerView_buy.getVisibility() == GONE) {
                    recyclerView_buy.setVisibility(VISIBLE);
                    recyclerView_sell.setVisibility(GONE);
                }
//                adapterBuy.setNewData(buyList);
                adapterBuy.getData().clear();
                adapterBuy.getData().addAll(buyList);
                adapterBuy.notifyDataSetChanged();
                break;
            case 1:
                //卖
                if (recyclerView_sell.getVisibility() == GONE
                        || recyclerView_buy.getVisibility() == VISIBLE) {
                    recyclerView_sell.setVisibility(VISIBLE);
                    recyclerView_buy.setVisibility(GONE);
                }
//                adapterSell.setNewData(sellList);
                adapterSell.getData().clear();
                adapterSell.getData().addAll(sellList);
                adapterSell.notifyDataSetChanged();
                break;
            case 0:
                //默认
                if (recyclerView_sell.getVisibility() == GONE
                        || recyclerView_buy.getVisibility() == GONE) {
                    recyclerView_sell.setVisibility(VISIBLE);
                    recyclerView_buy.setVisibility(VISIBLE);
                }
                adapterBuy.getData().clear();
                adapterBuy.getData().addAll(buyList);
                adapterBuy.notifyDataSetChanged();
                adapterSell.getData().clear();
                adapterSell.getData().addAll(sellList);
                adapterSell.notifyDataSetChanged();

//                adapterBuy.setNewData(buyList);
//                adapterSell.setNewData(sellList);
                break;
        }


    }


    /**
     * 交易的 b变换符号
     */
    private String tradeTextHint(int type) {
        String result = "";
        String s = resources.getString(R.string.price_gang);
        switch (type) {
            //price
            case 1:
                String priceFormat = resources.getString(R.string.price2);
                if (!TextUtils.isEmpty(lastSymbol)) {
                    result = String.format(priceFormat, lastSymbol);
                } else {
                    result = String.format(priceFormat, s);
                }
                break;
            //num
            case 2:
                String numFormat = resources.getString(R.string.number2);
                if (!TextUtils.isEmpty(firstSymbol)) {
                    result = String.format(numFormat, firstSymbol);
                } else {
                    result = String.format(numFormat, s);
                }
                break;
        }
        return result;

    }

    public void setDefaultItemNum(int defaultItemNum) {
        this.defaultItemNum = defaultItemNum;
    }

    public void setHideLegalCurrency(boolean hideLegalCurrency) {
        this.hideLegalCurrency = hideLegalCurrency;
    }

    public void getClickTradeItemListener(onClickTradeItemListener listener) {
        this.listener = listener;
    }

    public interface OnChangeDepthListener {
        void onChangeDepth(String depthCode);
    }

    /**
     * 点击recyvlerViewItem 之后返回到最佳成交价格的方法
     */
    public interface onClickTradeItemListener {
        void getClickTradeItemListener(String price);
    }

}
