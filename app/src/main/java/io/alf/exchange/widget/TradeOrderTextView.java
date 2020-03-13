package io.alf.exchange.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.ColorRes;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import java.math.BigDecimal;

import io.alf.exchange.R;
import io.tick.base.util.BigDecimalUtil;
import io.tick.base.util.DensityUtil;

/**
 * 功能描述：交易里面的额计算工具类
 */

public class TradeOrderTextView extends AppCompatTextView {

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    RectF bounds = new RectF();

    private Layout layout;
    private double div;
    private int isToRight = 0;//做判断用如果没有他会出现ui上面的bug

    private int totalPadding;//总的padding
    private int backPadding;//背影的padding
    private int endPadding;//剩余的padding

    public TradeOrderTextView(Context context) {
        this(context, null);
    }

    public TradeOrderTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TradeOrderTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TradeViewPadding);
        if (typedArray != null) {
            int totalP = DensityUtil.dp2px(5);
            int backP = DensityUtil.dp2px(4);
            totalPadding = typedArray.getDimensionPixelOffset(R.styleable.TradeViewPadding_totalPadding, totalP);
            backPadding = typedArray.getDimensionPixelOffset(R.styleable.TradeViewPadding_backGroundPadding, backP);
            endPadding = totalP - backP;
            typedArray.recycle();
        }
        setPadding(0, totalPadding, 0, totalPadding);
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//
//        layout = getLayout();
//        if (isToRight==1){//左边向右开始
//            bounds.left = (float) (layout.getLineLeft(0)-endpadding);//控制这里
//            bounds.right = (float) (layout.getLineLeft(0)+div+endpadding);
//        }else if (isToRight==2){    //右边向左开始
//            bounds.left = (float) (layout.getLineRight(0)-div-endpadding);//控制这里
//            bounds.right = (float) (layout.getLineRight(0)+ endpadding);
//        }else { //没有边距
//            bounds.left = (float) (layout.getLineRight(0));//控制这里
//            bounds.right = (float) (layout.getLineRight(0));
//        }
//        bounds.top = layout.getLineTop(0)+endpadding;
//        bounds.bottom = layout.getLineBottom(0)+backPadding+backPadding;
//        canvas.drawRect(bounds, paint);
//        super.onDraw(canvas);
//    }


    @Override
    protected void onDraw(Canvas canvas) {

        layout = getLayout();
        if (isToRight == 1) {//左边向右开始
            bounds.left = (float) (layout.getLineLeft(0));//控制这里
            bounds.right = (float) (layout.getLineLeft(0) + div);
        } else if (isToRight == 2) {    //右边向左开始
            bounds.left = (float) (layout.getLineRight(0) - div);//控制这里
            bounds.right = (float) (layout.getLineRight(0));
        } else { //没有边距
            bounds.left = (float) (layout.getLineRight(0));//控制这里
            bounds.right = (float) (layout.getLineRight(0));
        }
        bounds.top = layout.getLineTop(0) + endPadding;
        bounds.bottom = layout.getLineBottom(0) + backPadding + backPadding;
        canvas.drawRect(bounds, paint);
        super.onDraw(canvas);
    }

    //左边向右开始
    public void setDataToRight(String curNum, String totalNum, int ScreenWid, @ColorRes int color) {
        if (paint != null) {
            paint.setColor(ContextCompat.getColor(getContext(), color));
        }
//        double widPercent = curNum * ScreenWid;
//        div = div(widPercent, totalNum, 1);
        if (TextUtils.isEmpty(curNum)) {
            curNum = String.valueOf(0);
        }
        if (TextUtils.isEmpty(totalNum)) {
            totalNum = String.valueOf(1);
        }
        String mul = BigDecimalUtil.mul(curNum, String.valueOf(ScreenWid));
        div = div(mul, totalNum, 1);
        isToRight = 1;
    }

    //右边向左开始
    public void setData(String curNum, String totalNum, int ScreenWid, @ColorRes int color) {
        if (paint != null && color != -1) {
            paint.setColor(ContextCompat.getColor(getContext(), color));
        }
        if (TextUtils.isEmpty(curNum)) {
            curNum = String.valueOf(0);
        }
        if (TextUtils.isEmpty(totalNum)) {
            totalNum = String.valueOf(1);
        }
        String mul = BigDecimalUtil.mul(curNum, String.valueOf(ScreenWid));
        div = div(mul, totalNum, 1);
//        double widPercent = curNum * ScreenWid;
//        div = div(widPercent, totalNum, 1);
        isToRight = 2;
    }

    /**
     * 设置为透明
     */
    public void setTransparent() {
        if (paint != null) {
            paint.setColor(Color.TRANSPARENT);
        }
    }

    /**
     * 除法
     *
     * @param value1
     * @param value2
     * @return
     */
    public double div(String value1, String value2, int scale) {
        //如果精确范围小于0，抛出异常信息
        if (scale < 0) {
            try {
                throw new IllegalAccessException("精确度不能小于0");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (TextUtils.isEmpty(value1)) {
            value1 = String.valueOf(0);
        }
        if (TextUtils.isEmpty(value2)) {
            return 0;
        } else {
            double v = Double.parseDouble(value2);
            if (v == 0) {
                return 0;
            }
        }
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        return b1.divide(b2, scale).doubleValue();
    }

    /**
     * 从新设置
     *
     * @param total
     * @param back
     */
    public void setTotalAndBackPadding(int total, int back) {
        totalPadding = DensityUtil.dp2px(total);
        backPadding = DensityUtil.dp2px(back);
        endPadding = totalPadding - backPadding;
        setPadding(0, totalPadding, 0, totalPadding);
    }
}
