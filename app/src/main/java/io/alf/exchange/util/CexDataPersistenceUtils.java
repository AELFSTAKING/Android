package io.alf.exchange.util;

import com.blankj.utilcode.util.StringUtils;

import org.consenlabs.tokencore.wallet.Identity;

import java.util.List;

import io.alf.exchange.Constant;
import io.alf.exchange.mvp.bean.CurrencyBean;
import io.alf.exchange.mvp.bean.ExchangeRateBean;
import io.alf.exchange.mvp.bean.MqttConfigBean;
import io.alf.exchange.mvp.bean.TotalAssetBean;
import io.cex.exchange.kotlin.coreutil.SpHelperKt;
import io.cex.mqtt.bean.QuotationBean;
import io.tick.base.util.BigDecimalUtil;
import io.tick.base.util.DataPersistenceUtils;
import io.tick.base.util.SpUtil;


public class CexDataPersistenceUtils extends DataPersistenceUtils {

    private static final String ACCOUNT = "account";
    private static final String USER_INFO = "userInfo";
    private static final String USER_BIND_INFO = "userBindInfo";
    private static final String CURRENCY_INFO = "currencyInfo";
    private static final String COUNTRY_LIST = "countryList";
    private static final String CURRENT_LEGAL_CURRENCY_INFO = "currentLegalCurrencyRate";
    private static final String ALL_LEGAL_CURRENCY_INFO = "allLegalCurrencyRate";
    private static final String HIDE_ASSETS = "hideAssets";
    private static final String ASSETS = "assets";
    private static final String QUOTATION_BEAN = "quotationBean";
    private static final String DEPTH_STEP_LIST = "DepthStepList";
    private static final String KLINE_RANGE = "klineRange";
    private static final String IS_MINUTE_HOUR_KLINE = "isMinuteHourKLine";
    private static final String SECURITY = "security";
    private static final String CHANNEL_TOKEN_LIST = "channelTokenList";
    private static final String USER_SYMBOL_LIST = "userSymbolList";
    private static final String ZA_ORDER = "zaOrder";
    private static final String MQTT_CONFIG = "mqttConfig";
    private static final String BALANCE = "balance";
    private static final String SUPPORT_CURRENCIES = "support_currencies";
    private static final String CURRENT_SYMBOL = "currentSymbol";
    private static final String REGISTERED = "registered";
    private static final String TOTAL_ASSET_BEAN = "TotalAssetBean";
    private static final String USDT_PRICE = "usdtPrice";

    public static void clear() {
        SpUtil.clear();
    }

    public static void putAccount(String account) {
        SpUtil.putObj(ACCOUNT, account);
    }

    public static String getAccount() {
        return SpUtil.getObj(ACCOUNT, String.class);
    }

    public static boolean isHideAssets() {
        return SpUtil.getObj(HIDE_ASSETS, Boolean.class);
    }

    public static void setHideAssets(boolean hideAssets) {
        SpUtil.putObj(HIDE_ASSETS, hideAssets);
    }

    public static void putQuotationBean(QuotationBean bean) {
        SpUtil.putObj(QUOTATION_BEAN, bean);
    }

    public static QuotationBean getQuotationBean() {
        return SpUtil.getObj(QUOTATION_BEAN, QuotationBean.class);
    }

    public static void putDepthStepList(String symbol, List<String> depthStepList) {
        SpUtil.putList(DEPTH_STEP_LIST + symbol, depthStepList);
    }

    public static List<String> getDepthStepList(String symbol) {
        return SpUtil.getList(DEPTH_STEP_LIST + symbol, String.class);
    }

    public static String getDefaultDepthCode(String symbol) {
        List<String> stepsList = getDepthStepList(symbol);
        return (stepsList != null && stepsList.size() > 0) ? stepsList.get(0) : "00001";
    }

    public static boolean isMinuteHourKLine() {
        return SpUtil.getObj(IS_MINUTE_HOUR_KLINE, Boolean.class);
    }

    public static void setMinuteHourKLine(boolean minuteHour) {
        SpUtil.putObj(IS_MINUTE_HOUR_KLINE, minuteHour);
    }

    public static boolean isRegistered(String address) {
        return SpUtil.getObj(REGISTERED + "_" + address, Boolean.class);
    }

    public static void setRegistered(String address, boolean registered) {
        SpUtil.putObj(REGISTERED + "_" + address, registered);
    }


