package io.alf.exchange.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import io.alf.exchange.R;
import io.alf.exchange.bean.OrderAction;
import io.alf.exchange.bean.OrderType;
import io.alf.exchange.mvp.bean.OrderDetailBean;
import io.alf.exchange.mvp.bean.OrderHistoryBean;
import io.alf.exchange.mvp.presenter.QueryOrderDetailPresenter;
import io.alf.exchange.mvp.view.QueryOrderDetailView;
import io.alf.exchange.ui.adapter.DealDetailAdapter;
import io.alf.exchange.util.EmptyViewUtil;
import io.alf.exchange.util.SpannableUtils;
import io.alf.exchange.util.StringUtils;
import io.alf.exchange.util.WalletUtils;
import io.tick.base.mvp.MvpActivity;
import io.tick.base.util.ActivityStartUtils;
import io.tick.base.util.BigDecimalUtil;
import io.tick.base.util.DateUtil;

public class OrderDetailActivity extends MvpActivity implements QueryOrderDetailView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_symbol_title)
    TextView tvSymbol;
    @BindView(R.id.tv_buy_sell)
    TextView tvBuySell;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_price_status)
    TextView tvPriceStatus;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.tv_order_price)
    TextView tvOrderPrice;
    @BindView(R.id.tv_fee)
    TextView tvFee;
    @BindView(R.id.tv_deal_amount)
    TextView tvDealAmount;
    @BindView(R.id.tv_txid)
    TextView tvTxID;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private DealDetailAdapter adapter;

    private QueryOrderDetailPresenter mQueryOrderDetailPresenter;

    private static final String BEAN = "bean";
    private OrderHistoryBean.ListBean bean;

    public static void startUp(Context context, OrderHistoryBean.ListBean bean) {
        ActivityStartUtils.jump(context, OrderDetailActivity.class,
                intent -> intent.putExtra(BEAN, bean));
    }

    private OrderHistoryBean.ListBean getCurrencyBean() {
        if (bean == null) {
            bean = (OrderHistoryBean.ListBean) getIntent().getSerializableExtra(BEAN);
        }
        return bean;
    }

    @Override
    protected void initPresenter() {
        mQueryOrderDetailPresenter = registerPresenter(new QueryOrderDetailPresenter(), this);
    }

    @Override
    protected void initData() {
        String address = WalletUtils.getAddress();
        if (!StringUtils.isEmpty(address)) {
            mQueryOrderDetailPresenter.queryOrderDetail(getCurrencyBean().orderNo, address);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_detail;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        tvTitle.setText("交易详情");
        adapter = new DealDetailAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initEvents() {

    }

    @Override
    public void onQueryOrderDetail(OrderDetailBean bean) {
        if (bean != null) {
            tvSymbol.setText(bean.symbol);
            switch (bean.action) {
                case OrderAction.BUY:
                    tvBuySell.setText("买入");
                    tvBuySell.setTextColor(getResources().getColor(R.color.color_deposit));
                    tvBuySell.setBackgroundResource(R.drawable.bg_tx_type_deposit);
                    break;
                case OrderAction.SELL:
                    tvBuySell.setText("卖出");
                    tvBuySell.setTextColor(getResources().getColor(R.color.color_decrease));
                    tvBuySell.setBackgroundResource(R.drawable.bg_decrease);
                    break;
            }
            tvTime.setText(DateUtil.formatDateTime(DateUtil.FMT_HH_MM_MM_DD, bean.utcCreate));
            switch (bean.orderType) {
                case OrderType.LMT: {
                    tvPriceStatus.setText(R.string.limit_price);
                    break;
                }
                case OrderType.MKT: {
                    tvPriceStatus.setText(R.string.cur_best_price);
                    break;
                }
            }
            if (StringUtils.equals("0", bean.status)) {
                if (StringUtils.equals("3", bean.state)) {
                    tvStatus.setText("已完成");
/*                } else if (StringUtils.equals(bean.state, "2")) {
                    tvStatus.setText("部分成交");*/
                } else {
                    tvStatus.setText("正常");
                }
            } else if (StringUtils.equals("1", bean.status)) {
                tvStatus.setText("已取消");
            } else if (StringUtils.equals("2", bean.status)) {
                tvStatus.setText("处理中");
            } else if (StringUtils.equals("4", bean.status)) {
                tvStatus.setText("待确认");
            }
            if (StringUtils.equals(bean.orderType, OrderType.MKT)
                    && StringUtils.equals(bean.action, OrderAction.BUY)) {
                //成交数量/数量
                SpannableUtils.setShowTextView(tvNumber,
                        BigDecimalUtil.stripTrailingZeros(
                                BigDecimalUtil.sub(bean.quantity, bean.quantityRemaining)) + "/"
                                + 0,
                        this,
                        15, 15);
            } else {
                //成交数量/数量
                SpannableUtils.setShowTextView(tvNumber,
                        BigDecimalUtil.stripTrailingZeros(
                                BigDecimalUtil.sub(bean.quantity, bean.quantityRemaining)) + "/"
                                + BigDecimalUtil.stripTrailingZeros(bean.quantity),
                        this,
                        15, 15);
            }
            //均价/价格
            SpannableUtils.setShowTextView(tvOrderPrice,
                    BigDecimalUtil.stripTrailingZeros(bean.priceAverage) + "/"
                            + BigDecimalUtil.stripTrailingZeros(bean.priceLimit), this, 15, 15);
            tvFee.setText(String.format("%s %s", BigDecimalUtil.stripTrailingZeros(bean.fee),
                    bean.feeCurrency));
            String dealAmount = BigDecimalUtil.mul(
                    BigDecimalUtil.sub(bean.quantity, bean.quantityRemaining), bean.priceAverage);
            dealAmount = BigDecimalUtil.stripTrailingZeros(dealAmount);
            tvDealAmount.setText(String.format("%s %s", dealAmount, bean.symbol.split("/")[1]));
            tvTxID.setMovementMethod(LinkMovementMethod.getInstance());
            SpannableUtils.setHtmlTextView(tvTxID, StringUtils.ellipsize(bean.txId, 30, 15),
                    bean.url);
            if (bean.tradeDeals != null && bean.tradeDeals.size() > 0) {
                adapter.setNewData(bean.tradeDeals);
                adapter.notifyDataSetChanged();
            } else {
                adapter.setNewData(null);
                View emptyView = EmptyViewUtil.setEmpty(recyclerView, this, "暂无交易明细", 50);
                adapter.setEmptyView(emptyView);
            }
        }
    }
}
