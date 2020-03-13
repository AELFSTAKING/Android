package io.alf.exchange.util;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import io.alf.exchange.mvp.bean.MqttConfigBean;
import io.cex.mqtt.bean.HotSymbolBean;
import io.cex.mqtt.bean.QuotationBean;
import io.cex.mqtt.bean.QuotationGroupBean;
import io.cex.mqtt.bean.QuotationGroupByAreaBean;


public class FilterHideQuotationUtil {

    public static List<QuotationBean> filterHideQuotation(List<QuotationBean> dataList) {
        if (dataList != null && dataList.size() > 0 && hideQuotation()) {
            List<QuotationBean> ret = new ArrayList<>();
            for (QuotationBean bean : dataList) {
                if (TextUtils.equals(bean.isShow, "1")) {
                    ret.add(bean);
                }
            }
            return ret;
        } else {
            return dataList;
        }
    }

    public static List<HotSymbolBean> filterHideHotQuotation(List<HotSymbolBean> dataList) {
        if (dataList != null && dataList.size() > 0 && hideQuotation()) {
            List<HotSymbolBean> ret = new ArrayList<>();
            for (HotSymbolBean bean : dataList) {
                if (TextUtils.equals(bean.isShow, "1")) {
                    ret.add(bean);
                }
            }
            return ret;
        } else {
            return dataList;
        }
    }

    public static List<QuotationGroupBean> filterHideQuotationsGroupBy(List<QuotationGroupBean> dataList) {
        if (dataList != null && dataList.size() > 0 && hideQuotation()) {
            List<QuotationGroupBean> ret = new ArrayList<>();
            for (QuotationGroupBean bean : dataList) {
                if (bean != null && bean.list != null && bean.list.size() > 0) {
                    List<QuotationGroupBean.SymbolQuotation> list = new ArrayList<>();
                    for (QuotationGroupBean.SymbolQuotation symbolQuotation : bean.list) {
                        if (TextUtils.equals(symbolQuotation.isShow, "1")) {
                            list.add(symbolQuotation);
                        }
                    }
                    if (list.size() > 0) {
                        bean.list = list;
                        ret.add(bean);
                    }
                }
            }
            return ret;
        } else {
            return dataList;
        }
    }

    public static List<QuotationGroupByAreaBean> filterHideQuotationsGroupByArea(List<QuotationGroupByAreaBean> dataList) {
        if (dataList != null && dataList.size() > 0 && hideQuotation()) {
            List<QuotationGroupByAreaBean> ret = new ArrayList<>();
            for (QuotationGroupByAreaBean bean : dataList) {
                if (bean != null && bean.list != null && bean.list.size() > 0) {
                    List<QuotationGroupBean> list = filterHideQuotationsGroupBy(bean.list);
                    if (list.size() > 0) {
                        bean.list = list;
                        ret.add(bean);
                    }
                }
            }
            return ret;
        } else {
            return dataList;
        }
    }

    public static boolean hideQuotation(QuotationBean bean) {
        if (bean != null && TextUtils.equals(bean.isShow, "0") && hideQuotation()) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean hideQuotation() {
        MqttConfigBean mqttConfigBean = CexDataPersistenceUtils.getMqttConfig();
        if (mqttConfigBean != null && TextUtils.equals(mqttConfigBean.isShow, "0")) {
            return true;
        } else {
            return false;
        }
    }


}
