package io.alf.exchange.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import io.alf.exchange.R;
import io.tick.base.util.DensityUtil;

public class EmptyViewUtil {

    public static View setEmpty(RecyclerView recyclerView, Context context, String empty) {
        return setEmpty(recyclerView, context, empty, 0);
    }

    public static View setEmpty(RecyclerView recyclerView, Context context, String empty,
            int paddingTop) {
        View emptyView = LayoutInflater.from(context).inflate(R.layout.layout_empty_view,
                (ViewGroup) recyclerView, false);
        LinearLayout ll_starus = (LinearLayout) emptyView.findViewById(R.id.ll_starus);
        if (paddingTop != 0) {
            ll_starus.setPadding(0, DensityUtil.dp2px(paddingTop), 0, 0);
        }
        ImageView iv = (ImageView) emptyView.findViewById(R.id.iv_pic);
        TextView tv = (TextView) emptyView.findViewById(R.id.tv_content);
        tv.setText(TextUtils.isEmpty(empty) ? "" : empty);
        return emptyView;
    }
}
