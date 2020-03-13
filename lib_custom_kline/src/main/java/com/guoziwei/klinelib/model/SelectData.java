package com.guoziwei.klinelib.model;

/**
 * Created by hi-man on 2018/4/26.
 */

public class SelectData {
    private String selectName;
    private String selectValue;

    public SelectData(String selectName, String selectValue) {
        this.selectName = selectName;
        this.selectValue = selectValue;
    }

    public String getSelectName() {
        return selectName;
    }

    public void setSelectName(String selectName) {
        this.selectName = selectName;
    }

    public String getSelectValue() {
        return selectValue;
    }

    public void setSelectValue(String selectValue) {
        this.selectValue = selectValue;
    }
}
