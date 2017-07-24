package com.johnmillercoding.cryptonotify.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceManager {

    // Shared Preferences
    private final SharedPreferences sharedPreferences;
    private final Editor editor;

    // SharedPreferences tags
    private static final String SHARED_PREFERENCES = "sharedPreferences";
    private static final String ALL_NOTIFICATIONS = "allNotifications";
    private static final String ETH_NOTIFICATIONS = "ethNotifications";
    private static final String ZEC_NOTIFICATIONS = "zecNotifications";
    private static final String LTC_NOTIFICATIONS = "ltcNotifications";
    private static final String ETH_THRESHOLD = "ethThreshold";
    private static final String ZEC_THRESHOLD = "zecThreshold";
    private static final String LTC_THRESHOLD = "ltcThreshold";
    private static final String ETH_CURRENCY = "ethCurrency";
    private static final String ZEC_CURRENCY = "zecCurrency";
    private static final String LTC_CURRENCY = "ltcCurrency";

    // Constructor
    public PreferenceManager(Context context) {
        int PRIVATE_MODE = 0;
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, PRIVATE_MODE);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    public void enableAllNotifications(boolean enabled) {
        editor.putBoolean(ALL_NOTIFICATIONS, enabled);
        editor.apply();
    }

    public void enableEthNotifications(boolean enabled){
        editor.putBoolean(ETH_NOTIFICATIONS, enabled);
        editor.apply();
    }

    public void enableZecNotifications(boolean enabled){
        editor.putBoolean(ZEC_NOTIFICATIONS, enabled);
        editor.apply();
    }

    public void enableLtcNotifications(boolean enabled){
        editor.putBoolean(LTC_NOTIFICATIONS, enabled);
        editor.apply();
    }

    public void setEthThreshold(String threshold){
        editor.putString(ETH_THRESHOLD, threshold);
        editor.apply();
    }

    public void setZecThreshold(String threshold){
        editor.putString(ZEC_THRESHOLD, threshold);
        editor.apply();
    }

    public void setLtcThreshold(String threshold){
        editor.putString(LTC_THRESHOLD, threshold);
        editor.apply();
    }

    public void setEthCurrency(String currency){
        editor.putString(ETH_CURRENCY, currency);
        editor.apply();
    }

    public void setZecCurrency(String currency){
        editor.putString(ZEC_CURRENCY, currency);
        editor.apply();
    }

    public void setLtcCurrency(String currency){
        editor.putString(LTC_CURRENCY, currency);
        editor.apply();
    }

    public boolean allNotificationsEnabled(){ return sharedPreferences.getBoolean(ALL_NOTIFICATIONS, true); }

    public boolean ethNotificationsEnabled(){ return sharedPreferences.getBoolean(ETH_NOTIFICATIONS, true); }

    public boolean zecNotificationsEnabled(){ return sharedPreferences.getBoolean(ZEC_NOTIFICATIONS, true); }

    public boolean ltcNotificationsEnabled(){ return sharedPreferences.getBoolean(LTC_NOTIFICATIONS, true); }

    public String getEthThreshold(){ return sharedPreferences.getString(ETH_THRESHOLD, ""); }

    public String getZecThreshold(){ return sharedPreferences.getString(ZEC_THRESHOLD, ""); }

    public String getLtcThreshold(){ return sharedPreferences.getString(LTC_THRESHOLD, ""); }

    public String getEthCurrency(){ return sharedPreferences.getString(ETH_CURRENCY, "USD"); }

    public String getZecCurrency(){ return sharedPreferences.getString(ZEC_CURRENCY, "USD"); }

    public String getLtcCurrency(){ return sharedPreferences.getString(LTC_CURRENCY, "USD"); }
}