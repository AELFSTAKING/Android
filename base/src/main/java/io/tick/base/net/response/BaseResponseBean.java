package io.tick.base.net.response;

import java.io.Serializable;

public class BaseResponseBean implements Serializable {

    private static final long serialVersionUID = -1477609349345966116L;

    public String code;
    public String message;

    public ResponseBean toResponseBean() {
        ResponseBean MResponseBean = new ResponseBean();
        MResponseBean.code = code;
        MResponseBean.message = message;
        return MResponseBean;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message == null ? "" : message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
