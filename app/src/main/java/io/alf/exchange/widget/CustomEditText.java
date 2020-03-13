package io.alf.exchange.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

import io.alf.exchange.R;

public class CustomEditText extends AppCompatEditText implements View.OnFocusChangeListener, TextWatcher {

    private Context context;
    /**
     * 删除按钮的引用
     */
    private Drawable mClearDrawable;
    /**
     * 控件是否有焦点
     */
    private boolean hasFoucs;

    public CustomEditText(Context context) {
        this(context, null);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        // 获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
            // throw new
            // NullPointerException("You can add drawableRight attribute in XML");
            mClearDrawable = getResources().getDrawable(R.mipmap.icon_delete);
        }
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth() / 2, mClearDrawable.getIntrinsicHeight() / 2);
        setClearIconVisible(false);
        setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    protected void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFoucs = hasFocus;
        if (hasFocus)
            setClearIconVisible(getText().length() > 0);
        else
            setClearIconVisible(false);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
