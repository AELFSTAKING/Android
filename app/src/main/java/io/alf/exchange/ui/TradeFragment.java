package io.alf.exchange.ui;

import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import butterknife.BindView;
import io.alf.exchange.R;
import io.alf.exchange.ui.quotation.DetailQuotationPortraitActivity;
import io.alf.exchange.ui.quotation.QuotationsDialogFragment;
import io.alf.exchange.util.CexDataPersistenceUtils;
import io.alf.exchange.util.StringUtils;
import io.tick.base.eventbus.EventBusCenter;
import io.tick.base.eventbus.EventCode;
import io.tick.base.mvp.MvpFragment;
import io.tick.base.util.RxBindingUtils;

public class TradeFragment extends MvpFragment implements ISymbolProvider {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right)
    ImageView ivMarket;
    @BindView(R.id.fl_one)
    FrameLayout flOne;

    private static final String SYMBOL = "symbol";
    private static final String ACTION = "action";

    public static final String BUY = "buy";
    public static final String SELL = "sell";

    private TradeContentFragment mTradeContentFragment;
    private DelegateFragment mDelegateFragment;

    public static Bundle buildBundle(String action, String symbol) {
        Bundle args = new Bundle();
        args.putString(SYMBOL, symbol);
        args.putString(ACTION, action);
        return args;
    }

    public String getSymbol() {
        Bundle args = getArguments();
        String symbol = args != null ? args.getString(SYMBOL) : "";
        if (StringUtils.isEmpty(symbol)) {
            symbol = CexDataPersistenceUtils.getCurrentSymbol();
        }
        return symbol;
    }

    public String getAction() {
        Bundle args = getArguments();
        String action = args != null ? args.getString(ACTION) : BUY;
        if (StringUtils.isEmpty(action)) {
            action = BUY;
        }
        return action;
    }

    @Override
    protected void initPresenter() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_trade;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        add();
    }


    private void add() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        mTradeContentFragment = new TradeContentFragment();
        mDelegateFragment = new DelegateFragment();
        transaction.add(R.id.fl_one, mTradeContentFragment, "one");
        transaction.add(R.id.fl_two, mDelegateFragment, "two");
        transaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        tvTitle.setText(getSymbol());
        new Handler().postDelayed(() -> mTradeContentFragment.switchBuyOrSell(getAction()), 300);
        mTradeContentFragment.updateSymbol();
    }

    @Override
    protected void onVisibleToUser() {
        super.onVisibleToUser();
        tvTitle.setText(getSymbol());
        new Handler().postDelayed(() -> mTradeContentFragment.switchBuyOrSell(getAction()), 300);
        mTradeContentFragment.updateSymbol();
    }

    @Override
    protected void initEvents() {
        // 进入市场
        RxBindingUtils.clicks(
                v -> DetailQuotationPortraitActivity.startUp(getContext(), getSymbol()), ivMarket);
        RxBindingUtils.clicks(v -> showDialogFragment(), tvTitle);
    }

    private void showDialogFragment() {
        QuotationsDialogFragment dialogFragment = new QuotationsDialogFragment();
        dialogFragment.show(getChildFragmentManager(),
                QuotationsDialogFragment.class.getSimpleName());
    }

    @Override
    protected void onEventCallback(EventBusCenter event) {
        super.onEventCallback(event);
        if (event.code == EventCode.UPDATE_DEFAULT_SYMBOL) {
            Bundle args = getArguments();
            if (args != null) {
                args.putString(SYMBOL, CexDataPersistenceUtils.getCurrentSymbol());
            } else {
                args = new Bundle();
                args.putString(SYMBOL, CexDataPersistenceUtils.getCurrentSymbol());
                setArguments(args);
            }
            onResume();
        }
    }
}
