package io.alf.exchange.ui.quotation;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.alf.exchange.R;
import io.alf.exchange.mvp.bean.Favorites;
import io.alf.exchange.mvp.presenter.FavoritesPresenter;
import io.alf.exchange.mvp.view.FavoritesView;
import io.alf.exchange.ui.adapter.FavoriteAdapter;
import io.alf.exchange.util.CexDataPersistenceUtils;
import io.cex.mqtt.bean.QuotationBean;
import io.tick.base.eventbus.EventBusCenter;
import io.tick.base.eventbus.EventCode;
import io.tick.base.mvp.MvpFragment;


public class QuotationAllFragment extends MvpFragment implements FavoritesView {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ll_change)
    LinearLayout llChange;
    @BindView(R.id.ll_favorite)
    LinearLayout llFavorite;

    private FavoriteAdapter quotationAdapter;

    private static final String GROUP = "group";
    private static final String SYMBOL_SELECTOR = "symbolSelector";

    private String mGroup;
    private boolean symbolSelector;
    private List<QuotationBean> mData = new ArrayList<>();

    private FavoritesPresenter favoritesPresenter;

    public static QuotationAllFragment newInstance(String group) {
        return newInstance(group, false);
    }

    public static QuotationAllFragment newInstance(String group, boolean symbolSelector) {
        QuotationAllFragment fragment = new QuotationAllFragment();
        Bundle args = new Bundle();
        args.putString(GROUP, group);
        args.putBoolean(SYMBOL_SELECTOR, symbolSelector);
        fragment.setArguments(args);
        return fragment;
    }

    public String getGroup() {
        if (TextUtils.isEmpty(mGroup)) {
            Bundle args = getArguments();
            mGroup = args != null ? args.getString(GROUP) : "";
        }
        return mGroup;
    }

    public boolean isSymbolSelector() {
        if (!symbolSelector) {
            Bundle args = getArguments();
            symbolSelector = args != null && args.getBoolean(SYMBOL_SELECTOR);
        }
        return symbolSelector;
    }

    @Override
    protected void initPresenter() {
        favoritesPresenter = registerPresenter(new FavoritesPresenter(), this);
        favoritesPresenter.queryFavorites();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_quotations_child;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initAdapter();
        llChange.setVisibility(isSymbolSelector() ? View.GONE : View.VISIBLE);
        llFavorite.setVisibility(isSymbolSelector() ? View.VISIBLE : View.GONE);
    }

    private void initAdapter() {
        quotationAdapter = new FavoriteAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(quotationAdapter);
        quotationAdapter.setShowFavorite(isSymbolSelector());
        quotationAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mData != null && mData.size() > position) {
                    QuotationBean item = mData.get(position);
                    if (isSymbolSelector()) {
                        CexDataPersistenceUtils.putCurrentSymbol(item.symbol);
                        EventBusCenter.post(EventCode.UPDATE_DEFAULT_SYMBOL);
                        dismissParentDialogFragment();
                    } else {
                        DetailQuotationPortraitActivity.startUp(
                                getContext(),
                                item.symbol,
                                item.lastPrice,
                                item.lastUsdPrice,
                                item.wavePrice,
                                item.wavePercent,
                                item.direction,
                                item.lowestPrice,
                                item.highestPrice,
                                item.quantity
                        );
                    }
                }
            }
        });
        quotationAdapter.setOnItemChildClickListener(
                new BaseQuickAdapter.OnItemChildClickListener() {
                    @Override
                    public void onItemChildClick(BaseQuickAdapter adapter, View view,
                            int position) {
                        if (view.getId() == R.id.iv_favorite) {
                            String symbol = mData.get(position).symbol;
                            if (quotationAdapter.getFavorites() != null
                                    && quotationAdapter.getFavorites().getSymbolList() != null
                                    && quotationAdapter.getFavorites().getSymbolList().size() > 0
                                    && quotationAdapter.getFavorites().getSymbolList().contains(
                                    symbol)) {
                                favoritesPresenter.deleteFavorite(symbol);
                            } else {
                                favoritesPresenter.addFavorite(symbol);
                            }
                        }
                    }
                });
    }

    @Override
    protected void onVisibleToUser() {
        super.onVisibleToUser();
        favoritesPresenter.queryFavorites();
    }

    @Override
    public void onAddFavorite(String symbol, boolean success) {
        favoritesPresenter.queryFavorites();
    }

    @Override
    public void onDeleteFavorite(String symbol, boolean success) {
        favoritesPresenter.queryFavorites();
    }

    @Override
    public void onQueryFavorites(Favorites data) {
        if (data != null) {
            quotationAdapter.setFavorites(data);
        }
        quotationAdapter.notifyDataSetChanged();
    }


    private void dismissParentDialogFragment() {
        Fragment parent = getParentFragment();
        if (parent instanceof QuotationsDialogFragment) {
            ((QuotationsDialogFragment) parent).dismiss();
        }
    }

    @Override
    protected void initEvents() {
    }

    public void setData(List<QuotationBean> data) {
        mData.clear();
        mData.addAll(data);
        quotationAdapter.setNewData(mData);
        quotationAdapter.notifyDataSetChanged();
    }
}
