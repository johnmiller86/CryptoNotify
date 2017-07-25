package com.johnmillercoding.cryptonotify.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.johnmillercoding.cryptonotify.R;
import com.johnmillercoding.cryptonotify.activities.MainActivity;
import com.johnmillercoding.cryptonotify.models.ExchangePrice;
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

public class NotificationService extends Service {

    // Tags
    private final String LOG_TAG = NotificationService.this.getClass().getSimpleName();
    private final int ETH = 0, LTC = 1, ZEC = 2;

    // Service stuff
    private final int MILLISECONDS = 300000;  // 5 minutes for now
    private Handler mHandler = new Handler();
    private Timer mTimer = null;

    // Shared prefs
    private PreferenceManager preferenceManager;

    // Cryptocurrencies
    private List<String> coins, exchanges;
    private List<ExchangePrice> ethPrices, ltcPrices, zecPrices;

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

    private void initialize(){

        // Initializing SharedPreferences
        preferenceManager = new PreferenceManager(this);

        // Initializing lists
        coins = new ArrayList<>();
        coins.add("eth"); coins.add("ltc"); coins.add("zec"); // WTF BTC ISN'T WORKING
        exchanges = Arrays.asList(getResources().getStringArray(R.array.exchanges));
        ethPrices = new ArrayList<>();
        ltcPrices = new ArrayList<>();
        zecPrices = new ArrayList<>();

        // Starting service
        if (mTimer != null)
            mTimer.cancel();
        else
            mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new Notify(), 0, MILLISECONDS);
    }

    private class Notify extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    getPrices();
                }
            });
        }
    }

    private void getPrices(){

        boolean last = false;
        Toast.makeText(this, "Getting Prices", Toast.LENGTH_LONG).show();
        for (int i = 0; i < coins.size(); i++) {
            for (int j = 0; j < exchanges.size(); j++){

                // Check if last request because requests are async
                if (i == coins.size() - 1 && j == exchanges.size() - 1){
                    last = true;
                }

                // Request price
                requestPrice(coins.get(i).toUpperCase(), "BTC,USD", exchanges.get(j), last);
            }
        }
    }

    private void requestPrice(final String coin, final String conversions, final String exchange, final boolean last){
        final String requestString = "get_price";
        String uri = Config.URL_PRICE + "?fsym=" + coin + "&tsyms=" + conversions + "&e=" + exchange;
        StringRequest strReq = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    // Adding to exchange list
                    switch (coin.toUpperCase()){
                        case "ETH":
                            ethPrices.add(new ExchangePrice(coin, exchange.toUpperCase(), jsonObject.getDouble("BTC"), jsonObject.getDouble("USD")));
                            break;
                        case "LTC":
                            ltcPrices.add(new ExchangePrice(coin, exchange.toUpperCase(), jsonObject.getDouble("BTC"), jsonObject.getDouble("USD")));
                            break;
                        case "ZEC":
                            zecPrices.add(new ExchangePrice(coin, exchange.toUpperCase(), jsonObject.getDouble("BTC"), jsonObject.getDouble("USD")));
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
//                Toast.makeText(getApplicationContext(), "We're sorry! We could not retrieve data from the servers.", Toast.LENGTH_LONG).show();  // TODO only if activity in focus
            }
        });
        VolleyController.getInstance().addToRequestQueue(strReq, requestString);
    }

    private void sendNotifications(){

        // Checking if notifications are enabled
        if (preferenceManager.allNotificationsEnabled()){

            // Check each notification
            if (preferenceManager.ethNotificationsEnabled()){
                if (preferenceManager.getEthCurrency().equals("USD")){
                    Collections.sort(ethPrices, new Comparator<ExchangePrice>() {
                        @Override
                        public int compare(ExchangePrice e1, ExchangePrice e2) {
                            return Double.compare(e1.getUsdValue(), e2.getUsdValue());
                        }
                    });
                    if (preferenceManager.getEthThreshold().isEmpty() || zecPrices.get(0).getUsdValue() <= Double.parseDouble(preferenceManager.getEthThreshold())) {
                        notification(ETH, ethPrices.get(0).getCoin() + " Alert!", ethPrices.get(0).getCoin() + " is now $" + ethPrices.get(0).getUsdValue() + "!!!");
                    }
                }
                else{
                    Collections.sort(ethPrices, new Comparator<ExchangePrice>() {
                        @Override
                        public int compare(ExchangePrice e1, ExchangePrice e2) {
                            return Double.compare(e1.getBtcValue(), e2.getBtcValue());
                        }
                    });
                    if (preferenceManager.getEthThreshold().isEmpty() || zecPrices.get(0).getBtcValue() <= Double.parseDouble(preferenceManager.getEthThreshold())) {
                        notification(ETH, ethPrices.get(0).getCoin() + " Alert!", ethPrices.get(0).getCoin() + " is now " + ethPrices.get(0).getBtcValue() + "BTC!!!");
                    }
                }
            }
            if (preferenceManager.ltcNotificationsEnabled()){
                if (preferenceManager.getLtcCurrency().equals("USD")){
                    Collections.sort(ltcPrices, new Comparator<ExchangePrice>() {
                        @Override
                        public int compare(ExchangePrice e1, ExchangePrice e2) {
                            return Double.compare(e1.getUsdValue(), e2.getUsdValue());
                        }
                    });
                    if (preferenceManager.getLtcThreshold().isEmpty() || ltcPrices.get(0).getUsdValue() <= Double.parseDouble(preferenceManager.getLtcThreshold())) {
                        notification(LTC, ltcPrices.get(0).getCoin() + " Alert!", ltcPrices.get(0).getCoin() + " is now $" + ltcPrices.get(0).getUsdValue() + "!!!");
                    }
                }
                else{
                    Collections.sort(ltcPrices, new Comparator<ExchangePrice>() {
                        @Override
                        public int compare(ExchangePrice e1, ExchangePrice e2) {
                            return Double.compare(e1.getBtcValue(), e2.getBtcValue());
                        }
                    });
                    if (preferenceManager.getLtcThreshold().isEmpty() || ltcPrices.get(0).getBtcValue() <= Double.parseDouble(preferenceManager.getLtcThreshold())) {
                        notification(LTC, ltcPrices.get(0).getCoin() + " Alert!", ltcPrices.get(0).getCoin() + " is now " + ltcPrices.get(0).getBtcValue() + "BTC!!!");
                    }
                }
            }
            if (preferenceManager.zecNotificationsEnabled()){
                if (preferenceManager.getZecCurrency().equals("USD")){
                    Collections.sort(zecPrices, new Comparator<ExchangePrice>() {
                        @Override
                        public int compare(ExchangePrice e1, ExchangePrice e2) {
                            return Double.compare(e1.getUsdValue(), e2.getUsdValue());
                        }
                    });
                    if (preferenceManager.getZecThreshold().isEmpty() || zecPrices.get(0).getUsdValue() <= Double.parseDouble(preferenceManager.getZecThreshold())) {
                        notification(ZEC, zecPrices.get(0).getCoin() + " Alert!", zecPrices.get(0).getCoin() + " is now $" + zecPrices.get(0).getUsdValue() + "!!!");
                    }
                }
                else{
                    Collections.sort(zecPrices, new Comparator<ExchangePrice>() {
                        @Override
                        public int compare(ExchangePrice e1, ExchangePrice e2) {
                            return Double.compare(e1.getBtcValue(), e2.getBtcValue());
                        }
                    });
                    if (preferenceManager.getZecThreshold().isEmpty() || zecPrices.get(0).getBtcValue() <= Double.parseDouble(preferenceManager.getZecThreshold())) {
                        notification(ZEC, zecPrices.get(0).getCoin() + " Alert!", zecPrices.get(0).getCoin() + " is now " + zecPrices.get(0).getBtcValue() + "BTC!!!");
                    }
                }
            }
        }
    }

    // TODO modify
    private void notification(int id, String title, String text){
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(title)
                        .setContentText(text);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        mNotificationManager.notify(id, mBuilder.build());
    }
}
