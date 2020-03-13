package io.alf.exchange.widget;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.alf.exchange.R;
import io.alf.exchange.dialog.ErrorDialog;
import io.alf.exchange.mvp.bean.TradeSymbolInfoBean;
import io.alf.exchange.sample.SampleSeekChangeListener;
import io.alf.exchange.ui.TradeFragment;
import io.alf.exchange.util.CexDataPersistenceUtils;
import io.alf.exchange.util.PriceConvertUtil;
import io.cex.exchange.kotlin.coreutil.LegalAndValuationKt;
import io.cex.mqtt.bean.QuotationBean;
import io.cex.mqtt.bean.SellInfo;
import io.tick.base.ui.ActionSheet;
import io.tick.base.util.BigDecimalUtil;
import io.tick.base.util.NetUtils;
import io.tick.base.util.RxBindingUtils;
import io.tick.base.util.ToastUtils;


/**
 * 功能描述： 显示交易左边的视图 带有市价和限价的
 */

public class SelectorBuyAndSellView extends ConstraintLayout {

    private final String TAG = this.getClass().getSimpleName();
    // 用于可用价格
    private final StringBuffer showBuySellBuffer = new StringBuffer();
    public boolean isFillPrice = false;//控制editText的循环

    @BindView(R.id.rg_buy_sell)
    RadioGroup rgBuySell;
    @BindView(R.id.rb_buy)
    RadioButton rbBuyTitle;
    @BindView(R.id.rb_sell)
    RadioButton rbSellTitle;
    @BindView(R.id.cs_parent)
    ConstraintLayout cs_parent;
    @BindView(R.id.tv_available_price)
    TextView tv_available_price;//当是市价的时候需要隐藏 否则显示
    @BindView(R.id.et_trade_price)
    TradeEditText etTradePrice;//市价的时候是  以当前最优价格交易去掉两段的加减 而且不可输入   否则是价格
    @BindView(R.id.et_trade_number)
    TradeEditText2 etTradeNumber;//市价的时候总额（btc） 限价的时候是数量
    @BindView(R.id.et_trade_total_price)
    TradeEditText3 etTradeTotalPrice;//市价的时候消失
    @BindView(R.id.tv_guzhi_title)
    TextView tvGuZhiTitle;
    @BindView(R.id.tv_guzhi_price)
    TextView tvGuZhiPrice;
    @BindView(R.id.btn_buy_sell)
    TextView btnBuySell;//买卖
    @BindView(R.id.percent_buy)
    IndicatorSeekBar percentBuy;
    @BindView(R.id.percent_sell)
    IndicatorSeekBar percentSell;
    @BindView(R.id.ll_order_type)
    LinearLayout llOrderType;
    @BindView(R.id.tv_select_order)
    TextView tv_select_order;
    @BindView(R.id.iv_select)
    ImageView iv_select;
    boolean mBuyFlag = true;//默认是买 当有切换的时候就会变化

    private String availableBuy;//可用的买
    private String availableSell;//可用的卖
    private String numType = "numType";
    private String priceType = "priceType";
    private QuotationBean quotation;
    private StringBuffer depthBuf = new StringBuffer();
    private TradeSymbolInfoBean configInfo;
    private ArrayList<SellInfo> mBuyidsBeanList = new ArrayList<>();
    private ArrayList<SellInfo> mSellBeanList = new ArrayList<>();
    private String firstSymbol;
    private String lastSymbol;
    private int orderPos = 0;
    private String symbol;
    //配置信息
    private String priceBestBuy;//最佳成交价的买
    private String priceBestSell;//最佳成交价的卖
    private int pricePrecision = 2; // 价格小数位数
    private int quantityPrecision = 8;// 数量的小数位
    private int amountPrecision = 8; // 总额小数位数
    private String clickBestPrice; //点击recyclerView的item显示的价格
    private String bestPrice;//时时返回的最新成交价

    private Context context;
    private Resources resources;
    private boolean percentFlag = true;


    //    private boolean isclickPercentAndEdittextChange;//来控制百分比和 num的输入框该改变时候的方法
    private OnBuyOrSellClickListener onBuyOrSellClickListener;

    public SelectorBuyAndSellView(Context context) {
        this(context, null);
    }

