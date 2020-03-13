package io.alf.exchange.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.tabs.TabLayout;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;
import com.zhouwei.mzbanner.holder.MZViewHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import io.alf.exchange.R;
import io.alf.exchange.bean.Announcement;
import io.alf.exchange.mvp.bean.AllArticleList;
import io.alf.exchange.mvp.bean.BannerBean;
import io.alf.exchange.mvp.presenter.QueryAllArticlePresenter;
import io.alf.exchange.mvp.presenter.QuotationsPresenter;
import io.alf.exchange.mvp.view.QueryAllArticleView;
import io.alf.exchange.mvp.view.QuotationsView;
import io.alf.exchange.ui.adapter.QuantitySortAdapter;
import io.alf.exchange.ui.adapter.WaveSortAdapter;
import io.alf.exchange.ui.quotation.DetailQuotationPortraitActivity;
import io.alf.exchange.util.PriceConvertUtil;
import io.alf.exchange.widget.FlipTextView;
import io.cex.mqtt.bean.IncreaseSymbolsBean;
import io.cex.mqtt.bean.QuotationBean;
import io.tick.base.eventbus.EventBusCenter;
import io.tick.base.eventbus.EventCode;
import io.tick.base.mvp.MvpFragment;
import io.tick.base.util.DensityUtil;

