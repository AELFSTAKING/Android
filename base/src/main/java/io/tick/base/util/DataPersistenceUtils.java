package io.tick.base.util;

import android.util.Log;

public class DataPersistenceUtils {

    private static final String CLIENT_ID = "clientId";
    private static final String TOKEN = "token";
    private static final String LANGUAGE_INDEX = "language_index";
    private static final String HVALUE = "hvalue";

    public static void clear() {
        SpUtil.clear();
    }

    public static void putToken(String token) {
        SpUtil.putObj(TOKEN, token);
    }

    public static String getToken() {
        return SpUtil.getObj(TOKEN, String.class);
    }

    public static void putClientId(String account) {
        SpUtil.putObj(CLIENT_ID, account);
    }

    public static String getClientId() {
        return SpUtil.getObj(CLIENT_ID, String.class);
    }

    public static int getLanguageIndex() {
        return SpUtil.getObj(LANGUAGE_INDEX, Integer.class, 0);
    }

    public static void saveLanguageIndex(int languageIndex) {
        SpUtil.putObj(LANGUAGE_INDEX, languageIndex);
    }

    public static void putPreimage(String hValue, String preimage) {
        SpUtil.putObj(HVALUE + "_" + hValue, preimage);
    }

    public static String getPreimage(String hValue) {
        String preimge = SpUtil.getObj(HVALUE + "_" + hValue, String.class);
        Log.i("Dupeng", "hValue=" + hValue + ", preimge=" + preimge);
        return preimge;
    }
}
