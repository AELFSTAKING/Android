package io.tick.base.net;

import io.tick.base.BuildConfig;

public class BaseUrl {

    public final static String BASE_URL = BuildConfig.baseUrl;
    public final static String MQTT_URL = BuildConfig.mqttUrl;

    // 获取最新APP版本
    public final static String UPDATE_URL =
            BASE_URL + "dex-cms/cms/app/version/getLatestAppVersion";
    // 用户创建或导入eth地址时，调用
    public static final String REGISTER_ADDRESS = BASE_URL + "staking-dex/register_address";
    // 绑定地址
    public static final String BIND_ADDRESS = BASE_URL + "staking-dex/bind_address";
    // 绑定地址
    public static final String DELETE_BIND_ADDRESS = BASE_URL + "staking-dex/unbind_address";
    // 绑定地址
    public static final String QUERY_BIND_ADDRESS = BASE_URL + "staking-dex/bind_address_list";
    // 获取网页端需要订阅mqtt的配置信息
    public static final String QUERY_MQTT_INFO = BASE_URL + "staking-dex/mqtt/query/configInfo";
    // 交易对信息(最新价格、24h最高价、24h最低价、成交量、涨跌幅等信息)
    public static final String QUERY_SYMBOL_QUOTATION =
            BASE_URL + "staking-dex/quotation/symbolQuotation";
    // 行情-K线历史数据
    public static final String QUERY_QUOTATION_HISTORY =
            BASE_URL + "staking-dex/kline/get/quotationHistory";
    // 获取订单薄信息
    public static final String QUERY_ORDER_BOOK = BASE_URL + "staking-dex/order/query/orderBook";
    // 最新成交行情
    public static final String QUERY_DEAL_LIST = BASE_URL + "staking-dex/order/query/dealList";
    // 行情-深度步长列表
    public static final String QUERY_DEPTH_STEP = BASE_URL + "staking-dex/order/query/depthStep";
    // 查询深度图
    public static final String QUERY_DEPTH_DATA = BASE_URL + "staking-dex/quotation/depthData";
    // 查询交易对相关信息
    public static final String QUERY_TRADE_SYMBOL_INFO =
            BASE_URL + "staking-dex/user/order/query/tradeSymbolInfo";
    // 查询业务委托订单(当前委托)
    public static final String QUERY_CURRENT_ORDER_LIST = BASE_URL + "staking-dex/order_current";
    // 查询业务委托订单(历史委托)
    public static final String QUERY_HISTORY_ORDER_LIST = BASE_URL + "staking-dex/order_histories";
    // 查询业务委托订单(历史委托)
    public static final String QUERY_CURRENCY_LIST = BASE_URL + "staking-dex/currency_list";
    // 挂单
    public static final String CREATE_ORDER = BASE_URL + "staking-dex/create_order";
    // 挂单回调
    public static final String PAY_CALLBACK = BASE_URL + "staking-dex/order/status/payCallback";
    // 撤单
    public static final String CANCEL_USER_ORDER = BASE_URL + "staking-dex/cancel_order";
    // 撤单回调
    public static final String CANCEL_CALLBACK =
            BASE_URL + "staking-dex/order/status/cancelCallback";
    // 查询总资产
    public static final String QUERY_TOTAL_ASSET = BASE_URL + "staking-dex/quotation/totalAsset";
    // 订单（委托单）明细
    public static final String QUERY_ORDER_DETAIL = BASE_URL + "staking-dex/order_detail";
    // 订单（委托单）明细
    public static final String QUERY_DEPOSIT_ADDRESS = BASE_URL + "staking-dex/deposit_address";
    // 订单（委托单）明细
    public static final String QUERY_TX_HISTORY = BASE_URL + "staking-dex/tx_history";
    // 创建转账交易
    public static final String CREATE_WITHDRAW_ORDER = BASE_URL + "staking-dex/withdraw";

    // 创建转账交易
    public static final String WITHDRAW_CALLBACK = BASE_URL + "staking-dex/withdraw/callback";

    // 充值奖励
    public static final String QUERY_DEPOSIT_REWARD = BASE_URL + "staking-dex/deposit_reward_list";
    // 充值奖励
    public static final String QUERY_MINING_REWARD = BASE_URL + "staking-dex/balance_reward_list";
    // 奖励发放记录
    public static final String QUERY_REWARD_RECORD = BASE_URL + "staking-dex/send_reward_list";
    // 奖励信息
    public static final String QUERY_REWARD_NOTICE = BASE_URL + "staking-dex/reward";
    // 奖励信息
    public static final String QUERY_CURRENCY_INFO = BASE_URL + "staking-dex/currency/introduction";


    // 发送订单
    public static final String SEND_ORDER_TX = BASE_URL + "gateway/send_stacking_order_tx";
    // 发送订单
    public static final String SEND_CANCEL_ORDER_TX =
            BASE_URL + "gateway/send_stacking_order_cancel_tx";
    // 查询余额
    public static final String QUERY_BALANCE = BASE_URL + "gateway/balance";
    // 查询余额
    public static final String QUERY_PRICE = BASE_URL + "staking-dex/quotation/usdtPrice";
    // 查询预估转账gas费
    public static final String QUERY_TRANSFER_FEE = BASE_URL + "gateway/estimate_transfer_fee";
    // 创建转账交易
    public static final String CREATE_TRANSFER_TX = BASE_URL + "gateway/create_transfer_tx";
    // 发送转账交易
    public static final String SEND_TRANSFER_TX = BASE_URL + "gateway/send_transfer_tx";
    // 交易详情查询
    public static final String QUERY_TX_INFO = BASE_URL + "gateway/transaction";

    // 获取首页banner、滚动公告
    public final static String GET_ALL_ARTICLE_LIST = BASE_URL + "article/query/allArticleList";

    // 获取所有交易区行情数据，根据计价币种进行归类
    public static final String QUERY_QUOTATION_GROUP_BY =
            BASE_URL + "quotation/query/symbolQuotationGroupBy";
    // 获取所有交易区行情数据，根据计价币种进行归类
    public static final String QUERY_QUOTATION_GROUP_BY_AREA =
            BASE_URL + "quotation/query/symbolQuotationGroupByInTradeArea";


}
