package io.alf.exchange.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import io.alf.exchange.R;
import io.tick.base.util.BigDecimalUtil;
import io.tick.base.util.DensityUtil;

/**
 * 功能描述：订单买卖控件
 */

public class OrderTextView extends ConstraintLayout {
    //买
    public static final int BUY = 0;
    //卖
    public static final int SELL = 1;
    private ConstraintLayout cs_parent;
    private TradeOrderTextView ttv_number;
    private TextView tv_price;

    public OrderTextView(Context context) {
        this(context, null);
    }

    public OrderTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OrderTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_trade_order_textview, this);
        cs_parent = (ConstraintLayout) findViewById(R.id.cs_parent);
        tv_price = (TextView) findViewById(R.id.tv_price);
        ttv_number = (TradeOrderTextView) findViewById(R.id.ttv_number);
    }

    /**
     * 设置点击背景
     */
    public void setTouchBackGround() {
        cs_parent.setBackgroundResource(android.R.color.transparent);
    }

    /**
     * 设置item的间距 和字体大小
     *
     * @param padding
     */
    public OrderTextView setItemPadding(int padding, int textSize) {
        int px = DensityUtil.dp2px(padding);
        cs_parent.setPadding(0, px, 0, px);
        ttv_number.setTextSize(textSize);
        ttv_number.setTotalAndBackPadding(0, 0);
        tv_price.setTextSize(textSize);
        return this;
    }

    /**
     * curNum 当前的数量  totlaNum 是总数 #FF542A
     *
     * @param curNum//右边向左开始
     * @param totalNum
     * @param type           0是买  1是卖   其他是透明的颜色
     */
    public OrderTextView setData(String quantity, String curNum, String totalNum, int type, ProgressBar bar) {

        if (type == 0) {
            bar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_area_statistic));
        } else if (type == 1) {
            bar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_area_statistic2));
        } else {
            bar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_area_statistic3));
        }

        if (TextUtils.isEmpty(totalNum)) {
            totalNum = "0";
        }
        if (TextUtils.isEmpty(curNum)) {
            curNum = "0";
        }
        if (Double.parseDouble(totalNum) != 0) {
            try {
                int currentProgress = (int) (Double.parseDouble(
                        BigDecimalUtil.div(curNum, totalNum, 2)) * 100);
                bar.setProgress(currentProgress);
            } catch (Exception e) {
                bar.setProgress(0);
                bar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_area_statistic3));
            }
        } else {
            bar.setProgress(0);
            bar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_area_statistic3));
        }


        if (ttv_number != null) {
            ttv_number.setText(TextUtils.isEmpty(quantity) ? "--" : quantity);
        }
        return this;
    }

    /**
     * 设置数据为空 交易里面用
     *
     * @param type 0 买  1是卖
     */
    public void settvNumberNull(int type, ProgressBar bar) {
        String ganggang = getResources().getString(R.string.price_gang);
        if (type == 0) {
            tv_price.setTextColor(getResources().getColor(R.color.color_increase));
        } else if (type == 1) {
            tv_price.setTextColor(getResources().getColor(R.color.color_decrease));
        } else {
            tv_price.setTextColor(getResources().getColor(R.color.textPrimary));
        }
        ttv_number.setText(ganggang);
        ttv_number.setTransparent();
        bar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_area_statistic3));
        bar.setProgress(0);
    }

    /**
     * 价格
     *
     * @param price 两个控件
     * @return type  0==买   1==卖
     */
    public OrderTextView bindPriceView(String price, int type) {
        if (TextUtils.isEmpty(price) || getResources().getString(R.string.price_gang).equals(price)) {
            tv_price.setTextColor(getResources().getColor(R.color.textPrimary));
        } else {
            if (type == 0) {
                tv_price.setTextColor(getResources().getColor(R.color.color_increase));
            } else {
                tv_price.setTextColor(getResources().getColor(R.color.color_decrease));
            }
        }
        tv_price.setText(TextUtils.isEmpty(price) ? getResources().getString(R.string.price_gang) : price);
        return this;
    }

}