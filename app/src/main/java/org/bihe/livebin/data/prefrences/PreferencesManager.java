package org.bihe.livebin.data.prefrences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;


public class PreferencesManager {

    private static final String PREF_FILE_NAME = "app_preferences";
    public static final String PREF_KEY_NAME = "username";
    public static final String PREF_KEY_IDENTIFICATION_CODE = "identification_code";
    public static final String PREF_KEY_PERFORMANCE = "performance";
    public static final String PREF_KEY_DAY_OF_BIRTH = "day_of_birth";
    public static final String PREF_KEY_MONTH_OF_BIRTH = "month_of_birth";
    public static final String PREF_KEY_YEAR_OF_BIRTH = "year_of_birth";
    public static final String PREF_KEY_PASSWORD = "password";
    public static final String PREF_KEY_USER_ID = "";
    public static final String PREF_KEY_IS_LOGIN = "is_login";

    private static PreferencesManager preferencesManager;

    private final SharedPreferences sharedPreferences;

    private PreferencesManager(@NonNull Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized PreferencesManager getInstance(Context context) {
        if (preferencesManager == null) {
            preferencesManager = new PreferencesManager(context);
        }
        return preferencesManager;
    }

    public <T> void put(String key, T value) {

        if (value instanceof String) {
            sharedPreferences.edit().putString(key, (String) value).apply();
            return;
        }

        if (value instanceof Integer) {
            sharedPreferences.edit().putInt(key, (Integer) value).apply();
            return;
        }

        if (value instanceof Boolean) {
            sharedPreferences.edit().putBoolean(key, (Boolean) value).apply();
            return;
        }

        if (value instanceof Float) {
            sharedPreferences.edit().putFloat(key, (Float) value).apply();
            return;
        }

        if (value instanceof Long) {
            sharedPreferences.edit().putLong(key, (Long) value).apply();
            return;
        }
    }

    public <T> T get(String key, T defaultValue) {
        if (defaultValue instanceof String) {
            return (T) sharedPreferences.getString(key, (String) defaultValue);
        }

        if (defaultValue instanceof Integer) {
            Integer result = sharedPreferences.getInt(key, (Integer) defaultValue);
            return (T) result;
        }

        if (defaultValue instanceof Boolean) {
            Boolean result = sharedPreferences.getBoolean(key, (Boolean) defaultValue);
            return (T) result;
        }

        if (defaultValue instanceof Float) {
            Float result = sharedPreferences.getFloat(key, (Float) defaultValue);
            return (T) result;
        }

        if (defaultValue instanceof Long) {
            Long result = sharedPreferences.getLong(key, (Long) defaultValue);
            return (T) result;
        }
        return null;
    }

}
