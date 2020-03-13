package com.guoziwei.klinelib.chart;

/**
 * Created by Administrator on 2016/2/1.
 */

import android.content.Context;
import android.widget.TextView;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.guoziwei.klinelib.R;
import com.guoziwei.klinelib.model.HisData;
import com.guoziwei.klinelib.util.DoubleUtil;

import java.util.List;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class LineChartYMarkerView extends MarkerView {

    private final int digits;
    private List<HisData> mList;
    private TextView tvContent;
    private int type = 0;//0:normal 1:深度图

    public LineChartYMarkerView(Context context, List<HisData> list, int digits) {
        super(context, R.layout.view_mp_real_price_marker);
        this.digits = digits;
        this.mList = list;
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    public LineChartYMarkerView(Context context, List<HisData> list, int digits, int type) {
        super(context, R.layout.view_mp_real_price_marker);
        this.digits = digits;
        this.mList = list;
        this.type = type;
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        int value = (int) e.getX();
        if (mList != null && value < mList.size()) {
            if (type == 0) tvContent.setText(DoubleUtil.getStringByDigits(mList.get(value).getClose(), digits));
            else if (type == 1)
                tvContent.setText(DoubleUtil.getStringByDigits(mList.get(value).getVolDeepQuantity(), digits));

        }

        super.refreshContent(e, highlight);
    }

}
