package io.alf.exchange.ui.quotation;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.KeyboardUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import io.alf.exchange.App;
import io.alf.exchange.R;
import io.alf.exchange.bean.DBSearchEntity;
import io.alf.exchange.bean.HistoryDBEntity;
import io.alf.exchange.bean.HistoryDBEntity_;
import io.alf.exchange.mvp.bean.Favorites;
import io.alf.exchange.mvp.presenter.FavoritesPresenter;
import io.alf.exchange.mvp.presenter.QuotationsPresenter;
import io.alf.exchange.mvp.view.FavoritesView;
import io.alf.exchange.mvp.view.QuotationsView;
import io.alf.exchange.sample.SampleTextWatcher;
import io.alf.exchange.ui.adapter.SearchShowAdapter;
import io.alf.exchange.ui.adapter.SearchTagAdapter;
import io.alf.exchange.util.ClickUtil;
import io.alf.exchange.util.EmptyViewUtil;
import io.alf.exchange.util.ToUpText;
import io.alf.exchange.widget.CustomEditText;
import io.alf.exchange.widget.flowlayout.TagFlowLayout;
import io.cex.mqtt.bean.QuotationBean;
import io.objectbox.Box;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;
import io.tick.base.eventbus.EventBusCenter;
import io.tick.base.eventbus.EventCode;
import io.tick.base.mvp.MvpActivity;
import io.tick.base.util.ActivityStartUtils;

public class SearchQuotationActivity extends MvpActivity implements FavoritesView, QuotationsView {

    @BindView(R.id.et_input)
    CustomEditText etInput;
    @BindView(R.id.cs_history)
    ConstraintLayout cs_history;
    @BindView(R.id.tv_cancel)
    TextView tv_cancel;
    @BindView(R.id.iv_history_delete)
    ImageView iv_history_delete;
    @BindView(R.id.tab_layout)
    TagFlowLayout tab_layout;
    @BindView(R.id.ll_search)
    LinearLayout ll_search;
    @BindView(R.id.recyclerView_search)
    RecyclerView recyclerView_search;

    private Box<DBSearchEntity> aBox;
    private Box<HistoryDBEntity> historyBox;
    private SearchShowAdapter showAdapter;
    private SearchTagAdapter tagAdapter;
    private QuotationsPresenter quotationsPresenter;
    private FavoritesPresenter favoritesPresenter;
    private static final String ADD_FAVORITE = "addFavorite";


    public static void startUp(Context context, boolean addFavorite) {
        ActivityStartUtils.jump(context, SearchQuotationActivity.class,
                intent -> intent.putExtra(ADD_FAVORITE, addFavorite));
    }

    private boolean isAddFavorite() {
        boolean addFavorite = false;
        if (getIntent() != null) {
            addFavorite = getIntent().getBooleanExtra(ADD_FAVORITE, false);
        }
        return addFavorite;
    }

    @Override
    protected void initPresenter() {
        favoritesPresenter = registerPresenter(new FavoritesPresenter(), this);
        quotationsPresenter = registerPresenter(new QuotationsPresenter(), this);
    }

    @Override
    protected void initData() {
        favoritesPresenter.queryFavorites();
        quotationsPresenter.querySymbolQuotation();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_quotation;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setEditRequest();
        historyBox = App.getBoxStore().boxFor(HistoryDBEntity.class);
        aBox = App.getBoxStore().boxFor(DBSearchEntity.class);
        initHistory();
        showAdapter = new SearchShowAdapter();
        recyclerView_search.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_search.setAdapter(showAdapter);
    }

