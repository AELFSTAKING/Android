package io.alf.exchange.ui.quotation;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.alf.exchange.FragmentPagerAdapter;
import io.alf.exchange.R;
import io.alf.exchange.mvp.presenter.QuotationsPresenter;
import io.alf.exchange.mvp.view.QuotationsView;
import io.cex.mqtt.bean.IncreaseSymbolsBean;
import io.cex.mqtt.bean.QuotationBean;
import io.cex.mqtt.bean.QuotationGroupByAreaBean;
import io.tick.base.eventbus.EventBusCenter;
import io.tick.base.eventbus.EventCode;
import io.tick.base.mvp.BottomDialogFragment;

public class QuotationsDialogFragment extends BottomDialogFragment implements QuotationsView {

    @BindView(R.id.tab_layout)
    SlidingTabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private FragmentPagerAdapter fragmentPagerAdapter;

    private List<String> mTitles = new ArrayList<>();
    private List<Fragment> mFragments = new ArrayList<>();

    private String FAVORITES;
    private String ALL;

    private List<QuotationGroupByAreaBean> mData = new ArrayList<>();
    private QuotationsPresenter mQuotationsPresenter;

    @Override
    protected void initPresenter() {
        mQuotationsPresenter = registerPresenter(new QuotationsPresenter(), this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dialog_quotations;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initFragments();
        //updateUIData();
    }

    private void initFragments() {
        FAVORITES = "自选";
        // 自选
        addFavoriteFragment(FAVORITES);
        ALL = "全部";
        // 全部
        addAllFragment(ALL);

        fragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager(), mTitles,
                mFragments);
        viewpager.setAdapter(fragmentPagerAdapter);
        tabLayout.setViewPager(viewpager);
    }

    @Override
    public void initEvents() {
    }

    private void addFavoriteFragment(String group) {
        mTitles.add(group);
        QuotationsFavoriteFragment fragment = findFavoriteFragment(group);
        if (fragment == null) {
            fragment = QuotationsFavoriteFragment.newInstance(group, true);
        }
        mFragments.add(fragment);
    }

    private QuotationsFavoriteFragment findFavoriteFragment(String group) {
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment instanceof QuotationsFavoriteFragment) {
                QuotationsFavoriteFragment childFragment = (QuotationsFavoriteFragment) fragment;
                if (TextUtils.equals(group, childFragment.getGroup())) {
                    return childFragment;
                }
            }
        }
        return null;
    }

    private void addAllFragment(String group) {
        mTitles.add(group);
        QuotationAllFragment childFragment = findAllFragment(group);
        if (childFragment == null) {
            childFragment = QuotationAllFragment.newInstance(group, true);
        }
        mFragments.add(childFragment);
    }

    private QuotationAllFragment findAllFragment(String group) {
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment instanceof QuotationAllFragment) {
                QuotationAllFragment allFragment = (QuotationAllFragment) fragment;
                if (TextUtils.equals(group, allFragment.getGroup())) {
                    return allFragment;
                }
            }
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mQuotationsPresenter.querySymbolQuotation();
    }

    @Override
    protected void onVisibleToUser() {
        super.onVisibleToUser();
        mQuotationsPresenter.querySymbolQuotation();
    }

    @Override
    public void onQuerySymbolQuotation(List<QuotationBean> quotationList) {
        QuotationAllFragment allFragment = findAllFragment(ALL);
        if (allFragment != null) {
            allFragment.setData(quotationList);
        }
        QuotationsFavoriteFragment favoriteFragment = findFavoriteFragment(FAVORITES);
        if (favoriteFragment != null) {
            favoriteFragment.setData(quotationList);
        }
    }

    @Override
    protected void onEventCallback(EventBusCenter event) {
        super.onEventCallback(event);
        if (isVisibleToUser()) {
            if (event.code == EventCode.MQTT_INCREASE_SYMBOLS) {
                IncreaseSymbolsBean increaseSymbolsBean = (IncreaseSymbolsBean) event.data;
                if (increaseSymbolsBean != null && increaseSymbolsBean.upList != null
                        && increaseSymbolsBean.upList.size() > 0) {
                    onQuerySymbolQuotation(increaseSymbolsBean.upList);
                }
            }
        }
    }

    @Override
    protected int getHeight(int screenHeight) {
        return screenHeight * 2 / 3;
    }
}