    public static void putUserSymbolList(List<String> userSymbolList) {
        SpUtil.putObj(USER_SYMBOL_LIST, userSymbolList);
    }

    public static List<String> getUserSymbolList() {
        return SpUtil.getList(USER_SYMBOL_LIST, String.class);
    }

    public static boolean isZaOrder() {
        return SpUtil.getObj(ZA_ORDER, Boolean.class);
    }

    public static void setZaOrder(boolean azOrder) {
        SpUtil.putObj(ZA_ORDER, azOrder);
    }

    public static void putMqttConfig(MqttConfigBean bean) {
        SpUtil.putObj(MQTT_CONFIG, bean);
    }

    public static MqttConfigBean getMqttConfig() {
        return SpUtil.getObj(MQTT_CONFIG, MqttConfigBean.class);
    }

    public static boolean isLogin() {
        return Identity.getCurrentIdentity() != null;
    }

    public static void putAllLegalCurrencyInfo(ExchangeRateBean bean) {
        SpUtil.putObj(ALL_LEGAL_CURRENCY_INFO, bean);
    }

    public static ExchangeRateBean getAllLegalCurrencyInfo() {
        return SpUtil.getObj(ALL_LEGAL_CURRENCY_INFO, ExchangeRateBean.class);
    }

    public static void putCurrentLegalCurrencyInfo(ExchangeRateBean.ExchangeRateListBean bean) {
        SpUtil.putObj(CURRENT_LEGAL_CURRENCY_INFO, bean);
    }

    public static ExchangeRateBean.ExchangeRateListBean getCurrentLegalCurrencyInfo() {
        return SpUtil.getObj(CURRENT_LEGAL_CURRENCY_INFO,
                ExchangeRateBean.ExchangeRateListBean.class);
    }

    public static String getLatestKLineRange() {
        return SpHelperKt.getSpValue(KLINE_RANGE, Constant.MIN_15);
    }

    public static void putLatestKLineRange(String klineRange) {
        SpHelperKt.putSpValue(KLINE_RANGE, klineRange);
    }

    public static void putBalance(String chain, String currency, String balance) {
        SpUtil.putObj(BALANCE + "_" + chain.toLowerCase() + "_" + currency.toLowerCase(), balance);
    }

    public static String getBalance(String chain, String currency) {
        String balance = SpUtil.getObj(
                BALANCE + "_" + chain.toLowerCase() + "_" + currency.toLowerCase(), String.class);
        if (StringUtils.isEmpty(balance)) {
            return "0.00";
        } else {
            return BigDecimalUtil.stripTrailingZeros(BigDecimalUtil.formatByDecimalValue(balance));
        }
    }

    public static void putSupportCurrencies(List<CurrencyBean.ListBean> currencyBeanList) {
        SpUtil.putList(SUPPORT_CURRENCIES, currencyBeanList);
    }

    public static List<CurrencyBean.ListBean> getSupportCurrencies() {
        return SpUtil.getList(SUPPORT_CURRENCIES, CurrencyBean.ListBean.class);
    }

    public static void putCurrentSymbol(String currency) {
        SpUtil.putObj(CURRENT_SYMBOL, currency);
    }

    public static String getCurrentSymbol() {
        String symbol = SpUtil.getObj(CURRENT_SYMBOL, String.class);
        if (StringUtils.isEmpty(symbol)) {
            symbol = "ETH/PLANET";
        }
        return symbol;
    }

    public static void putTotalAssetBean(TotalAssetBean bean) {
        SpUtil.putObj(TOTAL_ASSET_BEAN, bean);
    }

    public static TotalAssetBean getTotalAssetBean() {
        return SpUtil.getObj(TOTAL_ASSET_BEAN, TotalAssetBean.class);
    }

    public static void putUsdtPrice(String chain, String currency, String price) {
        SpUtil.putObj(USDT_PRICE + "_" + chain.toLowerCase() + "_" + currency.toLowerCase(), price);
    }

    public static String getUsdtPrice(String chain, String currency) {
        String price = SpUtil.getObj(
                USDT_PRICE + "_" + chain.toLowerCase() + "_" + currency.toLowerCase(),
                String.class);
        if (StringUtils.isEmpty(price)) {
            return "0.00";
        } else {
            return BigDecimalUtil.stripTrailingZeros(price);
        }
    }
}
