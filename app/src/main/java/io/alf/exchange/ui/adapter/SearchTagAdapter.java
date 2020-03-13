package io.alf.exchange.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import io.alf.exchange.R;
import io.alf.exchange.bean.HistoryDBEntity;
import io.alf.exchange.widget.flowlayout.FlowLayout;
import io.alf.exchange.widget.flowlayout.TagAdapter;


/**
 * Created by Administrator on 2018/4/8.
 */

public class SearchTagAdapter extends TagAdapter {

    public SearchTagAdapter(List<HistoryDBEntity> datas) {
        super(datas);
    }

    //    /**
//     * @param symbolType  交易对
//     * @param price       最新成交价
//     * @param usdPrice    最新成交价折合USD价格
//     * @param wavePrice   涨跌价格	不带+-
//     * @param wavePercent 涨跌百分
//     * @param direction   价格变化
//     * @param quantity    24小时成交量
//     * @param maxPrice    24小时最高价
//     * @param minPrice    24小时最低价
//     */
    @Override
    public View getView(FlowLayout parent, int position, Object o) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_tag,
                parent, false);
        TextView tv_history = (TextView) inflate.findViewById(R.id.tv_history);
        if (o instanceof HistoryDBEntity) {
            HistoryDBEntity entity = (HistoryDBEntity) o;
            String symbol = entity.getSymbol();
            tv_history.setText(TextUtils.isEmpty(symbol) ? "" : symbol);
        }
        return inflate;
    }
}
