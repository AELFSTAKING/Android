package io.alf.exchange.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.alf.exchange.R;
import io.tick.base.util.BigDecimalUtil;

/**
 * 功能描述： 自定义买卖输入框
 * 创建时间： 2018/3/23 12:04
 * 编写人： gj
 * 类名：TradeEditText
 * 包名：com.example.administrator.viewpager.widget
 */

public class TradeEditText3 extends LinearLayout implements TextWatcher {


    private static final int SUB = 0;
    private static final int ADD = 1;
    public TextView tvTitle;
    public EditText etInput;
    public TextView tvCurrency;
    private boolean isMoney, isShowAddSub;
    private String inputHint;
    private String showCurNum;
    private String noNetdefNum;
    private String defNum;
    private String noNetdefMoney;
    private String defMoney;
    private StringBuffer buffer;
    private boolean touchable = true;
    private String addAndSubUnit;
    private TradeEditText.ClickEditListener clickEditListener;
    private String discountStr;

    public TradeEditText3(Context context) {
        this(context, null);
    }

    public TradeEditText3(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TradeEditText);
        if (typedArray != null) {
            isMoney = typedArray.getBoolean(R.styleable.TradeEditText_isMoney, true);
            inputHint = typedArray.getString(R.styleable.TradeEditText_setHintText);
            isShowAddSub = typedArray.getBoolean(R.styleable.TradeEditText_isShowAddSub, true);
            typedArray.recycle();
        }
        LayoutInflater.from(context).inflate(R.layout.view_trade_edittext_3, this, true);
        initView();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        etInput = findViewById(R.id.et_input);
        tvCurrency = findViewById(R.id.tv_currency);
        //FontUtil.setDinMediumFont(etInput);
        //FontUtil.setDinMediumFont(tvCurrency);
        buffer = new StringBuffer();
        setListener();
    }

    public void setEditable(boolean editable) {
        setEnabled(editable);
    }

    public void setTouchable(boolean touchable) {
        this.touchable = touchable;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!touchable) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 是钱还是数量
     */
    public void isMoney(boolean isMoney) {
        this.isMoney = isMoney;
    }

    /**
     * et设置可点击
     */
    public void setEnabled(boolean isFlag) {
        etInput.setEnabled(isFlag);
    }

    /**
     * hint
     */
    public void setTextHint(String hint) {
        etInput.setHint(TextUtils.isEmpty(hint) ? "" : hint);
    }

    public void setCurrency(String currency) {
        tvCurrency.setText(currency);
    }

    public void setValue(String title, String value, String currency) {
        tvTitle.setText(title);
        if (TextUtils.isEmpty(etInput.getText())) {
            etInput.setText(value);
        }
        tvCurrency.setText(currency);
    }

    public void setHintValue(String title, String value, String currency) {
        tvTitle.setText(title);
        etInput.setHint(value);
        tvCurrency.setText(currency);
    }

    public void addTextChangedListener() {
        etInput.addTextChangedListener(this);
    }

    public void removeTextChangedListener() {
        etInput.removeTextChangedListener(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListener() {

        addTextChangedListener();

        etInput.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                etInput.setSelected(b);
            }
        });
    }

    /**
     * 截取输入框得”.“
     */
    private void getEtInputString(String s) {
        int count = 0;
        String buf = "";
        if (s.contains(".")) {
            buffer.delete(0, buffer.length());
            int i = s.indexOf(".");
            String first = s.substring(0, i);
            String last = s.substring(i + 1, s.length());
            if (TextUtils.isEmpty(first)) {
                first = "0";
                String end = "";
                if (isMoney) {
                    int len = defMoney.length() - 2;
                    if (last.length() >= len) {
                        end = last.substring(0, len);
                    } else {
                        end = last;
                    }
                } else {
                    int len = defNum.length() - 2;
                    if (last.length() >= len) {
                        end = last.substring(0, len);
                    } else {
                        end = last;
                    }
                }
                buffer.append(first);
                buffer.append(".");
                buffer.append(end);
                setTextMoney(buffer.toString());
                buf = buffer.toString();
            } else {
                String end = "";
                if (isMoney) {
                    int len = defMoney.length() - 2;
                    if (last.length() >= len) {
                        end = last.substring(0, len);
                    } else {
                        if (last.contains(".")) {
                            end = last.replace(".", "");
                        } else {
                            end = last;
                        }

                    }
                } else {
                    int len = defNum.length() - 2;
                    if (last.length() >= len) {
                        end = last.substring(0, len);
                    } else {
                        if (last.contains(".")) {
                            end = last.replace(".", "");
                        } else {
                            end = last;
                        }
                    }
                }
                buffer.append(first);
                buffer.append(".");
                buffer.append(end);
                etInput.removeTextChangedListener(this);
                setTextMoney(buffer.toString());
                etInput.addTextChangedListener(this);
                buf = buffer.toString();
            }
        } else {
            buf = s;
        }
        char[] chars = buf.toCharArray();
        for (int i3 = 0; i3 < buf.length(); i3++) {
            char aChar = chars[i3];
            if (String.valueOf(aChar).contains(".")) {
                count++;
                if (count > 1) {
                    boolean b = buf.endsWith(".");
                    if (b) {
                        String substring = buf.substring(0, i3);
                        etInput.setText(substring);
                        etInput.setSelection(substring.length());
                    }
                    break;
                } else if (count == 1) {
                    boolean b = buf.startsWith(".");
                    if (b) {
                        buf = "0" + buf;
                        etInput.setText(buf);
                        etInput.setSelection(buf.length());
                    }
                }
            }
        }
        changeListener(buf);
    }

    /**
     * 点击加减或者编辑editText的时候的监听
     */
    private void changeListener(String s) {
        if (clickEditListener != null) {
            clickEditListener.clickEditTextChangeListener(this, s);
        }
    }

    public void setTextMoney(String money) {
        setTextMoney(money, false);
    }

    //给edit赋值
    public void setTextMoney(String money, boolean startPos) {
        etInput.setText(TextUtils.isEmpty(money) ? "" : money);
        if (startPos) {
            etInput.setSelection(0);
        } else {
            etInput.setSelection(etInput.length());
        }
    }

    /**
     * 设置内容
     * <p>
     * 用途187
     */
    public void setT(String inputMoney) {
        if (!TextUtils.isEmpty(inputMoney)) {
            showCurNum = inputMoney;
            etInput.setText(inputMoney);
            etInput.setSelection(etInput.length());
        }
    }

    /**
     * 获取内容
     */
    public String getText() {
        return etInput.getText().toString().trim();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        discountStr = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        String s = charSequence.toString();
        //截取.
        if (s.contains(".")) {
            int size = countString(s, ".");
            if (size > 1) {
                etInput.setText(discountStr);
                String trim1 = etInput.getText().toString().trim();
                etInput.setSelection(trim1.length());
                return;
            }
        }
        getEtInputString(s);
    }

    private int countString(String str, String s) {
        int count = 0;
        for (int i = 0; i <= str.length(); i++) {
            if (str.indexOf(s) == i) {
                str = str.substring(i + 1, str.length());
                count++;
            }
        }
        return count;
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public void setClickEditListener(TradeEditText.ClickEditListener clickEditListener) {
        this.clickEditListener = clickEditListener;
    }

    /**
     * 设置默认的小位数
     *
     * @param num   数量
     * @param money 价格
     */
    public void setDefualNumMoney(int num, int money) {
        //代小数点的
        StringBuffer buffer = new StringBuffer();
        if (num > 0) {
            buffer.append("0.");
            for (int i = 0; i < num - 1; i++) {
                buffer.append(String.valueOf(0));
            }
            buffer.append(String.valueOf(0));
            noNetdefNum = buffer.toString();
            buffer.delete(buffer.length() - 1, buffer.length());
            buffer.append(String.valueOf(1));
            defNum = buffer.toString();
            BigDecimalUtil.noNetdefNum = noNetdefNum;
            BigDecimalUtil.defNum = defNum;
        } else {
            noNetdefNum = BigDecimalUtil.noNetdefNum;
            defNum = BigDecimalUtil.defNum;
        }
        if (money > 0) {
            buffer.delete(0, buffer.length());
            buffer.append("0.");
            for (int i = 0; i < money - 1; i++) {
                buffer.append(String.valueOf(0));
            }
            buffer.append(String.valueOf(0));
            noNetdefMoney = buffer.toString();
            buffer.delete(buffer.length() - 1, buffer.length());
            buffer.append(String.valueOf(1));
            defMoney = buffer.toString();
            BigDecimalUtil.noNetdefMoney = noNetdefMoney;
            BigDecimalUtil.defMoney = defMoney;
        } else {
            noNetdefMoney = BigDecimalUtil.noNetdefMoney;
            defMoney = BigDecimalUtil.defMoney;
        }
    }
}
