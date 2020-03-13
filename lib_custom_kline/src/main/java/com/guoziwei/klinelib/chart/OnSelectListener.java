package com.guoziwei.klinelib.chart;

import com.guoziwei.klinelib.model.HisData;

import java.util.List;


/**
 * Created by hi-man on 2018/4/12.
 */

public interface OnSelectListener {


    /**
     * select date
     */
    void onSelect(List<HisData> mData, int index);


    void unSelect();


}
