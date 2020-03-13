package io.alf.exchange.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import org.consenlabs.tokencore.wallet.Identity;
import org.consenlabs.tokencore.wallet.validators.PrivateKeyValidator;

import java.util.regex.Pattern;

import io.alf.exchange.dialog.ErrorDialog;
import io.kofo.common.KofoUtil;
import io.kofo.common.model.KofoPair;

public class Validator {
    /**
     * 正则表达式：验证邮箱
     */
    private static final String REGEX_EMAIL =
            "^([a-z0-9A-Z_-]+[-|\\.]?)+[a-z0-9A-Z_-]@([a-z0-9A-Z_-]+(-[a-z0-9A-Z_-]+)?\\.)"
                    + "+[a-zA-Z_-]{2,}$";
    /**
     * 正则表达式：验证密码
     * 至少8个字符，至少1个大写字母，1个小写字母和1个数字,不能包含特殊字符（非数字字母）
     */
    private static final String REGEX_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";

    private static final String REGEX_ID = "^[\uFF08-\uFF09()A-Za-z0-9]{1,18}$";

    private static final String REGEX_CH_ID =
            "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)"
                    + "\\d{3}[0-9Xx]$";

    /**
     * 正则表达式：验证邮箱
     */
    private static final String REGEX_VERIFY_CODE = "^\\d{6}$";

    private static final String REGEX_ANTI_PHISHING = "^[A-Za-z0-9]{6}$";

    private static final String REGEX_PASSWORD_1 = "[A-Z]+";
    private static final String REGEX_PASSWORD_2 = "[a-z]+";
    private static final String REGEX_PASSWORD_3 = "[0-9]+";


    /**
     * 校验邮箱
     *
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String email) {
        return Pattern.matches(REGEX_EMAIL, email);
    }

    /**
     * 校验手机
     *
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPhone(String phone) {
        return true;
//        return PhoneNumberUtils.isGlobalPhoneNumber(phone);
    }

    public static boolean isVerifyCode(String verifyCode) {
        return Pattern.matches(REGEX_VERIFY_CODE, verifyCode);
    }

    public static boolean isAntiPhishingCode(String antiPhishingCode) {
        return Pattern.matches(REGEX_ANTI_PHISHING, antiPhishingCode);
    }

    /**
     * 校验密码
     *
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
    }

    public static boolean isIDNumber(String idNumber) {
        return Pattern.matches(REGEX_ID, idNumber);
    }

    public static boolean checkPrivateKey(Context context, TextView privateKeyView) {
        if (TextUtils.isEmpty(privateKeyView.getText().toString())) {
            new ErrorDialog(context, "私钥不能为空").show();
            return false;
        } else {
            try {
                new PrivateKeyValidator(privateKeyView.getText().toString()).validate();
            } catch (Exception e) {
                new ErrorDialog(context, "私钥格式不正确").show();
                return false;
            }
        }
        return true;
    }

    public static boolean checkAccountName(Context context, TextView accountView) {
        if (TextUtils.isEmpty(accountView.getText().toString())) {
            new ErrorDialog(context, "账户名不能为空").show();
            return false;
        } else if (accountView.getText().toString().length() > 16) {
            new ErrorDialog(context, "账户名必须在16个字符以内").show();
            return false;
        }
        return true;
    }

    public static boolean checkOldPassword(Context context, TextView oldPassword) {
        if (TextUtils.isEmpty(oldPassword.getText().toString())) {
            new ErrorDialog(context, "原密码不能为空").show();
            return false;
        } else {
            boolean ret = Identity.getCurrentIdentity().verifyPassword(
                    oldPassword.getText().toString());
            if (!ret) {
                new ErrorDialog(context, "原密码不正确").show();
                return false;
            } else {
                return true;
            }
        }
    }

    public static boolean checkPassword(Context context, TextView password) {
        if (TextUtils.isEmpty(password.getText().toString())) {
            new ErrorDialog(context, "密码不能为空").show();
            return false;
        } else {
            boolean ret = Identity.getCurrentIdentity().verifyPassword(
                    password.getText().toString());
            if (!ret) {
                new ErrorDialog(context, "密码不正确").show();
                return false;
            } else {
                return true;
            }
        }
    }

    public static boolean checkNewPassword(Context context, TextView password,
            TextView confirmPassword) {
        if (TextUtils.isEmpty(password.getText().toString())) {
            new ErrorDialog(context, "密码不能为空").show();
            return false;
        } else if (!isPassword(password.getText().toString())) {
            new ErrorDialog(context, "密码至少8个字符（包括大小写和数字）").show();
            return false;
        } else if (!TextUtils.equals(password.getText().toString(),
                confirmPassword.getText().toString())) {
            new ErrorDialog(context, "两次输入密码必须相同").show();
            return false;
        }
        return true;
    }

    public static boolean checkAddress(Context context, TextView address) {
        if (TextUtils.isEmpty(address.getText().toString())) {
            new ErrorDialog(context, "地址不能为空").show();
            return false;
        }
        return true;
    }

    public static boolean checkNoteName(Context context, TextView noteName) {
        if (TextUtils.isEmpty(noteName.getText().toString())) {
            new ErrorDialog(context, "备注名不能为空").show();
            return false;
        }
        return true;
    }

    public static void main(String args[]) {
        System.out.println("---------------密码测试---------------");
        System.out.println(isPassword("12345678"));
        System.out.println(isPassword("ABCDEFGH"));
        System.out.println(isPassword("abcdefgh"));
        System.out.println(isPassword("1234567A"));
        System.out.println(isPassword("1234567a"));
        System.out.println(isPassword("123456Aa"));
        System.out.println(isPassword("12345678ABCDEFGHabcdefgh"));
        System.out.println(isPassword("12345Aa"));
        System.out.println(isPassword("12345Aa,"));

        System.out.println("---------------大陆身份证号测试---------------");
        System.out.println(isIDNumber("51132119860625727x"));
        System.out.println(isIDNumber("51132119860625727X"));
        System.out.println(isIDNumber("11010120160101487x"));

        System.out.println("---------------香港身份证号测试---------------");
        System.out.println(isIDNumber("Z3127916"));
        System.out.println(isIDNumber("X825905A"));
        System.out.println(isIDNumber("N601463A"));

        System.out.println("---------------台湾身份证号测试---------------");
        System.out.println(isIDNumber("A223340555"));
        System.out.println(isIDNumber("B291520066"));
        System.out.println(isIDNumber("O211444176"));

        System.out.println("---------------括号测试---------------");
        System.out.println(
                isIDNumber("(O211444176)xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"));
        System.out.println(isIDNumber("(O211)444176"));
        System.out.println(isIDNumber("（O211）444176"));


        System.out.println("---------------KOFO Pair---------------");
        KofoPair kofoPair = KofoUtil.createKofo();
        System.out.println("KofoId : " + kofoPair.getKofoId());
        System.out.println("KofoPubKey : " + kofoPair.getKofoPubKey());
        System.out.println("KofoSecret : " + kofoPair.getKofoSecret());
    }
}
