package io.alf.exchange.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.alf.exchange.Constant;
import io.alf.exchange.R;
import io.tick.base.util.AntiShake;


public class KLineTimeTypePopWindow extends PopupWindow {
    public static String TAG_CHART_M = "tag_chart_m";
    public static String TAG_CHART_C = "tag_chart_c";
    // 用于防止重复点击
    protected AntiShake onClickUtil = new AntiShake();
    @BindView(R.id.cb_time)
    CheckBox cbTime;
    @BindView(R.id.cb_1min)
    CheckBox cb1Min;
    @BindView(R.id.cb_5min)
    CheckBox cb5Min;
    @BindView(R.id.cb_15min)
    CheckBox cb15Min;
    @BindView(R.id.cb_30min)
    CheckBox cb30Min;
    @BindView(R.id.cb_1hour)
    CheckBox cb1Hour;
    @BindView(R.id.cb_2hour)
    CheckBox cb2Hour;
    @BindView(R.id.cb_4hour)
    CheckBox cb4Hour;
    @BindView(R.id.cb_6hour)
    CheckBox cb6Hour;
    @BindView(R.id.cb_12hour)
    CheckBox cb12Hour;
    @BindView(R.id.cb_1day)
    CheckBox cb1Day;
    @BindView(R.id.cb_1week)
    CheckBox cb1Week;
    private Context context;
    private List<CheckBox> mCheckBoxList;
    private OnKLineTypeClickListener mOnKLineTypePopClickListener;
    private String selectTimeTxt;

    public KLineTimeTypePopWindow(Context context) {
        super(context);
        this.context = context;
        this.initPopupWindow();
    }

    private void initPopupWindow() {
        //使用view来引入布局
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.popupwindow_kline_type, null);
        ButterKnife.bind(this, contentView);

        cbTime.setTag(0);
        cb1Min.setTag(1);
        cb5Min.setTag(2);
        cb15Min.setTag(3);
        cb30Min.setTag(4);
        cb1Hour.setTag(5);
        cb2Hour.setTag(6);
        cb4Hour.setTag(7);
        cb6Hour.setTag(8);
        cb12Hour.setTag(9);
        cb1Day.setTag(10);
        cb1Week.setTag(11);

        mCheckBoxList = new ArrayList<>();
        mCheckBoxList.add(cbTime);
        mCheckBoxList.add(cb1Min);
        mCheckBoxList.add(cb5Min);
        mCheckBoxList.add(cb15Min);
        mCheckBoxList.add(cb30Min);
        mCheckBoxList.add(cb1Hour);
        mCheckBoxList.add(cb2Hour);
        mCheckBoxList.add(cb4Hour);
        mCheckBoxList.add(cb6Hour);
        mCheckBoxList.add(cb12Hour);
        mCheckBoxList.add(cb1Day);
        mCheckBoxList.add(cb1Week);

        // 设置SelectPicPopupWindow的View
        this.setContentView(contentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果，设置动画，一会会讲解
        // this.setAnimationStyle(R.style.pop_animation_preview);
    }

    @OnClick({
            R.id.cb_time,
            R.id.cb_1min,
            R.id.cb_5min,
            R.id.cb_15min,
            R.id.cb_30min,
            R.id.cb_1hour,
            R.id.cb_2hour,
            R.id.cb_4hour,
            R.id.cb_6hour,
            R.id.cb_12hour,
            R.id.cb_1day,
            R.id.cb_1week,
    })
    protected void onClick(View view) {
        if (onClickUtil.check()) return;
        switch (view.getId()) {
            case R.id.cb_time:
            case R.id.cb_1min:
            case R.id.cb_5min:
            case R.id.cb_15min:
            case R.id.cb_30min:
            case R.id.cb_1hour:
            case R.id.cb_2hour:
            case R.id.cb_4hour:
            case R.id.cb_6hour:
            case R.id.cb_12hour:
            case R.id.cb_1day:
            case R.id.cb_1week: {
                selectTimeTxt = ((CheckBox) view).getText().toString().trim();
                processCbCheckResult((Integer) view.getTag());
                break;
            }
        }
    }

    /**
     * @param checkBoxTag 选择位置
     */
    private void processCbCheckResult(int checkBoxTag) {
        this.dismiss();
        changeCbBackground(checkBoxTag);
        setNewKLineTime(checkBoxTag);
    }

    private void setNewKLineTime(int checkBoxTag) {
        String selTime = "";
        String tag = "";
        switch (checkBoxTag) {
            case 0:
                selTime = Constant.MIN_1;
                tag = TAG_CHART_M;
                break;
            case 1:
                selTime = Constant.MIN_1;
                tag = TAG_CHART_C;
                break;
            case 2:
                selTime = Constant.MIN_5;
                break;
            case 3:
                selTime = Constant.MIN_15;
                break;
            case 4:
                selTime = Constant.MIN_30;
                break;
            case 5:
                selTime = Constant.HOUR_1;
                break;
            case 6:
                selTime = Constant.HOUR_2;
                break;
            case 7:
                selTime = Constant.HOUR_4;
                break;
            case 8:
                selTime = Constant.HOUR_6;
                break;
            case 9:
                selTime = Constant.HOUR_12;
                break;
            case 10:
                selTime = Constant.DAY_1;
                break;
            case 11:
                selTime = Constant.WEEK_1;
                break;
        }
        if (mOnKLineTypePopClickListener != null) {
            mOnKLineTypePopClickListener.onKLineTypeClick(selTime, selectTimeTxt, tag);
        }
    }

    private void changeCbBackground(int checkBoxTag) {
        int size = mCheckBoxList.size();
        for (int i = 0; i < size; i++) {
            if (checkBoxTag == i) {
                mCheckBoxList.get(i).setChecked(true);
            } else {
                mCheckBoxList.get(i).setChecked(false);
            }
        }
    }

    public void setOnKLineTypeClickListener(OnKLineTypeClickListener lineTypePopListener) {
        mOnKLineTypePopClickListener = lineTypePopListener;
    }

    public void setSelected(String item, boolean isMinuteHour) {
        int position = 0;
        if (isMinuteHour) {
            position = 0;
        } else {
            if (TextUtils.equals(item, Constant.MIN_1)) {
                position = 1;
            } else if (TextUtils.equals(item, Constant.MIN_5)) {
                position = 2;
            } else if (TextUtils.equals(item, Constant.MIN_15)) {
                position = 3;
            } else if (TextUtils.equals(item, Constant.MIN_30)) {
                position = 4;
            } else if (TextUtils.equals(item, Constant.HOUR_1)) {
                position = 5;
            } else if (TextUtils.equals(item, Constant.HOUR_2)) {
                position = 6;
            } else if (TextUtils.equals(item, Constant.HOUR_4)) {
                position = 7;
            } else if (TextUtils.equals(item, Constant.HOUR_6)) {
                position = 8;
            } else if (TextUtils.equals(item, Constant.HOUR_12)) {
                position = 9;
            } else if (TextUtils.equals(item, Constant.DAY_1)) {
                position = 10;
            } else if (TextUtils.equals(item, Constant.WEEK_1)) {
                position = 11;
            }
        }
        changeCbBackground(position);
    }

    public interface OnKLineTypeClickListener {
        void onKLineTypeClick(String selectTime, String selectTimeTxt, String tag);
    }
}
