package io.alf.exchange.widget;

import static io.tick.base.util.DensityUtil.dp2px;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import io.alf.exchange.R;

/**
 * 功能描述： 交易view
 * 创建时间： 2018/4/19 11:29
 * 编写人： gj
 * 类名：MyView1
 * 包名：com.example.administrator.myview
 */

public class TradeShowDepthLineView extends AppCompatTextView {

    private final int p5;
    private final int p2;
    private final int p1;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    {
        int color = getResources().getColor(R.color.textThird);
        paint.setColor(color);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        p5 = dp2px(5);
        p2 = dp2px(2);
        p1 = dp2px(1);
        paint.setStrokeWidth(p1);
        setPadding(p5, p1, p5, 0);
    }

    public TradeShowDepthLineView(Context context) {
        this(context, null);
    }

    public TradeShowDepthLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TradeShowDepthLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int left = getLeft();
        int right = getRight();
        int width = right - left;
        float[] pts = {0, p5, p2, p2,
                p2, p2, width - p2, p2,
                width - p2, p2, width, p5};
        canvas.drawLines(pts, paint);
    }
}
