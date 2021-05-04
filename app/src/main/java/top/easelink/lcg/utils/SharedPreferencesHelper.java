package top.easelink.lcg.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.List;

import top.easelink.lcg.appinit.LCGApp;

import static top.easelink.lcg.account.UserSPConstantsKt.SP_COOKIE;
import static top.easelink.lcg.account.UserSPConstantsKt.SP_USER;

@SuppressWarnings("unused")
public class SharedPreferencesHelper {

    private static final int VALUE_TYPE_STRING = 0;
    private static final int VALUE_TYPE_INT = 1;
    private static final int VALUE_TYPE_LONG = 2;
    private static final int VALUE_TYPE_FLOAT = 3;
    private static final int VALUE_TYPE_BOOLEAN = 4;

    public static SharedPreferences getUserSp() {
        return LCGApp.getContext().getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
    }

    public static SharedPreferences getCookieSp() {
        return LCGApp.getContext().getSharedPreferences(SP_COOKIE, Context.MODE_PRIVATE);
    }

    public static SharedPreferences getDefaultSp() {
        return PreferenceManager.getDefaultSharedPreferences(LCGApp.getContext());
    }

    public static boolean isEmpty(SharedPreferences sp) {
        return sp == null || sp.getAll() == null || 0 == sp.getAll().size();
    }

    public static String getString(SharedPreferences sp, String key) {
        return sp.getString(key, "");
    }

    public static String getString(SharedPreferences sp, String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public static long getLong(SharedPreferences sp, String key) {
        return sp.getLong(key, 0L);
    }

    public static int getInt(SharedPreferences sp, String key) {
        return sp.getInt(key, 0);
    }

    public static void setPreference(SharedPreferences sp, String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    public static void setPreference(SharedPreferences sp, String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    public static void setPreference(SharedPreferences sp, String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    //批量put数据
    public static void setPreferenceWithList(SharedPreferences sp, List<SpItem> spItemList) {
        if (sp == null || spItemList == null || spItemList.isEmpty()) {
            return;
        }
        SharedPreferences.Editor spEditor = sp.edit();
        for (SpItem item : spItemList) {
            setSpItem(spEditor, item);
        }
        spEditor.apply();
    }

    //批量put数据
    public static void commitPreferenceWithList(SharedPreferences sp, List<SpItem> spItemList) {
        if (sp == null || spItemList == null || spItemList.isEmpty()) {
            return;
        }
        SharedPreferences.Editor spEditor = sp.edit();
        for (SpItem item : spItemList) {
            setSpItem(spEditor, item);
        }
        spEditor.commit();
    }

    public static void setPreference(SharedPreferences sp, String key, long value) {
        sp.edit().putLong(key, value).apply();
    }

    public static boolean getBoolean(SharedPreferences sp, String key) {
        return sp.getBoolean(key, false);
    }

    public static boolean getBoolean(SharedPreferences sp, String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    public static void remove(SharedPreferences sp, String key) {
        if (sp == null || TextUtils.isEmpty(key)) {
            return;
        }
        sp.edit().remove(key).apply();
    }

    private static void setSpItem(SharedPreferences.Editor spEditor, SpItem spItem) {
        if (spItem == null || spEditor == null) {
            return;
        }
        switch (spItem.mValueType) {
            case VALUE_TYPE_STRING:
                spEditor.putString(spItem.mKey, (String) spItem.mValue);
                break;
            case VALUE_TYPE_INT:
                spEditor.putInt(spItem.mKey, (Integer) spItem.mValue);
                break;
            case VALUE_TYPE_LONG:
                spEditor.putLong(spItem.mKey, (Long) spItem.mValue);
                break;
            case VALUE_TYPE_FLOAT:
                spEditor.putFloat(spItem.mKey, (Float) spItem.mValue);
                break;
            case VALUE_TYPE_BOOLEAN:
                spEditor.putBoolean(spItem.mKey, (Boolean) spItem.mValue);
                break;
        }
    }

    public static class SpItem<T> {
        private String mKey;
        private T mValue;
        private int mValueType;

        public SpItem(String key, T value) {
            this.mKey = key;
            this.mValue = value;
            this.mValueType = initValueType(value);
        }

        private int initValueType(T t) {
            int typ = -1;
            if (t instanceof String) {
                typ = VALUE_TYPE_STRING;
            } else if (t instanceof Integer) {
                typ = VALUE_TYPE_INT;
            } else if (t instanceof Long) {
                typ = VALUE_TYPE_LONG;
            } else if (t instanceof Float) {
                typ = VALUE_TYPE_FLOAT;
            } else if (t instanceof Boolean) {
                typ = VALUE_TYPE_BOOLEAN;
            }
            return typ;
        }
    }
}