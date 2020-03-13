package io.alf.exchange.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.consenlabs.tokencore.wallet.KeystoreStorage;
import org.consenlabs.tokencore.wallet.WalletManager;

import java.io.File;

import butterknife.BindView;
import io.alf.exchange.MqttTopicManager;
import io.alf.exchange.R;
import io.alf.exchange.bean.TradeEvent;
import io.alf.exchange.mvp.bean.MqttConfigBean;
import io.alf.exchange.mvp.presenter.QueryMqttConfigPresenter;
import io.alf.exchange.mvp.view.QueryMqttConfigView;
import io.alf.exchange.sample.SampleObserver;
import io.alf.exchange.ui.asset.AssetsFragment;
import io.alf.exchange.ui.quotation.QuotationsFragment;
import io.alf.exchange.util.CexDataPersistenceUtils;
import io.tick.base.eventbus.EventBusCenter;
import io.tick.base.eventbus.EventCode;
import io.tick.base.mvp.MvpActivity;
import io.tick.base.util.RxBindingUtils;

public class MainActivity extends MvpActivity implements KeystoreStorage, QueryMqttConfigView {

    @BindView(R.id.content)
    FrameLayout content;
    @BindView(R.id.ll_home)
    LinearLayout llHome;
    @BindView(R.id.ll_quotation)
    LinearLayout llQuotation;
    @BindView(R.id.ll_trade)
    LinearLayout llTrade;
    @BindView(R.id.ll_assets)
    LinearLayout llAssets;
    @BindView(R.id.ll_mine)
    LinearLayout llMine;

    private Fragment[] fragments = new Fragment[5];
    private static final int HOME = 0;
    private static final int QUOTATION = 1;
    private static final int TRADE = 2;
    private static final int ASSETS = 3;
    private static final int MINE = 4;

    private QueryMqttConfigPresenter mQueryMqttConfigPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        requestPermission();
        WalletManager.storage = this;
        WalletManager.scanWallets();
        openFragment(HOME);
    }

    @Override
    protected void initEvents() {
        RxBindingUtils.clicks(v -> openFragment(HOME), llHome);
        RxBindingUtils.clicks(v -> openFragment(QUOTATION), llQuotation);
        RxBindingUtils.clicks(v -> openFragment(TRADE), llTrade);
        RxBindingUtils.clicks(v -> openFragment(ASSETS), llAssets);
        RxBindingUtils.clicks(v -> openFragment(MINE), llMine);
    }

    protected void requestPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            RxPermissions rxPermissions = new RxPermissions(this);
            rxPermissions.request(Manifest.permission.READ_PHONE_STATE)
                    .subscribe(new SampleObserver<Boolean>() {
                        @Override
                        public void onNext(Boolean value) {
                        }
                    });
        }
    }

    private void openFragment(int position) {
        openFragment(position, new Bundle());
    }

    private void openFragment(int position, @Nullable Bundle args) {
        switch (position) {
            case HOME:
                selectedFragment(HOME, args);
                tabSelected(llHome);
                break;
            case QUOTATION:
                selectedFragment(QUOTATION, args);
                tabSelected(llQuotation);
                break;
            case TRADE:
                selectedFragment(TRADE, args);
                tabSelected(llTrade);
                break;
            case ASSETS:
                selectedFragment(ASSETS, args);
                tabSelected(llAssets);
                break;
            case MINE:
                selectedFragment(MINE, args);
                tabSelected(llMine);
                break;
            default:
                break;
        }
    }

    private void selectedFragment(int position, @Nullable Bundle args) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideFragment(transaction);
        switch (position) {
            case HOME:
                if (fragments[HOME] == null) {
                    fragments[HOME] = new HomeFragment();
                    transaction.add(R.id.content, fragments[HOME]);
                } else {
                    transaction.show(fragments[HOME]);
                }
                break;
            case QUOTATION:
                if (fragments[QUOTATION] == null) {
                    fragments[QUOTATION] = new QuotationsFragment();
                    transaction.add(R.id.content, fragments[QUOTATION]);
                } else {
                    transaction.show(fragments[QUOTATION]);
                }
                break;
            case TRADE:
                if (fragments[TRADE] == null) {
                    fragments[TRADE] = new TradeFragment();
                    fragments[TRADE].setArguments(args != null ? args : new Bundle());
                    transaction.add(R.id.content, fragments[TRADE]);
                } else {
                    if (fragments[TRADE].getArguments() != null) {
                        fragments[TRADE].getArguments().putAll(args);
                    }
                    transaction.show(fragments[TRADE]);
                }
                break;
            case ASSETS:
                if (fragments[ASSETS] == null) {
                    fragments[ASSETS] = new AssetsFragment();
                    transaction.add(R.id.content, fragments[ASSETS]);
                } else {
                    transaction.show(fragments[ASSETS]);
                }
                break;
            case MINE:
                if (fragments[MINE] == null) {
                    fragments[MINE] = new MineFragment();
                    transaction.add(R.id.content, fragments[MINE]);
                } else {
                    transaction.show(fragments[MINE]);
                }
                break;
            default:
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    private void hideFragment(FragmentTransaction transaction) {
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null) {
                    transaction.hide(fragment);
                }
            }
        }
    }

    private void tabSelected(LinearLayout linearLayout) {
        llHome.setSelected(false);
        llQuotation.setSelected(false);
        llTrade.setSelected(false);
        llAssets.setSelected(false);
        llMine.setSelected(false);
        linearLayout.setSelected(true);
    }

    @Override
    public File getKeystoreDir() {
        return this.getFilesDir();
    }

    @Override
    protected void onEventCallback(EventBusCenter event) {
        super.onEventCallback(event);
        if (event.code == EventCode.TRADE) {
            TradeEvent tradeEvent = (TradeEvent) event.data;
            openFragment(TRADE, TradeFragment.buildBundle(tradeEvent.action, tradeEvent.symbol));
        } else if (event.code == EventCode.CREATE_IMPORT_ACCOUNT) {
            openFragment(ASSETS);
        }
    }

    @Override
    protected void initPresenter() {
        mQueryMqttConfigPresenter = registerPresenter(new QueryMqttConfigPresenter(), this);
    }

    @Override
    protected void initData() {
        mQueryMqttConfigPresenter.queryMqttConfig();
    }

    @Override
    public void onQueryMqttConfig(MqttConfigBean bean) {
        if (bean != null) {
            MqttConfigBean oldConfig = CexDataPersistenceUtils.getMqttConfig();
            // 如果Mqtt配置有变更，存储Mqtt配置并重新连接Mqtt。
            if (!bean.equals(oldConfig)) {
                CexDataPersistenceUtils.putMqttConfig(bean);
                MqttTopicManager.connectMqtt(bean);
            }
            // 如果没有连接Mqtt，则连接Mqtt(比如：mqtt配置没有变更但App重启)。
            if (!MqttTopicManager.isConnected()) {
                MqttTopicManager.connectMqtt(bean);
            }
        }
    }
}
