package io.alf.exchange.ui.quotation;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
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
import io.alf.exchange.util.EmptyViewUtil;
import io.cex.mqtt.bean.QuotationBean;
import io.tick.base.eventbus.EventBusCenter;
import io.tick.base.eventbus.EventCode;
import io.tick.base.mvp.MvpFragment;


public class QuotationsFavoriteFragment extends MvpFragment implements FavoritesView {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ll_quotation_title)
    LinearLayout llQuotationTitle;
    @BindView(R.id.ll_change)
    LinearLayout llChange;
    @BindView(R.id.ll_favorite)
    LinearLayout llFavorite;

    private List<QuotationBean> mfavData = new ArrayList<>();
    private List<QuotationBean> mAllData = new ArrayList<>();
    private FavoriteAdapter mFavoriteAdapter;
    private static final String GROUP = "group";
    private static final String SYMBOL_SELECTOR = "symbolSelector";
    private String mGroup;
    private boolean symbolSelector;

    private FavoritesPresenter favoritesPresenter;

    public static QuotationsFavoriteFragment newInstance(String group) {
        return newInstance(group, false);
    }

    public static QuotationsFavoriteFragment newInstance(String group, boolean symbolSelector) {
        QuotationsFavoriteFragment fragment = new QuotationsFavoriteFragment();
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
        initFavoriteAdapter();
        updateUIData();
        llChange.setVisibility(isSymbolSelector() ? View.GONE : View.VISIBLE);
        llFavorite.setVisibility(isSymbolSelector() ? View.VISIBLE : View.GONE);
    }

    private void dismissParentDialogFragment() {
        Fragment parent = getParentFragment();
        if (parent instanceof QuotationsDialogFragment) {
            ((QuotationsDialogFragment) parent).dismiss();
        }
    }

    private void initFavoriteAdapter() {
        mFavoriteAdapter = new FavoriteAdapter();
        mFavoriteAdapter.setShowFavorite(isSymbolSelector());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mFavoriteAdapter);
        mFavoriteAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mfavData != null && mfavData.size() > position) {
                    QuotationBean item = mfavData.get(position);
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
        mFavoriteAdapter.setOnItemChildClickListener(
                new BaseQuickAdapter.OnItemChildClickListener() {
                    @Override
                    public void onItemChildClick(BaseQuickAdapter adapter, View view,
                            int position) {
                        if (view.getId() == R.id.iv_favorite) {
                            if (mFavoriteAdapter.getFavorites() != null
                                    && mFavoriteAdapter.getFavorites().getSymbolList() != null
                                    && mFavoriteAdapter.getFavorites().getSymbolList().size() > 0) {
                                String symbol = mFavoriteAdapter.getFavorites().getSymbolList().get(
                                        position);
                                favoritesPresenter.deleteFavorite(symbol);
                            }
                        }
                    }
                });
    }

    @Override
    protected void initEvents() {

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
            mFavoriteAdapter.setFavorites(data);
        }
        updateUIData();
    }

    private List<QuotationBean> filterFavoritesQuotations(List<QuotationBean> quotationList) {
        List<QuotationBean> favQuotations = new ArrayList<>();
        if (mFavoriteAdapter.getFavorites() != null && quotationList != null
                && quotationList.size() > 0
                && mFavoriteAdapter.getFavorites().getSymbolList().size() > 0) {
            for (String symbol : mFavoriteAdapter.getFavorites().getSymbolList()) {
                for (QuotationBean bean : quotationList) {
                    if (TextUtils.equals(symbol, bean.symbol)) {
                        favQuotations.add(bean);
                    }
                }
            }
        }
        return favQuotations;
    }

    private void updateUIData() {
        if (mAllData != null && mAllData.size() > 0) {
            // 摘出出自选
            List<QuotationBean> favQuotations = filterFavoritesQuotations(
                    mAllData);
            if (favQuotations != null) {
                mfavData.clear();
                mfavData.addAll(favQuotations);
            }

            if (mfavData != null && mfavData.size() > 0) {
                llQuotationTitle.setVisibility(View.VISIBLE);
                mFavoriteAdapter.setNewData(mfavData);
                mFavoriteAdapter.notifyDataSetChanged();
            } else {
                llQuotationTitle.setVisibility(View.GONE);
                if (!isSymbolSelector()) {
                    View emptyView = LayoutInflater.from(activity).inflate(
                            R.layout.layout_empty_favorites,
                            null);
                    mFavoriteAdapter.setNewData(null);
                    mFavoriteAdapter.setEmptyView(emptyView);
/*                    emptyView.findViewById(R.id.ll_add_favorite).setOnClickListener(
                            v -> SearchQuotationActivity.startUp(getContext(), true));*/
                    emptyView.findViewById(R.id.ll_add_favorite).setOnClickListener(
                            v -> EventBusCenter.post(EventCode.JUMP_TO_ALL));
                } else {
                    mFavoriteAdapter.setNewData(null);
                    View emptyView = EmptyViewUtil.setEmpty(recyclerView, getContext(), "没有数据", 5);
                    mFavoriteAdapter.setEmptyView(emptyView);
                }
            }
        }
    }

    public void setData(List<QuotationBean> data) {
        mAllData.clear();
        mAllData.addAll(data);
        updateUIData();
    }
}