public class HomeFragment extends MvpFragment implements QueryAllArticleView, QuotationsView {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.banner)
    MZBannerView bannerView;
    @BindView(R.id.ll_announcement)
    LinearLayout llAnnouncement;
    @BindView(R.id.tv_announcement)
    FlipTextView tvAnnouncement;
    @BindView(R.id.tl_tab)
    TabLayout tabLayout;
    @BindView(R.id.ll_tab_content1)
    LinearLayout llTabContent1;
    @BindView(R.id.wave_recycler_view)
    RecyclerView waveRecyclerView;
    @BindView(R.id.ll_tab_content2)
    LinearLayout llTabContent2;
    @BindView(R.id.quality_recycler_view)
    RecyclerView qualityRecyclerView;

    private List<String> tabs = new ArrayList<>();
    List<QuotationBean> waveSortDataList = new ArrayList<>();
    List<QuotationBean> sortQuantityDataList = new ArrayList<>();
    private WaveSortAdapter mWaveSortAdapter;
    private QuantitySortAdapter sortQuantityAdapter;

    private QueryAllArticlePresenter mQueryAllArticlePresenter;
    private QuotationsPresenter mQuotationsPresenter;

    @Override
    protected void initPresenter() {
        mQueryAllArticlePresenter = registerPresenter(new QueryAllArticlePresenter(), this);
        mQuotationsPresenter = registerPresenter(new QuotationsPresenter(), this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        tvTitle.setText("Staking Planet");
        initTabView();
        initWaveSortView();
        initQualityView();
        testBanner();
    }

    @Override
    public void initEvents() {

    }

    @Override
    public void onQueryAllArticleList(AllArticleList articleList) {
        if (articleList != null) {
            onFillBanner(articleList.getBannerList());
            onFillAnnouncement(articleList);
        }
    }

    @Override
    public void onQuerySymbolQuotation(List<QuotationBean> quotationList) {
        if (quotationList != null) {
            onFillWaveSort(quotationList);
            onFillQuantitySort(quotationList);
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
                    onFillWaveSort(increaseSymbolsBean.upList);
                    onFillQuantitySort(increaseSymbolsBean.upList);
                }
            }
        }
    }

    private void onFillWaveSort(List<QuotationBean> quotationList) {
        Collections.sort(quotationList, (QuotationBean o1, QuotationBean o2) -> new BigDecimal(
                PriceConvertUtil.getWavePrice(o2.direction, o2.wavePercent)).compareTo(
                new BigDecimal(PriceConvertUtil.getWavePrice(o1.direction, o1.wavePercent))));
        waveSortDataList.clear();
        waveSortDataList.addAll(
                quotationList.size() > 5 ? quotationList.subList(0, 5) : quotationList);
        mWaveSortAdapter.setNewData(waveSortDataList);
        mWaveSortAdapter.notifyDataSetChanged();
        LinearLayout.LayoutParams wlp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                DensityUtil.dp2px(68 * waveSortDataList.size()));
        wlp.setMargins(DensityUtil.dp2px(15), 0, DensityUtil.dp2px(15), 0);
        waveRecyclerView.setLayoutParams(wlp);
    }

    private void onFillQuantitySort(List<QuotationBean> quotationList) {
        Collections.sort(quotationList,
                (o1, o2) -> new BigDecimal(o2.quantity).compareTo(new BigDecimal(o1.quantity)));
        sortQuantityDataList.clear();
        sortQuantityDataList.addAll(
                quotationList.size() > 5 ? quotationList.subList(0, 5) : quotationList);
        sortQuantityAdapter.setNewData(sortQuantityDataList);
        sortQuantityAdapter.notifyDataSetChanged();
        LinearLayout.LayoutParams qlp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                DensityUtil.dp2px(68 * sortQuantityDataList.size()));
        qlp.setMargins(DensityUtil.dp2px(15), 0, DensityUtil.dp2px(15), 0);
        qualityRecyclerView.setLayoutParams(qlp);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bannerView != null) {
            bannerView.start();
        }
        //mQueryAllArticlePresenter.queryAllArticleList(false);
        mQuotationsPresenter.querySymbolQuotation();
    }

    @Override
    protected void onVisibleToUser() {
        super.onVisibleToUser();
        //mQueryAllArticlePresenter.queryAllArticleList(false);
        mQuotationsPresenter.querySymbolQuotation();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (bannerView != null) {
            bannerView.pause();
        }
    }

    private void initWaveSortView() {
        mWaveSortAdapter = new WaveSortAdapter();
        waveRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        waveRecyclerView.setAdapter(mWaveSortAdapter);
        waveRecyclerView.setHasFixedSize(true);
        waveRecyclerView.setNestedScrollingEnabled(false);
        mWaveSortAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (waveSortDataList != null && waveSortDataList.size() > position) {
                    QuotationBean item = waveSortDataList.get(position);
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
        });
    }

    private void initQualityView() {
        sortQuantityAdapter = new QuantitySortAdapter();
        qualityRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        qualityRecyclerView.setAdapter(sortQuantityAdapter);
        qualityRecyclerView.setHasFixedSize(true);
        qualityRecyclerView.setNestedScrollingEnabled(false);
        sortQuantityAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (sortQuantityDataList != null && sortQuantityDataList.size() > position) {
                    QuotationBean item = sortQuantityDataList.get(position);
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
        });
    }

    private void initTabView() {
        tabs.add("涨幅榜");
        tabs.add("成交榜");
        TextView tabTitle;
        for (int i = 0; i < tabs.size(); i++) {
            //获取tab
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            //给tab设置自定义布局
            Objects.requireNonNull(tab).setCustomView(R.layout.item_tab_home);
            tabTitle = Objects.requireNonNull(tab.getCustomView()).findViewById(R.id.tv_title);
            //填充数据
            tabTitle.setText(tabs.get(i));
            //默认选择第一项
            if (i == 0) {
                tabTitle.setSelected(true);
                tabTitle.setTextSize(16);
                tabTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tabTitle.setTextColor(getResources().getColor(R.color.textPrimary));
                changeTab();
            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView tabTitle = Objects.requireNonNull(tab.getCustomView()).findViewById(
                        R.id.tv_title);
                tabTitle.setSelected(true);
                tabTitle.setTextSize(16);
                tabTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tabTitle.setTextColor(getResources().getColor(R.color.textPrimary));
                changeTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tabTitle = Objects.requireNonNull(tab.getCustomView()).findViewById(
                        R.id.tv_title);
                tabTitle.setSelected(true);
                tabTitle.setTextSize(13);
                tabTitle.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                tabTitle.setTextColor(getResources().getColor(R.color.textSecondary));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void changeTab() {
        if (tabLayout.getSelectedTabPosition() == 0) {
            llTabContent2.setVisibility(View.GONE);
            llTabContent1.setVisibility(View.VISIBLE);
        } else if (tabLayout.getSelectedTabPosition() == 1) {
            llTabContent1.setVisibility(View.GONE);
            llTabContent2.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 公告显示处理
     */
    private void onFillAnnouncement(AllArticleList articleList) {
        List<Announcement> noticeList;
        if (articleList.getAnnouncementList() != null
                && articleList.getAnnouncementList().size() > 10) {
            noticeList = articleList.getAnnouncementList().subList(0, 10);
        } else if (articleList.getAnnouncementList() != null
                && articleList.getAnnouncementList().size() > 0) {
            noticeList = articleList.getAnnouncementList();
        } else {
            noticeList = Collections.emptyList();
        }
        if (!noticeList.isEmpty()) {
            llAnnouncement.setVisibility(View.VISIBLE);
            tvAnnouncement.setData(noticeList, position -> {
                if (noticeList.size() > position) {
                    Announcement announcement = noticeList.get(position);
                    Activity activity = getActivity();
/*                    if (activity != null) {
                        ActivityStartUtils.jump(activity, JockeyWebActivity.class, intent -> {
                            intent.putExtra(JockeyWebActivity.TITLE, announcement.getTitle());
                            intent.putExtra(JockeyWebActivity.BIZ_ID, announcement.getBizId());
                        });
                    }*/
                }
            }, llAnnouncement);
            llAnnouncement.findViewById(R.id.tv_announcement_more).setOnClickListener((v) -> {
                Activity activity = getActivity();
/*                if (activity != null) {
                    ActivityStartUtils.jump(activity, AnnouncementListActivity.class, intent ->
                            intent.putParcelableArrayListExtra(Constant.INTENT_LIST, new
                            ArrayList<>(noticeList)));
                }*/
            });
        } else {
            tvAnnouncement.setVisibility(View.GONE);
        }
    }

    private void testBanner() {
        List<BannerBean> bannerList = new ArrayList<>();
        bannerList.add(new BannerBean("", "", "", R.drawable.banner_1));
        bannerList.add(new BannerBean("", "", "", R.drawable.banner_2));
        onFillBanner(bannerList);

        List<Announcement> noticeList = new ArrayList<>();
        noticeList.add(new Announcement("", "Staking Planet正式主网上线！", 0));
        llAnnouncement.setVisibility(View.VISIBLE);
        tvAnnouncement.setData(noticeList, position -> {
        }, llAnnouncement);
    }

    /**
     * banner显示处理
     */
    private void onFillBanner(List<BannerBean> bannerList) {
        if (bannerList == null || bannerList.size() == 0) {
            return;
        }
        bannerView.setVisibility(View.VISIBLE);
        bannerView.setBannerPageClickListener((view, position) -> {
            if (bannerList.size() > position) {
/*                BannerBean bannerListBean = bannerList.get(position);
                if (!TextUtils.isEmpty(bannerListBean.getLink())) {
                    ActivityStartUtils.jump(getActivity(), JockeyWebActivity.class, intent -> {
                        intent.putExtra(JockeyWebActivity.TITLE, "");
                        intent.putExtra(JockeyWebActivity.URL, bannerListBean.getLink());
                    });
                    return;
                }*/
            }
        });

        bannerView.setIndicatorVisible(false);
        if (bannerList.size() == 2) {
            bannerList.addAll(bannerList);
        }
        bannerView.setPages(bannerList,
                (MZHolderCreator<BannerViewHolder>) () -> new BannerViewHolder());
        if (bannerList.size() > 2) bannerView.start();
    }

    public static class BannerViewHolder implements MZViewHolder<BannerBean> {

        private ImageView mImageView;

        @Override
        public View createView(Context context) {
            // 返回页面布局文件
            View view = LayoutInflater.from(context).inflate(R.layout.item_banner, null);
            mImageView = view.findViewById(R.id.banner_image);
            return view;
        }

        @Override
        public void onBind(Context context, int i, BannerBean data) {
/*            if (context == null || TextUtils.isEmpty(data.getImageUrl())) return;
            String imageUrl = data.getImageUrl();
            Glide.with(context).load(imageUrl).into(mImageView);*/
            if (context == null || data.localImgResId == 0) return;
            Glide.with(context).load(data.localImgResId).into(mImageView);
        }
    }
}
