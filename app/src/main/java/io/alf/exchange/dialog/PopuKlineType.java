package io.alf.exchange.dialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.guoziwei.klinelib.model.SelectData;

import java.util.ArrayList;
import java.util.List;

import io.alf.exchange.App;
import io.alf.exchange.Constant;
import io.alf.exchange.R;
import io.alf.exchange.widget.RecyclerViewDivider;

/**
 * 功能描述： K线时间类型 popuwindow
 * 创建时间： 2018/4/16 15:04
 * 编写人： gj
 * 类名：TradePopuWindow
 */

public class PopuKlineType {

    private final View localView;
    private final MyAdapter adapter;
    View myShowView;
    private RecyclerView recyclerView;
    private PopupWindow window;
    //窗口在x轴偏移量
    private int xOff = 0;
    //窗口在y轴的偏移量
    private int yOff = 0;
    private View animaView;
    private List<SelectData> datas;
    private TextView textView;
    private boolean isFlag;
    private boolean isHasAnim;

    private String hourValue = Constant.HOUR_1;
    private KlineTimeTypePopListener listener;


    /**
     * @param context   上下文
     * @param datas     数据
     * @param textView  显示的控件
     * @param animaView 动画的控件
     * @param isHasAnim true是显示动画 否则不显示动画
     */
    public PopuKlineType(Context context, List<SelectData> datas, TextView textView, View animaView, boolean isFlag, boolean isHasAnim) {
        this.isFlag = isFlag;
        this.isHasAnim = isHasAnim;
        if (isFlag) {
            localView = LayoutInflater.from(context).inflate(R.layout.lv_pw_menu_bottom, null);
        } else {
            localView = LayoutInflater.from(context).inflate(R.layout.lv_pw_menu, null);
        }
        recyclerView = (RecyclerView) localView.findViewById(R.id.recyclerView);


        recyclerView.addItemDecoration(new RecyclerViewDivider(context,
                DividerItemDecoration.HORIZONTAL,
                1,
                ContextCompat.getColor(context, R.color.linePrimary)));


        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new MyAdapter(context, datas);
        recyclerView.setAdapter(adapter);
        this.textView = textView;
        this.animaView = animaView;
        this.datas = datas;
        if (isHasAnim) {
            if (isFlag) {
                animaView.animate().rotation(180);
            } else {
                animaView.animate().rotation(-180);
            }
        }
        window = new PopupWindow(context);
        window.setFocusable(true);
        //点击 back 键的时候，窗口会自动消失
        window.setBackgroundDrawable(new BitmapDrawable());
        //设置显示的视图
        window.setContentView(localView);

        window.setOnDismissListener(() -> dismiss());

        adapter.setOnItemClickListener((pos, select) -> {

            if (listener != null) {
                listener.ontKlineTimeTypeClick(select.getSelectValue());
                textView.setText(TextUtils.isEmpty(select.getSelectName()) ? "" : select.getSelectName());
                dismiss();
            }
        });
    }

    public void dismiss() {
        window.dismiss();
        if (isHasAnim) {
            animaView.animate().rotation(0);
        }
    }

    /**
     * @param xOff x轴（左右）偏移
     * @param yOff y轴（上下）偏移
     */
    public void setOff(int xOff, int yOff) {
        this.xOff = xOff;
        this.yOff = yOff;
    }

    /**
     * @param paramView 点击的按钮
     */
    public void show(View paramView, int count) {
        //该count 是手动调整窗口的宽度
        int i = paramView.getWidth() * count;
        int i1 = i - dp2px(13);
        window.setWidth(i1);
        //设置窗口显示位置, 后面两个0 是表示偏移量，可以自由设置
        window.showAsDropDown(paramView, xOff, yOff);
        //更新窗口状态
        window.update();
    }

    /**
     * 位于上面方法
     */
    public void showAtLocationTop(View view) {
        this.myShowView = view;
        localView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = localView.getMeasuredWidth();
        int popupHeight = localView.getMeasuredHeight();

        int width = view.getWidth();

        // width= (int) (width*scale);


        window.setWidth(260);
        window.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);


        int[] location = new int[2];
        view.getLocationOnScreen(location);
        window.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1] - popupHeight - dp2px(1));//需要减去

        window.update();


    }

    public void setKlineTimePopListener(KlineTimeTypePopListener listener) {
        this.listener = listener;
    }

    /**
     * dp转px
     **/
    private int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, App.getInstance().getApplicationContext().getResources().getDisplayMetrics());
    }


    /**
     * 选中的popuwindow方法
     */
    public interface KlineTimeTypePopListener {
        void ontKlineTimeTypeClick(String selTime);
    }

    /**
     * adapter里面的点击监听
     */
    public interface onItemClickListener {
        void onItemClickListener(int pos, SelectData select);
    }

    //为什么会出现 recyclerView填充不满 是因为 宽高都是错误的 需要在Adapter里面去重新设置
    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private Context context;
        private List<SelectData> mDatas;
        private onItemClickListener onItemClickListener;

        public MyAdapter(Context context, List<SelectData> datas) {
            this.context = context;
            if (datas == null) {
                datas = new ArrayList<>();
            }
            mDatas = datas;
        }

        public void setOnItemClickListener(onItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(context).inflate(R.layout.item_kline_pw_menu, null);
            return new MyViewHolder(inflate);
        }


        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            SelectData item = mDatas.get(position);


            holder.textView.setText(TextUtils.isEmpty(item.getSelectName()) ? "null" : item.getSelectName());
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.textView.getLayoutParams();
            layoutParams.height = dp2px(30);
            layoutParams.width = dp2px(180);

            layoutParams.gravity = Gravity.CENTER;

            holder.textView.setLayoutParams(layoutParams);

            holder.textView.setOnClickListener(view ->
            {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClickListener(position, item);
                }
            });
        }


        @Override
        public int getItemCount() {
            return mDatas == null ? 0 : mDatas.size();
        }

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_item_pw_menu);
        }
    }
}
