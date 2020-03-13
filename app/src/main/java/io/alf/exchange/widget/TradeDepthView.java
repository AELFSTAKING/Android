package io.alf.exchange.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import io.alf.exchange.R;


public class TradeDepthView extends LinearLayout {

    private TradeShowDepthLineView line_View;
    private TextView tv_depth_Left, tv_depth_right;
    private boolean isShowTv;

    public TradeDepthView(Context context) {
        this(context, null);
    }

    public TradeDepthView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TradeDepthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IsShowTv);
        if (typedArray != null) {
            isShowTv = typedArray.getBoolean(R.styleable.IsShowTv_isShow, false);
            typedArray.recycle();
        }
        LayoutInflater.from(context).inflate(R.layout.trade_depth_line, this);
        line_View = (TradeShowDepthLineView) findViewById(R.id.line_View);
        tv_depth_Left = (TextView) findViewById(R.id.tv_depth_Left);
        tv_depth_right = (TextView) findViewById(R.id.tv_depth_right);
        if (isShowTv) {
            tv_depth_Left.setVisibility(VISIBLE);
            tv_depth_right.setVisibility(VISIBLE);
        }
    }

    /**
     * 三个参数设置深度
     *
     * @param depth
     */
    public void setTvDepth(String depth, String left, String right) {
        if (!TextUtils.isEmpty(depth)) {
            line_View.setText(depth);
            tv_depth_Left.setText(left);
            tv_depth_right.setText(right);
            line_View.invalidate();
        }
    }

    /**
     * 设置深度
     *
     * @param depth
     */
    public void setTvDepth(String depth) {
        if (!TextUtils.isEmpty(depth)) {
            line_View.setText(depth);
            line_View.invalidate();
        }
    }

}
