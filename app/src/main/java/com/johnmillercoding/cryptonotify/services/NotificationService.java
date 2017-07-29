package com.johnmillercoding.cryptonotify.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.johnmillercoding.cryptonotify.R;
import com.johnmillercoding.cryptonotify.activities.MainActivity;
import com.johnmillercoding.cryptonotify.models.ExchangeRequest;
import com.johnmillercoding.cryptonotify.utilities.Config;
import com.johnmillercoding.cryptonotify.utilities.PreferenceManager;
import com.johnmillercoding.cryptonotify.utilities.VolleyController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class to model a notification service.
 */
public class NotificationService extends Service {

    // Tags
    private final String LOG_TAG = NotificationService.this.getClass().getSimpleName();

    private final Handler handler = new Handler();
    private Timer timer = null;

    // Shared prefs
    private PreferenceManager preferenceManager;

    // Cryptocurrencies
    private List<String> coins, exchanges, urls;
    private List<ExchangeRequest> ethPrices, ltcPrices, zecPrices;

    public NotificationService() {
    }

    @Override
    public void onCreate(){
        initialize();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Initializes components and starts the service.
     */
    private void initialize(){

        // Initializing SharedPreferences
        preferenceManager = new PreferenceManager(this);

        // Initializing interval
        int notificationInterval = getNotificationInterval();

        // Initializing lists
        coins = new ArrayList<>();
        coins.add("eth"); coins.add("ltc"); coins.add("zec");
        exchanges = Arrays.asList(getResources().getStringArray(R.array.exchanges));
        urls = Arrays.asList(getResources().getStringArray(R.array.urls));
        ethPrices = new ArrayList<>();
        ltcPrices = new ArrayList<>();
        zecPrices = new ArrayList<>();

        // Starting service
        if (timer != null)
            timer.cancel();
        else
            timer = new Timer();

        // Starting the notification service
        timer.scheduleAtFixedRate(new Notify(), 0, notificationInterval);
    }

    /**
     * Inner class to run the keep the service running at a scheduled rate.
     */
    private class Notify extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            handler.post(new Runnable() {
                @Override
                public void run() {
                    getPrices();
                }
            });
        }
    }

    private int getNotificationInterval(){

        switch (preferenceManager.getNotificationUnit()){

            case "Minutes":
                return preferenceManager.getNotificationInterval() * 60000;
            case "Hours":
                return preferenceManager.getNotificationInterval() * 3600000;
            default:
                return preferenceManager.getNotificationInterval() * 25200000;
        }
    }

    /**
     * Gets the pricing information for each coin at each exchange.
     */
    private void getPrices(){

        boolean last = false;
        for (int i = 0; i < coins.size(); i++) {
            for (int j = 0; j < exchanges.size(); j++){

                // Check if last request because requests are async
                if (i == coins.size() - 1 && j == exchanges.size() - 1){
                    last = true;
                }

                // Request price
                requestPrice(coins.get(i).toUpperCase(), exchanges.get(j), urls.get(j), last);
            }
        }
    }

    /**
     * Requests coin pricing from various exchanges.
     * @param coin the crypto coin.
     * @param exchange the exchange to request pricing from.
     * @param last last request flag used to determine the asynchronous requests have completed.
     */
    private void requestPrice(final String coin, final String exchange, final String url, final boolean last){
        final String requestString = "get_price";
        String uri = Config.URL_PRICE + "?fsym=" + coin + "&tsyms=BTC,USD&e=" + exchange;
        StringRequest strReq = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    // Adding to exchange list
                    switch (coin.toUpperCase()){
                        case "ETH":
                            ethPrices.add(new ExchangeRequest(coin, exchange.toUpperCase(), url, jsonObject.getDouble("BTC"), jsonObject.getDouble("USD")));
                            break;
                        case "LTC":
                            ltcPrices.add(new ExchangeRequest(coin, exchange.toUpperCase(), url, jsonObject.getDouble("BTC"), jsonObject.getDouble("USD")));
                            break;
                        case "ZEC":
                            zecPrices.add(new ExchangeRequest(coin, exchange.toUpperCase(), url, jsonObject.getDouble("BTC"), jsonObject.getDouble("USD")));
                            break;
                    }
                }
                // JSON error
                catch (JSONException e) {
                    Log.d(LOG_TAG, "JSON ERROR: " + e.getMessage());
                }
                finally {
                    // Send notifications
                    if (last){
                        sendNotifications();
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error){
                Log.d(LOG_TAG, "VOLLEY ERROR -- COIN: " + coin + " Exchange: " + exchange + " ERROR: " + error.getMessage());
            }
        });
        VolleyController.getInstance().addToRequestQueue(strReq, requestString);
    }

    /**
     * Sends notifications to the user based upon their preferred settings.
     */
    private void sendNotifications(){

        // Checking if notifications are enabled
        if (preferenceManager.allNotificationsEnabled()){

            // Check each currency and configure desired notifications
            // ETH
            if (preferenceManager.ethNotificationsEnabled()){
                // USD
                int ETH = 0;
                if (preferenceManager.getEthCurrency().equals("USD")){
                    Collections.sort(ethPrices, new Comparator<ExchangeRequest>() {
                        @Override
                        public int compare(ExchangeRequest e1, ExchangeRequest e2) {
                            return Double.compare(e1.getUsdValue(), e2.getUsdValue());
                        }
                    });
                    if (preferenceManager.getEthThreshold().isEmpty() || zecPrices.get(0).getUsdValue() <= Double.parseDouble(preferenceManager.getEthThreshold())) {
                        notification(ETH, ethPrices.get(0).getCoin() + " Alert!", ethPrices.get(0).getCoin() + " is now $" + ethPrices.get(0).getUsdValue() + " on " + ethPrices.get(0).getExchange() +"!!!", ethPrices.get(0).getUrl());
                    }
                }
                // BTC
                else{
                    Collections.sort(ethPrices, new Comparator<ExchangeRequest>() {
                        @Override
                        public int compare(ExchangeRequest e1, ExchangeRequest e2) {
                            return Double.compare(e1.getBtcValue(), e2.getBtcValue());
                        }
                    });
                    if (preferenceManager.getEthThreshold().isEmpty() || zecPrices.get(0).getBtcValue() <= Double.parseDouble(preferenceManager.getEthThreshold())) {
                        notification(ETH, ethPrices.get(0).getCoin() + " Alert!", ethPrices.get(0).getCoin() + " is now " + ethPrices.get(0).getBtcValue() + "BTC on " + ethPrices.get(0).getExchange() + "!!!", ethPrices.get(0).getUrl());
                    }
                }
            }
            // LTC
            if (preferenceManager.ltcNotificationsEnabled()){
                // USD
                int LTC = 1;
                if (preferenceManager.getLtcCurrency().equals("USD")){
                    Collections.sort(ltcPrices, new Comparator<ExchangeRequest>() {
                        @Override
                        public int compare(ExchangeRequest e1, ExchangeRequest e2) {
                            return Double.compare(e1.getUsdValue(), e2.getUsdValue());
                        }
                    });
                    if (preferenceManager.getLtcThreshold().isEmpty() || ltcPrices.get(0).getUsdValue() <= Double.parseDouble(preferenceManager.getLtcThreshold())) {
                        notification(LTC, ltcPrices.get(0).getCoin() + " Alert!", ltcPrices.get(0).getCoin() + " is now $" + ltcPrices.get(0).getUsdValue() + " on " + ltcPrices.get(0).getExchange() + "!!!", ltcPrices.get(0).getUrl());
                    }
                }
                // BTC
                else{
                    Collections.sort(ltcPrices, new Comparator<ExchangeRequest>() {
                        @Override
                        public int compare(ExchangeRequest e1, ExchangeRequest e2) {
                            return Double.compare(e1.getBtcValue(), e2.getBtcValue());
                        }
                    });
                    if (preferenceManager.getLtcThreshold().isEmpty() || ltcPrices.get(0).getBtcValue() <= Double.parseDouble(preferenceManager.getLtcThreshold())) {
                        notification(LTC, ltcPrices.get(0).getCoin() + " Alert!", ltcPrices.get(0).getCoin() + " is now " + ltcPrices.get(0).getBtcValue() + "BTC on " + ltcPrices.get(0).getExchange() + "!!!", ltcPrices.get(0).getUrl());
                    }
                }
            }
            // ZEC
            if (preferenceManager.zecNotificationsEnabled()){
                // USD
                int ZEC = 2;
                if (preferenceManager.getZecCurrency().equals("USD")){
                    Collections.sort(zecPrices, new Comparator<ExchangeRequest>() {
                        @Override
                        public int compare(ExchangeRequest e1, ExchangeRequest e2) {
                            return Double.compare(e1.getUsdValue(), e2.getUsdValue());
                        }
                    });
                    if (preferenceManager.getZecThreshold().isEmpty() || zecPrices.get(0).getUsdValue() <= Double.parseDouble(preferenceManager.getZecThreshold())) {
                        notification(ZEC, zecPrices.get(0).getCoin() + " Alert!", zecPrices.get(0).getCoin() + " is now $" + zecPrices.get(0).getUsdValue() + " on " + zecPrices.get(0).getExchange() +"!!!", zecPrices.get(0).getUrl());
                    }
                }
                // BTC
                else{
                    Collections.sort(zecPrices, new Comparator<ExchangeRequest>() {
                        @Override
                        public int compare(ExchangeRequest e1, ExchangeRequest e2) {
                            return Double.compare(e1.getBtcValue(), e2.getBtcValue());
                        }
                    });
                    if (preferenceManager.getZecThreshold().isEmpty() || zecPrices.get(0).getBtcValue() <= Double.parseDouble(preferenceManager.getZecThreshold())) {
                        notification(ZEC, zecPrices.get(0).getCoin() + " Alert!", zecPrices.get(0).getCoin() + " is now " + zecPrices.get(0).getBtcValue() + "BTC on " + ethPrices.get(0).getExchange() + "!!!", zecPrices.get(0).getUrl());
                    }
                }
            }
        }
    }

    /**
     * Builds and displays a system notification.
     * @param id the unique notification id.
     * @param title the notification title.
     * @param text the notification description.
     */
    private void notification(int id, String title, String text, String url){

        // Configuring NotificationBuilder
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this).setSmallIcon(R.drawable.notification_icon).setContentTitle(title).setContentText(text).setAutoCancel(true);

        // Intent to launch from the notification
//        Intent intent = new Intent(this, MainActivity.class);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        // TODO mess with this
        // Creates the back stack
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);

        // Adds the Intent to the back stack
        taskStackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent to the top of the stack
        taskStackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        // Getting the system notification service
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Building the notification
        notificationManager.notify(id, builder.build());
    }
}