    public SelectorBuyAndSellView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectorBuyAndSellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        resources = context.getResources();
        View contentView = LayoutInflater.from(context).inflate(R.layout.view_select_buy_sell,
                this);
        ButterKnife.bind(this, contentView);
        changePercentBtnVisibility();
        initEvents();
        //initSymbolSplitStr(MqttTopicManager.getCurrentSymbol());
    }

    private void changePercentBtnVisibility() {
        percentBuy.setProgress(0);
        percentSell.setProgress(0);
        if (rbBuyTitle.isChecked()) {
            percentBuy.setVisibility(VISIBLE);
            percentSell.setVisibility(GONE);
        } else if (rbSellTitle.isChecked()) {
            percentBuy.setVisibility(GONE);
            percentSell.setVisibility(VISIBLE);
        }
    }

    private void initEvents() {
        rgBuySell.setOnCheckedChangeListener((group, checkedId) -> changePercentBtnVisibility());
        percentBuy.setOnSeekChangeListener(mSeekChangeListener);
        percentSell.setOnSeekChangeListener(mSeekChangeListener);
        etTradePrice.setClickEditListener(clickEditListener);
        etTradeNumber.setClickEditListener(clickEditListener);
        etTradeTotalPrice.setClickEditListener(clickEditListener);
        RxBindingUtils.clicks(aVoid -> switchBuyOrSell(TradeFragment.BUY), rbBuyTitle);
        RxBindingUtils.clicks(aVoid -> switchBuyOrSell(TradeFragment.SELL), rbSellTitle);
        RxBindingUtils.clicks(aVoid -> {
            if (onBuyOrSellClickListener != null) {
                if (NetUtils.isNetworkConnected()) {
                    boolean selected = btnBuySell.isSelected();//如果选中是买  否则是卖
                    if (selected) {
                        onBuyOrSellClickListener.onBuyClick();
                    } else {
                        onBuyOrSellClickListener.onSellClick();
                    }
                } else {
                    ToastUtils.showShortToast(R.string.dangqianwangluobujia);
                }
            }
        }, btnBuySell);
    }

    private OnSeekChangeListener mSeekChangeListener = new SampleSeekChangeListener() {
        @Override
        public void onSeeking(SeekParams seekParams) {
            if (percentFlag) {
                showTotalMoneyPercent(String.valueOf(seekParams.progress / 100f));
            }
        }
    };

    private TradeEditText.ClickEditListener clickEditListener =
            new TradeEditText.ClickEditListener() {
                @Override
                public void clickEditListeners() {
                }

                @Override
                public void clickEditTextChangeListener(View v, String num) {
                    switch (v.getId()) {
                        case R.id.et_trade_price:
                            if (orderPos == 0) {// 限价单
                                showGuZhi(num);
                                showTotalMoney(num, priceType);
                            } //市价单  不需要处理 因为i他是市价不可点击
                            if (!TextUtils.isEmpty(num)) {
                                percentFlag = false;
                                showPercent(etTradeNumber.getText());
                                percentFlag = true;
                            }
                            break;
                        case R.id.et_trade_number:
                            if (orderPos == 0) {//限价单
                                showTotalMoney(num, numType);
                            }// 点击editTextView 时候改变的估值
                            if (!TextUtils.isEmpty(num)) {
                                percentFlag = false;
                                showPercent(num);
                                percentFlag = true;
                            }
                            break;
                        case R.id.et_trade_total_price:
                            etTradeNumber.removeTextChangedListener();
                            String price = etTradePrice.getText();
                            if (!TextUtils.isEmpty(price) && !TextUtils.isEmpty(num)) {
                                try {
                                    int numLen = 0;
                                    if (orderPos == 0) {//限价
                                        numLen = quantityPrecision;
                                    } else {
                                        if (mBuyFlag) {//买的时候是价格
                                            numLen = pricePrecision;
                                        } else {//数量
                                            numLen = quantityPrecision;
                                        }
                                    }
                                    String number = BigDecimalUtil.divTrade(num, price, numLen);
                                    etTradeNumber.setTextMoney(number);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                etTradeNumber.setTextMoney("");
                            }
                            etTradeNumber.addTextChangedListener();
                            break;
                    }
                }
            };

    private void showPercent(String num) {
        if (orderPos == 0) {//限价单
            if (mBuyFlag) {//买
                if (!TextUtils.isEmpty(availableBuy)) {
                    double v = Double.parseDouble(availableBuy);//可用的钱数
                    if (v == 0) {
                        percentBuy.setProgress(0);
                    } else {
                        String price = etTradePrice.getText();
                        if (!TextUtils.isEmpty(price)) {
                            try {
                                String availableBuyNum = BigDecimalUtil.divTrade(availableBuy,
                                        price, quantityPrecision);
                                float progress = Float.valueOf(
                                        BigDecimalUtil.div(num, availableBuyNum, 3)) * 100;
                                progress = progress > 100 ? 100 : progress;
                                percentBuy.setProgress(progress);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            percentBuy.setProgress(0);
                        }
                    }
                } else {
                    percentBuy.setProgress(0);
                }
            } else {
                if (!TextUtils.isEmpty(availableSell)) {
                    double v = Double.parseDouble(availableSell);//可用的钱数
                    if (v == 0) {
                        percentSell.setProgress(0);
                    } else {
                        String price = etTradePrice.getText();
                        if (!TextUtils.isEmpty(price)) {
                            try {
                                float progress = Float.valueOf(
                                        BigDecimalUtil.div(num, availableSell, 3)) * 100;
                                progress = progress > 100 ? 100 : progress;
                                percentSell.setProgress(progress);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            percentSell.setProgress(0);
                        }
                    }
                } else {
                    percentSell.setProgress(0);
                }
            }
        } else { //市价单
            if (mBuyFlag) {
                if (!TextUtils.isEmpty(availableBuy)) {
                    double v = Double.parseDouble(availableBuy);//可用的钱数
                    if (v == 0) {
                        percentBuy.setProgress(0);
                    } else {
                        try {
                            float progress = Float.valueOf(BigDecimalUtil.div(num, availableBuy, 3))
                                    * 100;
                            progress = progress > 100 ? 100 : progress;
                            percentBuy.setProgress(progress);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                if (!TextUtils.isEmpty(availableSell)) {
                    double v = Double.parseDouble(availableSell);//可用的钱数
                    if (v == 0) {
                        percentSell.setProgress(0);
                    } else {
                        try {
                            float progress = Float.valueOf(
                                    BigDecimalUtil.div(num, availableSell, 3)) * 100;
                            progress = progress > 100 ? 100 : progress;
                            percentSell.setProgress(progress);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * D.以美元计价
     * E.BTC/USD：估值=输入的限价买入价格
     * F.精度：2位，如$ 8100.67
     * <p>
     * A.以美元计价
     * B.ETH/BTC：估值=（ETH/BTC价格）*（BTC/USD价格）
     * C.精度：2位，如 $8100.67
     * <p>
     * 最终areaUsdPrice 显示估值
     * 1.只和最佳成交价有关系
     * 2.还有点击7挡的时候
     * 3.第一次进入页面时候的最佳成交价  看的币成  如果不行 再改
     * <p>
     * 5.2 号 海民说 估值折合成美元是两位
     */
    public void showGuZhi(String value) {
        if (!TextUtils.isEmpty(value)) {
            tvGuZhiPrice.setText(PriceConvertUtil.getUsdtAmount(value));
        } else {
            tvGuZhiPrice.setText("");
        }
    }

    //显示总额 根据价格 和数量相乘得到的总额 限价的买卖的显示总额
    private void showTotalMoney(String input, String type) {
        etTradeTotalPrice.removeTextChangedListener();
        String price = null;
        String number = null;
        if (priceType.equals(type)) {
            price = input;
            number = etTradeNumber.getText();
        } else if (numType.equals(type)) {
            price = etTradePrice.getText();
            number = input;
        }
        if (!TextUtils.isEmpty(price) && !TextUtils.isEmpty(number)) {
            double v = Double.parseDouble(number);
            if (v == 0) {
                etTradeTotalPrice.setTextMoney("0.00", true);
            } else {
                String totalPrice = BigDecimalUtil.mulTrade(number, price, amountPrecision);
                etTradeTotalPrice.setTextMoney(BigDecimalUtil.stripTrailingZeros(totalPrice), true);
            }
        } else {
            etTradeTotalPrice.setTextMoney("0.00", true);
        }
        etTradeTotalPrice.addTextChangedListener();
    }


    public void onClickBuy() {
        if (rbBuyTitle != null) {
            rbBuyTitle.performClick();
        }
    }

    public void onClickSell() {
        if (rbSellTitle != null) {
            rbSellTitle.performClick();
        }
    }

    /**
     * 监听里面有限价和市价
     */
    public void intOrderTypeListener(FragmentActivity activity) {
        llOrderType.setOnClickListener(v -> ActionSheet.createBuilder(activity,
                activity.getSupportFragmentManager())
                .setCancelButtonTitle(R.string.cancel)
                .setOtherButtonTitles(activity.getString(R.string.limit_price),
                        activity.getString(R.string.cur_best_price))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.SampleActionSheetListener() {
                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            //限价单
                            orderPos = 0;
                            //初始化小位数
                            xianJiaOrder();
                            tv_select_order.setText(R.string.limit_price_order);
                            percentBuy.setProgress(0);
                            percentSell.setProgress(0);
                        } else if (index == 1) {
                            orderPos = 1;
                            shiJiaOrder();
                            //市价单
                            tv_select_order.setText(R.string.marketOrder);
                            percentBuy.setProgress(0);
                            percentSell.setProgress(0);
                        }
                        initNumberDigit();
                    }
                }).show());
    }

    /**
     * 限价单
     */
    private void xianJiaOrder() {
        if (tvGuZhiTitle.getVisibility() == View.GONE) {
            clearView(true);
        }
        tvGuZhiTitle.setVisibility(VISIBLE);
        //tvGuZhiPrice.setVisibility(VISIBLE);
        etTradeTotalPrice.setVisibility(VISIBLE);
        etTradePrice.setVisibleForSubAndAdd(true);
        etTradePrice.setEnabled(true);//最优价格交易
        showXianJiaHint();
        etTradeNumber.isMoney(false);
    }

    /**
     * 显示限价的hint 值
     */
    private void showXianJiaHint() {
        etTradePrice.setTextHint(tradeTextHint(1));
        //etTradeNumber.setTextHint(tradeTextHint(2));
        etTradeNumber.setValue(context.getString(R.string.number), "0.00", firstSymbol);
        etTradeNumber.clearFocus();
        etTradeTotalPrice.setValue(context.getString(R.string.transaction_amount_1), "0.00",
                lastSymbol);
        etTradeTotalPrice.clearFocus();
        //etTradeTotalPrice.setTextHint(tradeTextHint(3));
    }

    /**
     * 市价单
     */
    private void shiJiaOrder() {
        if (tvGuZhiTitle.getVisibility() == View.VISIBLE) {
            clearView(false);
        }
        tvGuZhiTitle.setVisibility(GONE);
        tvGuZhiPrice.setVisibility(GONE);
        etTradeTotalPrice.setVisibility(GONE);
        etTradePrice.setVisibleForSubAndAdd(false);
        etTradePrice.clearFocus();
        etTradePrice.setEnabled(false);//最优价格交易
        etTradePrice.setTextHint(context.getResources().getString(R.string.cur_best_price));
        showShiJiaHint();
        etTradeNumber.isMoney(true);
    }

    /**
     * 显示市价的hint 值
     */
    private void showShiJiaHint() {
        if (mBuyFlag) {
            if (LegalAndValuationKt.isDefaultLegalCurrency(lastSymbol)) {
                etTradeNumber.setTextHint(tradeTextHint(4));
            } else {
                etTradeNumber.setValue(context.getString(R.string.transaction_amount_1), "0.00",
                        lastSymbol);
                etTradeNumber.clearFocus();
                //etTradeNumber.setTextHint(tradeTextHint(5));//如果是买的话 显示的是价格  卖 显示的是数量
            }
        } else {
            etTradeNumber.setValue(context.getString(R.string.number), "0.00", firstSymbol);
            //etTradeNumber.setTextHint(tradeTextHint(2));
        }
    }

    /**
     * 买卖切换
     * 清除选中的view方法和button的买入和卖出
     */
    public void switchBuyOrSell(String data) {
        if (TextUtils.equals(TradeFragment.BUY, data)) {
            setBuyAndSell(true);
            boolean isFirst;
            if (!TextUtils.isEmpty(clickBestPrice)) {
                priceBestBuy = clickBestPrice;
                isFirst = false;
            } else {
                isFirst = true;
            }
            setBestPrice(priceBestBuy, isFirst);
        } else if (TextUtils.equals(TradeFragment.SELL, data)) {
            setBuyAndSell(false);
            boolean isFirst;
            if (!TextUtils.isEmpty(clickBestPrice)) {
                priceBestSell = clickBestPrice;
                isFirst = false;
            } else {
                isFirst = true;
            }
            setBestPrice(priceBestSell, isFirst);
        }
        if (orderPos == 0) {//限价
            clearView(true);
            showXianJiaHint();
        } else {//市价
            clearView(false);
            showShiJiaHint();
        }
    }

    /**
     * button 的买入还是卖出
     *
     * @param buyFlag true  buy
     *                false sell
     */
    private void setBuyAndSell(boolean buyFlag) {
        this.mBuyFlag = buyFlag;
        btnBuySell.setSelected(mBuyFlag);
        if (buyFlag) {
            btnBuySell.setText(resources.getString(R.string.buy) + firstSymbol);
            changeview(availableBuy, mBuyFlag);
        } else {
            btnBuySell.setText(resources.getString(R.string.sell) + firstSymbol);
            changeview(availableSell, mBuyFlag);
        }
    }

    public void setTouchable(boolean touchAble) {
        etTradePrice.setTouchable(touchAble);
        etTradeNumber.setTouchable(touchAble);
        etTradeTotalPrice.setTouchable(false);
        /*        if (touchAble) {*/
        btnBuySell.setText(mBuyFlag ? resources.getString(R.string.buy) + firstSymbol
                : resources.getString(R.string.sell) + firstSymbol);
/*        } else {
            btnBuySell.setText(R.string.login);
        }*/
    }

    /**
     * 初始化小数位数
     */
    public void initNumberDigit() {
        if (orderPos == 0) { // 限价单
            // 数量
            etTradeNumber.isMoney(false);
            etTradeNumber.setDefualNumMoney(quantityPrecision, quantityPrecision);
        } else {// 市价单
            if (mBuyFlag) {
                // 市价买入，交易总额
                etTradeNumber.isMoney(true);
                etTradeNumber.setDefualNumMoney(amountPrecision, amountPrecision);
            } else { // 市价卖出，数量
                etTradeNumber.setDefualNumMoney(quantityPrecision, quantityPrecision);
                etTradeNumber.isMoney(false);
            }
        }
        // 价格
        etTradePrice.isMoney(true);
        etTradePrice.setDefualNumMoney(pricePrecision, pricePrecision);
        // 总额
        etTradeTotalPrice.isMoney(true);
        etTradeTotalPrice.setDefualNumMoney(amountPrecision, amountPrecision);
    }


    /**
     * 清除焦点
     */
    public void clearFocus() {
        if (!etTradePrice.isFocused()) {
            etTradePrice.clearFocus();
        }
    }

    private void showError(@StringRes int id) {
        showError(getContext().getResources().getString(id));
    }

    private void showError(String message) {
        new ErrorDialog(getContext(), message).show();
    }

    /**
     * 获取提交的参数
     */
    public void getSubmitParams(String changeSymbol) {
/*        int tradeStatus = 0;
        if (configInfo != null) {
            tradeStatus = configInfo.tradeStatus;// 交易状态  0-不可交易，1-可交易
        }
        if (tradeStatus == 0) {
            showError(R.string.trade_status_is_avaliable);
            return;
        }*/
        HashMap<String, String> params = new HashMap<>();
        if (TextUtils.isEmpty(symbol)) {
            symbol = changeSymbol;
        }
        params.put("symbol", symbol);
        if (mBuyFlag) { //买入
            params.put("action", "BUY");
        } else { //卖出
            params.put("action", "SELL");
        }
        if (orderPos == 0) {
            params.put("orderType", "LMT");
        } else if (orderPos == 1) {
            params.put("orderType", "MKT");
        }
        switch (orderPos) {
            case 0: // "LMT" 限价
                String limitPrice = etTradePrice.getText();
                if (TextUtils.isEmpty(limitPrice) || BigDecimalUtil.equal("0", limitPrice)) {
                    showError(R.string.please_fill_price);
                    return;
                }
                String quantity = etTradeNumber.getText();
                if (TextUtils.isEmpty(quantity) || BigDecimalUtil.equal("0", quantity)) {
                    showError(R.string.please_input_num);
                    return;
                }
                String amount = etTradeTotalPrice.getText();
                if (mBuyFlag ? BigDecimalUtil.compare(amount, availableBuy)
                        : BigDecimalUtil.compare(quantity, availableSell)) {
                    showError(R.string.cyuerbuzu);
                    return;
                }
                // 委托价格
                //params.put("limitPrice", limitPrice);
                params.put("priceLimit", limitPrice);
                params.put("quantity", quantity);
                break;
            case 1: // "MKT" 市价
                if (mBuyFlag) { // 市价买入
                    String buyAmount = etTradeNumber.getText(); // 买入金额
                    if (TextUtils.isEmpty(buyAmount) || BigDecimalUtil.equal("0", buyAmount)) {
                        showError(R.string.please_input_num);
                        return;
                    }
                    if (BigDecimalUtil.compare(buyAmount, availableBuy)) {
                        showError(R.string.cyuerbuzu);
                        return;
                    }
                    params.put("quantity", "0");
                    //params.put("limitPrice", "0");
                    params.put("priceLimit", "0");
                    params.put("amount", buyAmount);
                } else { // 市价卖出
                    String sellQuantity = etTradeNumber.getText(); //卖出数量
                    if (TextUtils.isEmpty(sellQuantity) || BigDecimalUtil.equal("0",
                            sellQuantity)) {
                        showError(R.string.please_input_num);
                        return;
                    }
                    if (BigDecimalUtil.compare(sellQuantity, availableSell)) {
                        showError(R.string.cyuerbuzu);
                        return;
                    }
                    params.put("quantity", sellQuantity);
                    //params.put("limitPrice", "0");
                    params.put("priceLimit", "0");
                }
                break;
        }
        if (onBuyOrSellClickListener != null) {
            onBuyOrSellClickListener.resultSubmitParams(params);
        }
    }

    /**
     * 显示百分比
     */
    private void showTotalMoneyPercent(String percent) {
        etTradeNumber.removeTextChangedListener();
        etTradeTotalPrice.removeTextChangedListener();
        if (orderPos == 0) {//限价单
            if (mBuyFlag) {//买
                if (!TextUtils.isEmpty(availableBuy)) {
                    double v = Double.parseDouble(availableBuy);//可用的钱数
                    if (v == 0) {
                        etTradeNumber.setTextMoney("0", true);
                    } else {
                        String price = etTradePrice.getText();
                        if (!TextUtils.isEmpty(price)) {
                            String mul = BigDecimalUtil.mulTrade(availableBuy, percent,
                                    amountPrecision);
                            try {
                                String num = BigDecimalUtil.divTrade(mul, price, quantityPrecision);
                                etTradeNumber.setTextMoney(new BigDecimal(
                                        num).stripTrailingZeros().toPlainString(), true);
                                etTradeTotalPrice.setTextMoney(new BigDecimal(
                                        mul).stripTrailingZeros().toPlainString(), true);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        } else {
                            etTradeNumber.setTextMoney("0", true);
                        }
                    }
                } else {
                    etTradeNumber.setTextMoney("0", true);
                }
            } else {
                if (!TextUtils.isEmpty(availableSell)) {
                    double v = Double.parseDouble(availableSell);//可用的钱数
                    if (v == 0) {
                        etTradeNumber.setTextMoney("0", true);
                    } else {
                        String price = etTradePrice.getText();
                        if (!TextUtils.isEmpty(price)) {
                            String mul = BigDecimalUtil.mulTrade(availableSell, percent,
                                    quantityPrecision);
                            etTradeNumber.setTextMoney(new BigDecimal(
                                    mul).stripTrailingZeros().toPlainString(), true);
                            String totalPrice = BigDecimalUtil.mulTrade(mul, price,
                                    amountPrecision);
                            etTradeTotalPrice.setTextMoney(new BigDecimal(
                                    totalPrice).stripTrailingZeros().toPlainString(), true);
                        } else {
                            etTradeNumber.setTextMoney("0", true);
                        }
                    }
                } else {
                    etTradeNumber.setTextMoney("0", true);
                }
            }
        } else {//市价单
            String totalPrice = "";
            if (mBuyFlag) {
                totalPrice = BigDecimalUtil.mulTrade(availableBuy, percent, amountPrecision);
            } else {
                totalPrice = BigDecimalUtil.mulTrade(availableSell, percent, quantityPrecision);
            }
            totalPrice = TextUtils.isEmpty(totalPrice) ? "0.00" : totalPrice;
            etTradeNumber.setTextMoney(new BigDecimal(
                    totalPrice).stripTrailingZeros().toPlainString(), true);
        }
        etTradeNumber.addTextChangedListener();
        etTradeTotalPrice.addTextChangedListener();
    }

    public void initlickBestPrice() {
        clickBestPrice = null;
        isFillPrice = false;
    }

    public void setClickPrice(String price) {
        clickBestPrice = price;
    }

    /**
     * 配置信息
     */
    public void setConFigInfo(TradeSymbolInfoBean configInfo) {
        clickBestPrice = null;
        if (configInfo != null) {
            this.configInfo = configInfo;
            priceBestBuy = configInfo.priceBestBuy;
            priceBestSell = configInfo.priceBestSell;
            pricePrecision = configInfo.priceScale;
            quantityPrecision = configInfo.currencyCoinQuantityScale < 8 ? 8
                    : configInfo.currencyCoinQuantityScale;
            amountPrecision = configInfo.amountScale < 8 ? 8 : configInfo.amountScale;
            initSymbolSplitStr(configInfo.symbol);
            availableBuy = configInfo.availableBuy;//买入可用金额
            availableSell = configInfo.availableSell;//卖出可用数量
            if (mBuyFlag) {//买
                changeview(availableBuy, true);
            } else {//卖
                changeview(availableSell, false);
            }
            //初始化小位数
            initNumberDigit();
        }
    }

    /**
     * 切换交易对之后需要对数据进行情况
     */
    public void beforSymbolclearData() {
        etTradeNumber.setTextMoney("");
        percentBuy.setProgress(0);
        percentSell.setProgress(0);
    }

    /**
     * 拆分字符串
     */
    public void initSymbolSplitStr(String symbol) {
        if (!CexDataPersistenceUtils.isLogin()) {
            availableBuy = null;
            availableSell = null;
        }
        this.symbol = symbol;
        if (!TextUtils.isEmpty(symbol)) {
            String[] split = symbol.split("/");
            if (split.length == 2) {
                firstSymbol = split[0];
                lastSymbol = split[1];
            }
        }
        if (orderPos == 0) {
            xianJiaOrder();
        } else if (orderPos == 1) {
            shiJiaOrder();
        }
        if (mBuyFlag) {
            changeview(availableBuy, mBuyFlag);
        } else {
            changeview(availableSell, mBuyFlag);
        }
        initNumberDigit();
        setBuyAndSell(mBuyFlag);
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
                String priceFormat = resources.getString(R.string.price1);
                if (!TextUtils.isEmpty(lastSymbol)) {
                    result = String.format(priceFormat, lastSymbol);
                } else {
                    result = String.format(priceFormat, s);
                }
                break;
            //num
            case 2:
                String numFormat = resources.getString(R.string.number1);
                if (!TextUtils.isEmpty(firstSymbol)) {
                    result = String.format(numFormat, firstSymbol);
                } else {
                    result = String.format(numFormat, s);
                }
                break;
            //total 总额
            case 3:
                String totalFormat = resources.getString(R.string.total_price);
                if (!TextUtils.isEmpty(lastSymbol)) {
                    result = String.format(totalFormat, lastSymbol);
                } else {
                    result = String.format(totalFormat, s);
                }

                break;
            case 4://针对市价买 如果是usd 就需要显示金额 其他的显示数量
                String tradeSjPrice = resources.getString(R.string.shijia_trade_price);
                if (!TextUtils.isEmpty(lastSymbol)) {
                    result = String.format(tradeSjPrice, lastSymbol);
                } else {
                    result = String.format(tradeSjPrice, s);
                }
                break;
            case 5://针对市价买 如果是usd 就需要显示金额 其他的显示数量
                numFormat = resources.getString(R.string.trade_amount);
                if (!TextUtils.isEmpty(lastSymbol)) {
                    result = String.format(numFormat, lastSymbol);
                } else {
                    result = String.format(numFormat, s);
                }
                break;
        }
        return result;

    }

    /**
     * 点击recyclerView的最佳成交价格
     *
     * @param isFirst 如果是第一次进入的话是
     */


    public void setClickItemPrice(String price, boolean isFirst) {
        //根据估值去判断是否是限价还是市价
//        clickEditListeners();
        int visibility = tvGuZhiTitle.getVisibility();
        if (visibility == VISIBLE) {
            if (!TextUtils.isEmpty(price)) {
                if (!TextUtils.isEmpty(clickBestPrice)) {
                    price = clickBestPrice;
                }
                etTradePrice.removeTextChangedListener();
                etTradePrice.setTextMoney(price);
                etTradePrice.setSelection();
                etTradePrice.addTextChangedListener();
                //传递过来的哪种币种的价格 如果想要usd 需要计算
                showGuZhi(price);
                //showClickTotalMoney(price);
            }
        }
    }

    /**
     * 清除view方法  切换买卖的时候处理的
     */
    private void clearView(boolean isXj) {
        if (isXj) {
            String price;//如果点击过右边的列表 就显示右边列表的价格 否则 看是买还是卖  然后相应赋值
            if (TextUtils.isEmpty(clickBestPrice)) {
                if (mBuyFlag) {
                    price = priceBestBuy;
                } else {
                    price = priceBestSell;
                }
            } else {
                price = clickBestPrice;
            }
            etTradePrice.setTextMoney(price);
            etTradeNumber.setDefualNumMoney(quantityPrecision, quantityPrecision);
        } else {//市价的买卖
            if (mBuyFlag) {
                etTradeNumber.isMoney(true);
                etTradeNumber.setDefualNumMoney(amountPrecision, amountPrecision);
            } else {
                etTradeNumber.setDefualNumMoney(quantityPrecision, quantityPrecision);
                etTradeNumber.isMoney(false);
            }
            etTradePrice.setTextMoney("");
        }
        etTradeTotalPrice.setTextMoney("", true);
        etTradeNumber.setTextMoney("");
    }

    //买入和卖出的按钮o
    public void onResume() {
        //btnBuySell.setSelected(mBuyFlag);
        //Log.i("Dupeng", "mBuyFlag : " + mBuyFlag);
        etTradeNumber.clearFocus();
        initlickBestPrice();
    }


    /**
     * 改变价格    可用资金           可以用的钱数
     *
     * @param money 改变的money
     *              true  buy
     *              false sell
     */
    private void changeview(String money, boolean isFlag) {
        showBuySellBuffer.delete(0, showBuySellBuffer.length());
        if (tv_available_price != null) {
            if (!TextUtils.isEmpty(money)) {
                showBuySellBuffer.append(money);
            }
        }
        if (isFlag) {
            if (!TextUtils.isEmpty(lastSymbol)) {
                if (!TextUtils.isEmpty(showBuySellBuffer.toString())) {
                    showBuySellBuffer.append(lastSymbol);
                }
                String s = showBuySellBuffer.toString();
                tv_available_price.setText(
                        TextUtils.isEmpty(s) ? context.getResources().getString(R.string.price_gang)
                                : s);
            }
        } else {
            if (!TextUtils.isEmpty(firstSymbol)) {
                if (!TextUtils.isEmpty(showBuySellBuffer.toString())) {
                    showBuySellBuffer.append(firstSymbol);
                }
                String s = showBuySellBuffer.toString();
                tv_available_price.setText(
                        TextUtils.isEmpty(s) ? context.getResources().getString(R.string.price_gang)
                                : s);
            }
        }
    }

    /**
     * 支付成功之后调用这个
     */
    public void paySuccess() {
        etTradeNumber.setTextMoney("");
        etTradeTotalPrice.setTextMoney("", true);
        percentBuy.setProgress(0);
        percentSell.setProgress(0);
    }

    /**
     * 切换交易对的时候 显示最佳成交价
     */
    public void setBestPrice(String bestPrice, boolean isfirst) {
        //传递过来的哪种币种的价格 如果想要usd 需要计算
        etTradePrice.clearFocus();
        setClickItemPrice(bestPrice, isfirst);
    }

    public void setOnSelectBuyOrSell(OnBuyOrSellClickListener onBuyOrSellClickListener) {
        this.onBuyOrSellClickListener = onBuyOrSellClickListener;
    }

    public interface OnBuyOrSellClickListener {
        void onBuyClick();

        void onSellClick();

        void resultSubmitParams(HashMap<String, String> params);
    }
}