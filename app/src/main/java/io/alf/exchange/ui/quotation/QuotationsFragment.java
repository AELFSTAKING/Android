package io.alf.exchange.ui.quotation;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

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
import io.tick.base.mvp.MvpFragment;
import io.tick.base.util.RxBindingUtils;

public class QuotationsFragment extends MvpFragment implements QuotationsView {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;
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
    private boolean openSearch = false;

    @Override
    protected void initPresenter() {
        mQuotationsPresenter = registerPresenter(new QuotationsPresenter(), this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_quotations;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        tvTitle.setText("行情");
        if (openSearch) {
            ivRight.setImageResource(R.mipmap.ic_search);
        }
        initFragments();
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
        if (openSearch) {
            RxBindingUtils.clicks(
                    v -> SearchQuotationActivity.startUp(getContext(), false), ivRight);
        }
    }

    private void addFavoriteFragment(String group) {
        mTitles.add(group);
        QuotationsFavoriteFragment fragment = findFavoriteFragment(group);
        if (fragment == null) {
            fragment = QuotationsFavoriteFragment.newInstance(group);
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
            childFragment = QuotationAllFragment.newInstance(group);
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
            } else if (event.code == EventCode.JUMP_TO_ALL) {
                tabLayout.setCurrentTab(1);
            }
        }
    }
}
