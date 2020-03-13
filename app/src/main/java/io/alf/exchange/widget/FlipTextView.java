package io.alf.exchange.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;

import java.util.ArrayList;
import java.util.List;

import io.alf.exchange.R;
import io.alf.exchange.bean.Announcement;


public class FlipTextView extends TextSwitcher implements TextSwitcher.ViewFactory {

    private static final int AUTO_RUN_FLIP_TEXT = 11;
    private static final int WAIT_TIME = 5000;
    private List<Announcement> demoBeans = new ArrayList<>();
    private int mIndex;
    private ItemDataListener itemDataListener;
    private View view; //click view
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AUTO_RUN_FLIP_TEXT:
                    if (demoBeans.size() > 0) {
                        mHandler.sendEmptyMessageDelayed(AUTO_RUN_FLIP_TEXT, WAIT_TIME);
                        Announcement announcement = demoBeans.get(mIndex);
                        StringBuilder stringBuilder = new StringBuilder(announcement.title);
//                        StringBuilder stringBuilder = new StringBuilder( string.getTitle() +": "+string.getSummary());
                        setText(stringBuilder);
                    }
                    mIndex++;
                    if (mIndex > demoBeans.size() - 1) {
                        mIndex = 0;
                    }
                    view.setOnClickListener(view -> {
                        if (mIndex == 0) {
                            itemDataListener.onItemClick(demoBeans.size() - 1);
                        } else {
                            itemDataListener.onItemClick(mIndex - 1);
                        }
                    });
                    break;
            }
        }
    };

    public FlipTextView(Context context) {
        super(context);
        init();
    }

    public FlipTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setFactory(this);
        setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.trans_bottom_to_top_in_fast));
        setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.trans_bottom_to_top_out_fast));
    }

    @Override
    public View makeView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.view_flip_item_text_view, null);
    }

    public void setData(List<Announcement> datas, ItemDataListener listener, View v) {
        view = v;
        itemDataListener = listener;
        if (demoBeans.size() > 0) {
            demoBeans.clear();
        }
        demoBeans.addAll(datas);
        mIndex = 0;
        mHandler.removeMessages(AUTO_RUN_FLIP_TEXT);
        mHandler.sendEmptyMessage(AUTO_RUN_FLIP_TEXT);
    }


    public abstract interface ItemDataListener {
        public void onItemClick(int position);
    }
}