    public void setEditRequest() {
        etInput.setFocusable(true);
        etInput.setFocusableInTouchMode(true);
        etInput.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    /**
     * 初始化历史记录
     */
    private void initHistory() {
        QueryBuilder<HistoryDBEntity> query = historyBox.query();
        List<HistoryDBEntity> historyList = query.build().find();
        if (historyList.size() > 10) {
            tab_layout.setVisibility(View.VISIBLE);
            List<HistoryDBEntity> historyDBEntities = historyList.subList(0, 10);
            tagAdapter = new SearchTagAdapter(historyDBEntities);
            tab_layout.setAdapter(tagAdapter);
        } else if (historyList.size() > 0) {
            tab_layout.setVisibility(View.VISIBLE);
            tagAdapter = new SearchTagAdapter(historyList);
            tab_layout.setAdapter(tagAdapter);
        } else {
            tab_layout.setVisibility(View.GONE);
        }

        tab_layout.setOnTagClickListener((view, position, parent) -> {
            if (ClickUtil.isNotQuickDoubleClick()) {
                HistoryDBEntity entity = historyList.get(position);
                nextActivity(entity);
            }
            return false;
        });
    }

    /***
     * 跳入下一个Activity
     * @param entity
     */
    private void nextActivity(HistoryDBEntity entity) {
        //TradeActivity.startUp(this, entity.getSymbol(), true);
    }


    @Override
    protected void initEvents() {
        showAdapter.setListener(entity -> {
            if (ClickUtil.isNotQuickDoubleClick()) {
                String symbol = entity.getSymbol();
                String[] trade = historyBox.query().build().property(
                        HistoryDBEntity_.symbol).findStrings();
                List<String> addList = Arrays.asList(trade);
                if (!addList.contains(symbol)) {
                    addHistoryDB(entity);
                }
                //TradeActivity.startUp(this, entity.getSymbol(), true);
            }
        });
        etInput.addTextChangedListener(new SampleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() > 0) {
                    ll_search.setVisibility(View.VISIBLE);
                    cs_history.setVisibility(View.GONE);
                    queryDB(s);
                } else {
                    cs_history.setVisibility(View.VISIBLE);
                    ll_search.setVisibility(View.GONE);
                    initHistory();
                }
            }
        });
        etInput.setTransformationMethod(new ToUpText());
        tv_cancel.setOnClickListener(view -> finish());
        iv_history_delete.setOnClickListener(view -> {
            //清除历史记录
            historyBox.removeAll();
            initHistory();
        });
        showAdapter.setListener(entity -> {
            if (ClickUtil.isNotQuickDoubleClick()) {
                String symbol = entity.getSymbol();
                String[] trade = historyBox.query().build().property(
                        HistoryDBEntity_.symbol).findStrings();
                List<String> addList = Arrays.asList(trade);
                if (!addList.contains(symbol)) {
                    addHistoryDB(entity);
                }
                DetailQuotationPortraitActivity.startUp(this, entity.getSymbol());
                finish();
            }
        });
        showAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.iv_favorite) {
                DBSearchEntity entity = showAdapter.getData().get(position);
                if (showAdapter.getFavorites() != null
                        && showAdapter.getFavorites().getSymbolList() != null
                        && showAdapter.getFavorites().getSymbolList().contains(
                        entity.getSymbol())) {
                    showAdapter.getFavorites().getSymbolList().remove(entity.getSymbol());
                    favoritesPresenter.deleteFavorite(entity.getSymbol());
                } else {
                    if (showAdapter.getFavorites() != null) {
                        showAdapter.getFavorites().getSymbolList().add(entity.getSymbol());
                    }
                    favoritesPresenter.addFavorite(entity.getSymbol());
                }
                showAdapter.notifyDataSetChanged();
                if (isAddFavorite()) {
                    onBackPressed();
                }
            }
        });
    }

    /**
     * 添加到数据库的集合
     */
    private void addHistoryDB(DBSearchEntity entity) {
        HistoryDBEntity historyDBEntity = new HistoryDBEntity();
        historyDBEntity.setApplies(entity.getApplies());
        historyDBEntity.setDirection(entity.getDirection());
        historyDBEntity.setMax(entity.getMax());
        historyDBEntity.setMin(entity.getMin());
        historyDBEntity.setPrice(entity.getPrice());
        historyDBEntity.setQuantity(entity.getQuantity());
        historyDBEntity.setSymbol(entity.getSymbol());
        historyDBEntity.setUsdPrice(entity.getUsdPrice());
        historyDBEntity.setWavePercent(entity.getWavePercent());
        historyDBEntity.setWavePrice(entity.getWavePrice());
        historyBox.put(historyDBEntity);
    }

    /**
     * 查询数据库显示在recyclerView上面
     */
    private void queryDB(CharSequence s) {
        Query<DBSearchEntity> build = aBox.query().build();
        List<DBSearchEntity> dbList = build.find();
        List<DBSearchEntity> mDBList = new ArrayList<>();
        for (DBSearchEntity entity : dbList) {
            String symbol = entity.getSymbol();
            String s1 = String.valueOf(s).toLowerCase();
            String s2 = String.valueOf(s).toUpperCase();
            if (symbol.contains(s1) || symbol.contains(s2)) {
                mDBList.add(entity);
            }
        }

        if (mDBList.size() > 0) {
            showAdapter.setNewData(mDBList);
        } else {
            showAdapter.setNewData(null);
            View emptyView = EmptyViewUtil.setEmpty(recyclerView_search, this, "暂无结果", 30);
            showAdapter.setEmptyView(emptyView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        favoritesPresenter.queryFavorites();
        quotationsPresenter.querySymbolQuotation();
    }

    @Override
    public void onAddFavorite(String symbol, boolean success) {
        if (success) {
            EventBusCenter.post(EventCode.UPDATE_FAVORITES, showAdapter.getFavorites());
        } else {
            showAdapter.getFavorites().getSymbolList().add(symbol);
            showAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDeleteFavorite(String symbol, boolean success) {
        if (success) {
            EventBusCenter.post(EventCode.UPDATE_FAVORITES, showAdapter.getFavorites());
        } else {
            showAdapter.getFavorites().getSymbolList().remove(symbol);
            showAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onQueryFavorites(Favorites data) {
        showAdapter.setFavorites(data);
        showAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        KeyboardUtils.hideSoftInput(this);
        super.onStop();
    }


    @Override
    public void onQuerySymbolQuotation(List<QuotationBean> quotationList) {
        aBox.removeAll();
        List<DBSearchEntity> dbList = new ArrayList<>();
        for (QuotationBean bean : quotationList) {
            DBSearchEntity DBEntity = new DBSearchEntity();
            //DBEntity.setApplies(entity.getApplies());
            DBEntity.setDirection(bean.direction);
            DBEntity.setMax(bean.highestPrice);
            DBEntity.setMin(bean.lowestPrice);
            DBEntity.setPrice(bean.lastPrice);
            DBEntity.setQuantity(bean.quantity);
            DBEntity.setSymbol(bean.symbol);
            DBEntity.setUsdPrice(bean.lastUsdPrice);
            DBEntity.setWavePercent(bean.wavePercent);
            DBEntity.setWavePrice(bean.wavePrice);
            dbList.add(DBEntity);
        }
        aBox.put(dbList);
    }

    @Override
    protected void onEventCallback(EventBusCenter event) {
        super.onEventCallback(event);
        if (event.code == EventCode.UPDATE_FAVORITES) {
            if (event.data != null) {
                Favorites favorites = (Favorites) event.data;
                showAdapter.setFavorites(favorites);
                showAdapter.notifyDataSetChanged();
            }
        }
    }
}
