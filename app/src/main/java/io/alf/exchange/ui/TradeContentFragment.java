package io.alf.exchange.ui;

import static io.tick.base.eventbus.EventCode.MQTT_CONNECTION_STATUS;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import io.alf.exchange.MqttTopicManager;
import io.alf.exchange.R;
import io.alf.exchange.bean.TradeEvent;
import io.alf.exchange.dialog.CreateAccountDialog;
import io.alf.exchange.dialog.PasswordDialog;
import io.alf.exchange.mvp.bean.CreateOrderBean;
import io.alf.exchange.mvp.bean.SendTxBean;
import io.alf.exchange.mvp.bean.TradeSymbolInfoBean;
import io.alf.exchange.mvp.presenter.CreateOrderPresenter;
import io.alf.exchange.mvp.presenter.IdentityPresenter;
import io.alf.exchange.mvp.presenter.QueryDepthStepPresenter;
import io.alf.exchange.mvp.presenter.QueryOrderBookPresenter;
import io.alf.exchange.mvp.presenter.QuerySymbolTradeInfoPresenter;
import io.alf.exchange.mvp.presenter.SendOrderTxPresenter;
import io.alf.exchange.mvp.view.CreateOrderView;
import io.alf.exchange.mvp.view.QueryDepthStepView;
import io.alf.exchange.mvp.view.QueryOrderBookView;
import io.alf.exchange.mvp.view.QuerySymbolTradeInfoView;
import io.alf.exchange.mvp.view.SendOrderTxView;
import io.alf.exchange.util.CexDataPersistenceUtils;
import io.alf.exchange.util.FilterOrderBookUtil;
import io.alf.exchange.util.SignTxUtil;
import io.alf.exchange.util.StringUtils;
import io.alf.exchange.util.Validator;
import io.alf.exchange.util.WalletUtils;
import io.alf.exchange.widget.SelectorBuyAndSellView;
import io.alf.exchange.widget.TradeDataView;
import io.cex.mqtt.MQTTStateManager;
import io.cex.mqtt.bean.OrderBookBean;
import io.cex.mqtt.bean.StepBean;
import io.tick.base.eventbus.EventBusCenter;
import io.tick.base.eventbus.EventCode;
import io.tick.base.mvp.MvpFragment;

