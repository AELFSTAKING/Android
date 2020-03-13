package com.guoziwei.klinelib.chart;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.highlight.Highlight;

/**
 * Created by dell on 2017/10/27.
 */

public class ChartInfoViewHandler implements View.OnTouchListener {

    private final GestureDetector mDetector;
    private BarLineChartBase mChart;
    private boolean mIsLongPress = false;

    private Chart[] mOtherChart;

    private OnSelectListener mOnSelectListener;

    public ChartInfoViewHandler(BarLineChartBase chart, OnSelectListener onSelectListener, Chart... otherChart) {
        mOnSelectListener = onSelectListener;
        mChart = chart;
        mOtherChart = otherChart;
        mDetector = new GestureDetector(mChart.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                mIsLongPress = true;
                Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());
                if (h != null) {
                    mChart.highlightValue(h, true);
                    mChart.disableScroll();
                }
            }
        });
    }

    public ChartInfoViewHandler(BarLineChartBase chart, OnSelectListener onSelectListener) {
        mOnSelectListener = onSelectListener;
        mChart = chart;
        mDetector = new GestureDetector(mChart.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                mIsLongPress = true;
                Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());
                if (h != null) {
                    mChart.highlightValue(h, true);
                    mChart.disableScroll();
                }
            }

        });
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            mIsLongPress = false;
            if (mOnSelectListener != null) mOnSelectListener.unSelect();
            mChart.highlightValue(null);
            if (mOtherChart != null) {
                for (int i = 0; i < mOtherChart.length; i++) {
                    mOtherChart[i].highlightValues(null);
                }
            }
        }
        if (mIsLongPress && event.getAction() == MotionEvent.ACTION_MOVE) {

            if (event.getX() > mChart.getWidth()) {
                mChart.highlightValue(null);
                if (mOtherChart != null) {
                    for (int i = 0; i < mOtherChart.length; i++) {
                        mOtherChart[i].highlightValues(null);
                    }
                }
                if (mOnSelectListener != null) mOnSelectListener.unSelect();
                return true;
            }

            Highlight h = mChart.getHighlightByTouchPoint(event.getX(), event.getY());
            if (h != null) {
                mChart.highlightValue(h, true);
                mChart.disableScroll();
            }
            return true;
        }
        return false;
    }
}
