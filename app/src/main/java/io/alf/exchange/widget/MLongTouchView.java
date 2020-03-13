package io.alf.exchange.widget;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import io.tick.base.util.NetUtils;

/**
 * 功能描述： 长按view
 * 创建时间： 2018/4/13 16:10
 * 编写人： gj
 * 类名：MyLongTouchView
 */

public class MLongTouchView extends AppCompatImageView {

    private boolean isClick;
    private onLongClickListener listener;
    private int time = 100;

    public MLongTouchView(Context context) {
        this(context, null);
    }

    public MLongTouchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = getY();
        float x = getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isClick = true;
                new LongTouchTask().execute();
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isClick = false;
                break;
        }
        return super.onTouchEvent(event);
    }


    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setOnLongClickLitener(onLongClickListener listener, int time) {
        this.listener = listener;
        this.time = time;
    }


    public interface onLongClickListener {
        void onLongTouch();
    }

    class LongTouchTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (NetUtils.isNetworkConnected()) {
                while (isClick) {
                    sleep(time);
                    publishProgress(0);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (listener != null) {
                listener.onLongTouch();
            }
        }
    }
}