public class TradeContentFragment extends MvpFragment implements
        TradeDataView.OnChangeDepthListener, QueryOrderBookView,
        QueryDepthStepView, QuerySymbolTradeInfoView, SendOrderTxView, CreateOrderView {

    @BindView(R.id.left)
    SelectorBuyAndSellView buyAndSellView;
    @BindView(R.id.right)
    TradeDataView orderBookView;

    private String depthCode;

    private boolean firstUpdateConfigAfterChangeSymbol = true;

    // 显示卖
    public static final String IS_SHOW_SELL = "isShowSell";
    //显示买方
    public static final String IS_SHOW_BUY = "isShowBuy";
    //显示买卖
    public static final String IS_SHOW_AND_BUY = "isShowBuyAndSell";
    // 显示类型
    public static String IS_SHOW_TYPE = IS_SHOW_AND_BUY;

    private QueryOrderBookPresenter mQueryOrderBookPresenter;
    private QueryDepthStepPresenter mQueryDepthStepPresenter;
    private QuerySymbolTradeInfoPresenter mQuerySymbolTradeInfoPresenter;
    private IdentityPresenter mIdentityPresenter;
    private CreateOrderPresenter mCreateOrderPresenter;
    private SendOrderTxPresenter mSendOrderTxPresenter;
    private HashMap<String, String> mParams = new HashMap<>();
    private PasswordDialog mPasswordDialog;
    private CreateAccountDialog mCreateAccountDialog;
    private boolean first = true;

    @Override
    protected void initPresenter() {
        mQueryOrderBookPresenter = registerPresenter(new QueryOrderBookPresenter(), this);
        mQueryDepthStepPresenter = registerPresenter(new QueryDepthStepPresenter(), this);
        mCreateOrderPresenter = registerPresenter(new CreateOrderPresenter(), this);
        mQuerySymbolTradeInfoPresenter = registerPresenter(new QuerySymbolTradeInfoPresenter(),
                this);
        mIdentityPresenter = registerPresenter(new IdentityPresenter(), this);
        mSendOrderTxPresenter = registerPresenter(new SendOrderTxPresenter(), this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mQueryOrderBookPresenter.queryOrderBook(getSymbol(), getDepthCode());
        mQueryDepthStepPresenter.queryDepthStep(getSymbol());
    }

    @Override
    protected void onVisibleToUser() {
        super.onVisibleToUser();
        mQueryOrderBookPresenter.queryOrderBook(getSymbol(), getDepthCode());
        mQueryDepthStepPresenter.queryDepthStep(getSymbol());
    }

    public String getSymbol() {
        Fragment parent = getParentFragment();
        if ((parent instanceof TradeFragment)) {
            return ((TradeFragment) parent).getSymbol();
        } else {
            return "-- / --";
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_trade_up_content;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        refreshSymbol();
        if (CexDataPersistenceUtils.isLogin()) {
            buyAndSellView.setTouchable(true);
        } else {
            buyAndSellView.setTouchable(false);
        }
        orderBookView.setOnChangeDepthListener(this);
    }

    private void refreshSymbol() {
        buyAndSellView.initSymbolSplitStr(getSymbol());
    }

    public String getDepthCode() {
        if (TextUtils.isEmpty(depthCode)) {
            depthCode = CexDataPersistenceUtils.getDefaultDepthCode(getSymbol());
        }
        return depthCode;
    }

    @Override
    protected void initEvents() {
        // 市价，限价选择
        buyAndSellView.intOrderTypeListener(getActivity());
        // 买入，卖出点击
        buyAndSellView.setOnSelectBuyOrSell(new SelectorBuyAndSellView.OnBuyOrSellClickListener() {

            @Override
            public void onBuyClick() {
                if (mIdentityPresenter.getIdentity() != null) {
                    buyAndSellView.getSubmitParams(getSymbol());
                } else {
                    mCreateAccountDialog = new CreateAccountDialog(getContext(),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mCreateAccountDialog.dismiss();
                                    EventBusCenter.post(EventCode.CREATE_IMPORT_ACCOUNT);
                                }
                            });
                    mCreateAccountDialog.show();
                }
            }

            @Override
            public void onSellClick() {
                if (CexDataPersistenceUtils.isLogin()) {
                    buyAndSellView.getSubmitParams(getSymbol());
                } else {
                    mCreateAccountDialog = new CreateAccountDialog(getContext(),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mCreateAccountDialog.dismiss();
                                    EventBusCenter.post(EventCode.CREATE_IMPORT_ACCOUNT);
                                }
                            });
                    mCreateAccountDialog.show();
                }
            }

            @Override
            public void resultSubmitParams(HashMap<String, String> params) {
                String address = WalletUtils.getAddress();
                if (!StringUtils.isEmpty(address)) {
                    params.put("address", WalletUtils.getAddress());
                    mParams.clear();
                    mParams.putAll(params);
                    mPasswordDialog = new PasswordDialog(getContext(), v -> {
                        TextView passwordView = (TextView) v;
                        if (Validator.checkPassword(getContext(), passwordView)) {
                            mCreateOrderPresenter.createUserOrder(passwordView.getText().toString(),
                                    mParams);
                            mPasswordDialog.dismiss();
                        }
                    });
                    mPasswordDialog.show();
                } else {
                    toast("没有钱包地址");
                }
            }
        });
        orderBookView.initListeners(getActivity());
    }


    public void updateSymbol() {
        firstUpdateConfigAfterChangeSymbol = true;
        buyAndSellView.initSymbolSplitStr(getSymbol());
        buyAndSellView.beforSymbolclearData();
        orderBookView.initSymbolSplitStr(getSymbol());
        //查询深度聚合数字
        mQueryDepthStepPresenter.queryDepthStep(getSymbol());
        if (CexDataPersistenceUtils.isLogin()) {
            String address = WalletUtils.getAddress();
            if (!StringUtils.isEmpty(address)) {
                mQuerySymbolTradeInfoPresenter.queryTradeSymbolInfo(getSymbol(), address);
            }
        }
        if (TextUtils.isEmpty(this.depthCode)) {
            List<String> stepsList = CexDataPersistenceUtils.getDepthStepList(getSymbol());
            this.depthCode = (stepsList != null && stepsList.size() > 0) ? stepsList.get(0)
                    : "00001";
        }
        // 1.查询和订阅右边最新交易聚合数据并更新UI。
        mQueryOrderBookPresenter.queryOrderBook(getSymbol(), this.depthCode);
        MqttTopicManager.subscribeOrderBookData(getSymbol(), this.depthCode);

/*        // 2.查询和订阅最新深度图数据并更新左下角深度图。
        queryDepthDataPresenter.queryDepthData(getSymbol());
        MqttTopicManager.subscribeDepthData(getSymbol());*/
    }

    @Override
    public void onChangeDepth(String depthCode) {
        this.depthCode = depthCode;
        mQueryOrderBookPresenter.queryOrderBook(getSymbol(), this.depthCode);
        MqttTopicManager.subscribeOrderBookData(getSymbol(), this.depthCode);
    }

    @Override
    public void onQueryOrderBook(OrderBookBean orderBookBean) {
        OrderBookBean data = FilterOrderBookUtil.filterOrderBook(getSymbol(),
                this.depthCode, orderBookBean);
        // 更新右方订单簿数据
        if (data != null) {
            setOrderBookData(data);
        } else if (TextUtils.isEmpty(orderBookBean.symbol)) {
            setOrderBookData(null);
        }
    }

    // 设置右侧订单薄数据
    public void setOrderBookData(OrderBookBean orderBookBean) {
        orderBookView.setRecyclerViewData(orderBookBean);
        String serverSymbol = orderBookBean.symbol;
        orderBookView.initSymbolSplitStr(serverSymbol);
        orderBookView.setDefulNumber(serverSymbol);
    }

    @Override
    public void onCreateUserOrder(String password, CreateOrderBean orderBean) {
        if (orderBean != null) {
            String signedRawTransaction = SignTxUtil.signTx(
                    orderBean.createOrderTxResp.rawTransaction, password);
            mCreateOrderPresenter.sendOrderTx(orderBean.order.orderNo, signedRawTransaction);
        }
    }

    @Override
    public void onSendOrderTx(String orderId, SendTxBean data) {
        if (data != null) {
            mCreateOrderPresenter.payCallback(orderId, data.txHash);
        }
        toast("下单请求发送成功");
    }

    @Override
    public void onPayCallback(String orderId, boolean success) {
        EventBusCenter.post(EventCode.UPDATE_DELEGATE);
    }

    public void switchBuyOrSell(String action) {
        if (TradeEvent.BUY.equals(action)) {
            buyAndSellView.onClickBuy();
        } else if (TradeEvent.SELL.equals(action)) {
            buyAndSellView.onClickSell();
        }
    }

    @Override
    public void onQueryDepthStep(List<String> depthStepList) {
        if (depthStepList != null && depthStepList.size() > 0) {
            List<StepBean> stepBeans = new ArrayList<>();
            for (String step : depthStepList) {
                if (!TextUtils.isEmpty(step)) {
                    StepBean bean = new StepBean();
                    bean.code = step;
                    bean.decimalValue = step.length() - 1;
                    bean.type = 0;
                    stepBeans.add(bean);
                }
            }
            if (stepBeans.size() > 0) {
                orderBookView.updateRange(stepBeans);
            }
        }
    }


    @Override
    public void onQueryTradeSymbolInfo(boolean success, TradeSymbolInfoBean data) {
        if (success) {
            if (TextUtils.equals(data.symbol, getSymbol())) {
                // 更新最新交易信息
                buyAndSellView.setConFigInfo(data);
                // equal判断是为了过滤上一个交易对的信息(切换交易对前的)
                if (firstUpdateConfigAfterChangeSymbol) {
                    setBuyAndSellPrice();
                    firstUpdateConfigAfterChangeSymbol = false;
                }
            }
        }
    }

    private void setBuyAndSellPrice() {
        Fragment parent = getParentFragment();
        if (parent instanceof TradeFragment) {
            switchBuyOrSell(((TradeFragment) parent).getAction());
        }
    }

    @Override
    protected void onEventCallback(EventBusCenter event) {
        super.onEventCallback(event);
        if (isVisibleToUser()) {
            if (event.code == MQTT_CONNECTION_STATUS) {
                MQTTStateManager.StateBean stateBean = (MQTTStateManager.StateBean) event.data;
/*                if (rightTradeDataView != null) {
                    rightTradeDataView.setMqttSignal(stateBean.connected);
                }*/
            } else if (event.code == EventCode.MQTT_ORDER_BOOK) {
                OrderBookBean orderBookBean = FilterOrderBookUtil.filterOrderBook(getSymbol(),
                        this.depthCode, (OrderBookBean) event.data);
                if (orderBookBean != null) {
                    setOrderBookData(orderBookBean);
                }
            }
            long currentTime = System.currentTimeMillis();
            // 10s 刷新一次
            if ((currentTime - lastUpdateTime) > 10000) {
                lastUpdateTime = currentTime;
                // TODO: 2019/1/30
                querySymbol(getSymbol());
            }
        }
    }

    private static long lastUpdateTime = 0;

    public void querySymbol(String symbol) {
        lastUpdateTime = System.currentTimeMillis();
        if (!TextUtils.isEmpty(symbol)) {
            if (CexDataPersistenceUtils.isLogin()) {
                mParams.put("symbol", symbol);
                String address = WalletUtils.getAddress();
                if (!StringUtils.isEmpty(address)) {
                    mQuerySymbolTradeInfoPresenter.queryTradeSymbolInfo(symbol, address, false);//查询
                }
            } else {
                if (buyAndSellView != null) {
                    buyAndSellView.initSymbolSplitStr(symbol);
                }
            }
        }
    }
}
