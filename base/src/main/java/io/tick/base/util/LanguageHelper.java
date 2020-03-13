package io.tick.base.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import java.util.Locale;

import io.tick.base.ContextProvider;
import io.tick.base.eventbus.EventBusCenter;


/**
 * Created by xianghairui on 2018/2/15.
 */

public class LanguageHelper {
    private static final String APP_LANGUAGE = "appLanguage";
    private static final String LANGUAGE_NAME = "languageName";
    public static final int EC_CHANGE_APP_LANGUAGE = 100000002;
    public static final String FOLLOW_SYSTEM = "followSystem"; //跟随系统
    public static final String SIMPLIFIED_CHINESE = "simplifiedChinese"; //简体中文
    public static final String TRADITIONAL_CHINESE = "traditionalChinese";//繁体中文
    public static final String ENGLISH = "english";  //英语
    public static final String THAILAND = "Thailand";//泰国
    public static final String INDONESIA = "Indonesia";//印尼

    private static void setting(Locale locale) {
        Resources resources = ContextProvider.getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, metrics);
        SpUtil.putObj(APP_LANGUAGE, locale);
        EventBusCenter.post(EC_CHANGE_APP_LANGUAGE);
    }

    public static Locale getCurAppLanguage() {
        return SpUtil.getObj(APP_LANGUAGE, Locale.class, getCurSystemLanguage());
    }

    private static Locale getCurSystemLanguage() {
        return Resources.getSystem().getConfiguration().locale;
    }

    public static void followSystem() {
        languageSetting(getCurSystemLanguage(), FOLLOW_SYSTEM);
    }

    public static void simplifiedChinese() {
        languageSetting(Locale.SIMPLIFIED_CHINESE, SIMPLIFIED_CHINESE);//simplifiedChinese
    }

    public static void traditionalChinese() {
        languageSetting(Locale.TRADITIONAL_CHINESE, TRADITIONAL_CHINESE);//traditionalChinese
    }

    public static void english() {
        languageSetting(Locale.ENGLISH, ENGLISH);//english
    }

    public static void thailand() {
        languageSetting(new Locale("th"), THAILAND);
    }

    public static void indonesia() {
        languageSetting(new Locale("id"), INDONESIA);
    }

    /**
     * 更改app内语言
     */
    private static void languageSetting(Locale locale, String name) {
        SpUtil.putObj(LANGUAGE_NAME, name);
        Locale cache = SpUtil.getObj(APP_LANGUAGE, Locale.class, getCurSystemLanguage());
        if (!cache.equals(locale)) {
            setting(locale);
        }
    }

    public static void languageSetting(Locale locale) {
        languageSetting(locale, null);
    }

    public static void initAppLanguage() {
        initSystemLanguage();
        if (!getCurSystemLanguage().equals(getCurAppLanguage())) {
            setting(getCurAppLanguage());
        }
    }

    private static void initSystemLanguage() {
        if (FOLLOW_SYSTEM.equals(getName())) {
            Locale appLanguage = getCurAppLanguage();
            if (!getCurSystemLanguage().equals(appLanguage)) {
                setting(getCurSystemLanguage());
            }
        }
    }

    public static String getName() {
        return SpUtil.getObj(LANGUAGE_NAME, String.class, FOLLOW_SYSTEM);
    }

    public static Context attachBaseContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 8.0需要使用createConfigurationContext处理
            return updateResources(context);
        } else {
            return context;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context) {
        Resources resources = context.getResources();
        Locale locale = getCurAppLanguage();// getSetLocale方法是获取新设置的语言
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLocales(new LocaleList(locale));
        return context.createConfigurationContext(configuration);
    }

    /**
     * 跟随系统
     */
    public static String getSystemLanguage() {
        Locale locale = getCurSystemLanguage();
        String language = locale.getLanguage();
        String country = locale.getCountry().toLowerCase();
        if ("zh".equals(language)) {
            if ("hk".equals(country) || "tw".equals(country)) {
                language = TRADITIONAL_CHINESE;
            } else {
                language = SIMPLIFIED_CHINESE;
            }
        } else if ("th".equals(language)) {
            language = THAILAND;
        } else if ("id".equals(language)) {
            language = INDONESIA;
        } else {
            language = ENGLISH;
        }
        return language;
    }

    public static String getLang() {
        String name = LanguageHelper.getName();
        if (LanguageHelper.FOLLOW_SYSTEM.equals(name)) {
            if (LanguageHelper.SIMPLIFIED_CHINESE.equals(LanguageHelper.getSystemLanguage())) {
                return "zh-CN";
            } else {
                return "en-US";
            }
        } else if (LanguageHelper.SIMPLIFIED_CHINESE.equals(name)) {
            return "zh-CN";
        } else {
            return "en-US";
        }
    }
}
