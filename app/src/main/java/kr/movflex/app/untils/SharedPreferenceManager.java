package kr.movflex.app.untils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {
    private static volatile SharedPreferenceManager instance = null;
    private static final String KEY = "movflex";

    public static SharedPreferenceManager getInstance() {
        if (null == instance) {
            synchronized (SharedPreferenceManager.class) {
                instance = new SharedPreferenceManager();
            }
        }
        return instance;
    }

    public String getUserToken(Context context) {
        return context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getString("token", "");
    }

    public void setUserToken(Context context, String userToken) {
        SharedPreferences.Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putString("token", userToken);
        editor.apply();
    }

    public boolean isFirstLaunch(Context context) {
        return context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getBoolean("isFirstLaunch", true);
    }

    public void setFirstLaunch(Context context, Boolean isFirstLaunch) {
        SharedPreferences.Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean("isFirstLaunch", isFirstLaunch);
        editor.apply();
    }
}
