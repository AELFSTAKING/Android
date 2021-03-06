package io.alf.exchange.mvp.presenter;

import com.lzy.okgo.model.Response;

import org.consenlabs.tokencore.wallet.Identity;

import java.util.HashMap;
import java.util.Map;

import io.alf.exchange.util.CexDataPersistenceUtils;
import io.alf.exchange.util.WalletUtils;
import io.tick.base.mvp.IView;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;

public class IdentityPresenter extends BasePresenter<IView> {

    public Identity getIdentity() {
        Identity identity = Identity.getCurrentIdentity();
        if (identity != null) {
            String address = WalletUtils.getAddress();
            if (!CexDataPersistenceUtils.isRegistered(address)) {
                registerAddress(address);
            }
        }
        return identity;
    }

    public void registerAddress(String address) {
        Map<String, Object> data = new HashMap<>();
        data.put("address", address);
        HttpUtils.postRequest(BaseUrl.REGISTER_ADDRESS, getView(), generateRequestHeader(),
                generateRequestBody(data), new BaseCallback<ResponseBean<String>>(
                        BaseUrl.REGISTER_ADDRESS) {
                    @Override
                    public void onSuccess(Response<ResponseBean<String>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null && response.body().isSuccess()) {
                            CexDataPersistenceUtils.setRegistered(address, true);
                        }
                    }
                });
    }
}
