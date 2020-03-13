package io.tick.base.appupdate;

import com.google.gson.JsonSyntaxException;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Response;

import java.io.File;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import io.tick.base.BaseApp;
import io.tick.base.R;

public abstract class MFileCallback extends FileCallback {

    public MFileCallback(String path, String fileName) {
        super(path, fileName);
    }

    protected String validateError(Response<File> response) {
        if (response.getException() instanceof SocketTimeoutException) {
            return BaseApp.getContext().getResources().getString(R.string.b_net_socket_time_out);
        } else if (response.getException() instanceof SocketException) {
            return BaseApp.getContext().getResources().getString(R.string.b_net_socket_exception);
        } else if (response.getException() instanceof JsonSyntaxException) {
            return BaseApp.getContext().getResources().getString(R.string.b_net_error_parse);
        }
        int code = response.code();
        if (code == 404) {
            return BaseApp.getContext().getResources().getString(R.string.b_net_url_error);
        }
        if (code >= 500) {
            return BaseApp.getContext().getResources().getString(R.string.b_net_server_error);
        } else if (code >= 400) {
            return BaseApp.getContext().getResources().getString(R.string.b_net_api_error);
        } else {
            return BaseApp.getContext().getResources().getString(R.string.b_net_unknown_error,
                    code);
        }
    }
}
