package io.tick.base.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

import io.tick.base.BaseApp;

public class SpUtil {

    private final static String SS_NAME = "app_base";

    public static SharedPreferences getSharedPreferences() {
        try {
            return BaseApp.getContext().getSharedPreferences(SS_NAME, Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getIdentity(String id, String key) {
        return id == null ? key : id + "_" + key;
    }

    public static <T> void putObj(String id, String key, T obj) {
        putObj(getIdentity(id, key), obj);
    }

    public static <T> void putObj(String key, T obj) {
        try {
            getSharedPreferences().edit().putString(key, JsonUtils.toJsonString(obj)).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> void putList(String id, String key, List<T> lst) {
        putList(getIdentity(id, key), lst);
    }

    public static <T> void putList(String key, List<T> lst) {
        try {
            getSharedPreferences().edit().putString(key, JsonUtils.toJsonString(lst)).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T getObj(String id, String key, Class<T> clazz, T defaultVale) {
        return getObj(getIdentity(id, key), clazz, defaultVale);
    }

    public static <T> T getObj(String key, Class<T> clazz, T defaultVale) {
        T t = defaultVale;
        try {
            String jsonStr = getSharedPreferences().getString(key, null);
            if (null != jsonStr) {
                t = JsonUtils.parse(jsonStr, clazz);
            }
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    public static <T> T getObj(String id, String key, Class<T> clazz) {
        return getObj(getIdentity(id, key), clazz);
    }

    public static <T> T getObj(String key, Class<T> clazz) {
        try {
            T t = null;
            String jsonStr = getSharedPreferences().getString(key, null);
            if (null == jsonStr) {
                if (Integer.class == clazz) {
                    t = (T) new Integer(0);
                } else if (Long.class == clazz) {
                    t = (T) new Long(0);
                } else if (Float.class == clazz) {
                    t = (T) new Float(0);
                } else if (Double.class == clazz) {
                    t = (T) new Double(0);
                } else if (Boolean.class == clazz) {
                    t = (T) new Boolean(false);
                }
            } else {
                t = JsonUtils.parse(jsonStr, clazz);
            }
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> getList(String id, String key, Class<T> clazz) {
        return getList(getIdentity(id, key), clazz);
    }

    public static <T> List<T> getList(String key, Class<T> clazz) {
        try {
            return JsonUtils.parseList(getSharedPreferences().getString(key, null), clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean remove(String id, String key) {
        return remove(getIdentity(id, key));
    }

    public static boolean remove(String key) {
        try {
            return getSharedPreferences().edit().remove(key).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean clear() {
        try {
            return getSharedPreferences().edit().clear().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
