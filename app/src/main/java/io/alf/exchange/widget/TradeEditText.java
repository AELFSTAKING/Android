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
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.math.BigDecimal;
import java.math.BigInteger;

import io.alf.exchange.R;
import io.tick.base.util.BigDecimalUtil;
import io.tick.base.util.NetUtils;

/**
 * 功能描述： 自定义买卖输入框
 * 创建时间： 2018/3/23 12:04
 * 编写人： gj
 * 类名：TradeEditText
 * 包名：com.example.administrator.viewpager.widget
 */

public class TradeEditText extends LinearLayout implements TextWatcher {


    private static final int SUB = 0;
    private static final int ADD = 1;
    public EditText etInput;
    private ImageView tvSub, tvAdd;
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
    private ClickEditListener clickEditListener;
    private String discountStr;

    public TradeEditText(Context context) {
        this(context, null);
    }

    public TradeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TradeEditText);
        if (typedArray != null) {
            isMoney = typedArray.getBoolean(R.styleable.TradeEditText_isMoney, true);
            inputHint = typedArray.getString(R.styleable.TradeEditText_setHintText);
            isShowAddSub = typedArray.getBoolean(R.styleable.TradeEditText_isShowAddSub, true);
            typedArray.recycle();
        }
        LayoutInflater.from(context).inflate(R.layout.view_trade_edittext, this, true);
        initView();
        setVisibleForSubAndAdd(isShowAddSub);
    }

    private void initView() {
        tvSub = (ImageView) findViewById(R.id.tv_sub);
        tvAdd = (ImageView) findViewById(R.id.tv_add);
        etInput = (EditText) findViewById(R.id.et_input);
        //FontUtil.setDinMediumFont(etInput);
        etInput.setHint(TextUtils.isEmpty(inputHint) ? "" : inputHint);
        buffer = new StringBuffer();
        setListener();
    }

    public void setEditable(boolean editable) {
        setEnabled(editable);
        //etInput.clearFocus();
        tvSub.setOnClickListener(editable ? (OnClickListener) v -> {
            if (clickEditListener != null) {
                clickEditListener.clickEditListeners();
                getIndex(SUB);
            }
        } : null);

        tvAdd.setOnClickListener(editable ? (OnClickListener) v -> {
            if (clickEditListener != null) {
                clickEditListener.clickEditListeners();
                getIndex(ADD);
            }
        } : null);
    }

    public void initTextMoney() {
        if (!TextUtils.isEmpty(this.addAndSubUnit)) {
            setTextMoney(this.addAndSubUnit);
        }
    }

    public void setTouchable(boolean touchable) {
        this.touchable = touchable;
    }

    public void setAddAndSubUnit(String addAndSubUnit) {
        this.addAndSubUnit = addAndSubUnit;
        this.defNum = addAndSubUnit;
        this.defMoney = addAndSubUnit;
        this.noNetdefNum = "0";
        this.noNetdefMoney = "0";
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
     * 方法 isFlag  加减控件是否消失
     * <p>
     * 用途104
     */
    public void setVisibleForSubAndAdd(boolean visible) {
        tvSub.setVisibility(visible ? View.VISIBLE : View.GONE);
        tvAdd.setVisibility(visible ? View.VISIBLE : View.GONE);
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
        tvSub.setOnClickListener(v -> {
            if (clickEditListener != null) {
                clickEditListener.clickEditListeners();
                getIndex(SUB);
            }
        });
        tvAdd.setOnClickListener(v -> {
            if (clickEditListener != null) {
                clickEditListener.clickEditListeners();
                getIndex(ADD);
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

    //给edit赋值
    public void setTextMoney(String money) {
        etInput.setText(TextUtils.isEmpty(money) ? "" : money);
        etInput.setSelection(etInput.length());
    }

    /**
     * 1是加  0是减
     */
    private String getIndex(int type) {
        String sInput = etInput.getText().toString().trim();
        String inputMoney = "";
        if (NetUtils.isNetworkConnected()) {
            if (TextUtils.isEmpty(sInput)) {//如果没有数值的时候
                if (type == 0) {// 减法 从默认开始
                    if (isMoney) {
                        inputMoney = noNetdefMoney;
                    } else {
                        inputMoney = noNetdefNum;
                    }
                } else {//加  从默认开始
                    if (isMoney) {
                        inputMoney = defMoney;
                    } else {
                        inputMoney = defNum;
                    }
                }
            } else {
                if (type == 0) {//减法
                    if (TextUtils.equals(sInput, BigDecimalUtil.noNetdefNum) || TextUtils.equals(
                            sInput, BigDecimalUtil.noNetdefMoney)) {
                        return sInput;
                    }
                }
                inputMoney = hasNetAndData(inputMoney, sInput, type);
            }
        } else {
            //当不满足条件的时候 或者没有网络 没有数据的时候走这里
            if (isMoney) {
                inputMoney = noNetdefMoney;
            } else {
                inputMoney = noNetdefNum;
            }
        }
        setT(inputMoney);
        return inputMoney;
    }

    /***
     * @param inputMoney
     * @param sInput
     * @param type
     * @return
     */
    //有网络和数据的时候走
    private String hasNetAndData(String inputMoney, String sInput, int type) {
        if (!TextUtils.isEmpty(addAndSubUnit)) {
            //如果数量小于addAndSubUnit 就等于addAndSubUnit  否则正常加减
            BigDecimal current = new BigDecimal(sInput);
            if (current.compareTo(new BigDecimal(addAndSubUnit)) < 0) {
                inputMoney = addAndSubUnit;
            } else {
                if (type == 0) {
                    BigDecimal[] result = current.divideAndRemainder(new BigDecimal(addAndSubUnit));
                    if (result[1].compareTo(BigDecimal.ZERO) != 0) {
                        current = result[0].multiply(new BigDecimal(addAndSubUnit));
                    } else {
                        current = result[0].subtract(BigDecimal.ONE).multiply(
                                new BigDecimal(addAndSubUnit));
                    }
                } else {
                    BigDecimal[] result = current.divideAndRemainder(new BigDecimal(addAndSubUnit));
                    current = result[0].add(BigDecimal.ONE).multiply(new BigDecimal(addAndSubUnit));
                }
                if (current.compareTo(new BigDecimal(addAndSubUnit)) < 0) {
                    inputMoney = addAndSubUnit;
                } else {
                    inputMoney = current.toString();
                }
            }
        } else {
            int length = sInput.length();
            int index = sInput.indexOf(".");
            //代小数点的
            if (index != -1) {
                int count = length - index;
                StringBuffer buffer = new StringBuffer();
                buffer.append("0.");
                for (int i = 0; i < count - 2; i++) {
                    buffer.append(String.valueOf(0));
                }
                buffer.append(String.valueOf(1));
                String price = buffer.toString();
                if (type == 0) {
                    inputMoney = BigDecimalUtil.sub(sInput, price, isMoney);
                } else {
                    inputMoney = BigDecimalUtil.add(sInput, price);
                }
            } else {
                //TODO 考虑能否合并两种 case (即 BigDecimal 和 BigInteger)
                //如果数量小于0 就等于0  否则正常加减
                BigInteger current = new BigInteger(sInput);
                if (current.compareTo(BigInteger.ZERO) <= 0) {
                    inputMoney = isMoney ? noNetdefMoney : noNetdefNum;
                } else {
                    if (type == 0) {
                        current = current.subtract(BigInteger.ONE);
                    } else {
                        current = current.add(BigInteger.ONE);
                    }
                    inputMoney = current.toString();
                }
            }
        }
        changeListener(inputMoney);
        return inputMoney;
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

    /**
     * 光标定位最后面
     */
    public void setSelection() {
        etInput.setSelection(etInput.length());
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
        if (!TextUtils.isEmpty(s)) {
            getEtInputString(s);
        }
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

    public void setClickEditListener(ClickEditListener clickEditListener) {
        this.clickEditListener = clickEditListener;
    }

    /**
     * 在未登录的情况下 如果是美元的话就是2位 不是美元就是8位
     */
    public void setNoLoginNumMoney(int num, int monry) {
        setDefualNumMoney(num, monry);
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


    public interface ClickEditListener {


        void clickEditListeners();

        /**
         * 输入框变化的方法
         */
        void clickEditTextChangeListener(View v, String num);

    }
}
