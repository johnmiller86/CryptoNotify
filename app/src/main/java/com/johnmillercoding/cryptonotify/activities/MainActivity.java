package com.johnmillercoding.cryptonotify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.johnmillercoding.cryptonotify.R;
import com.johnmillercoding.cryptonotify.models.ExchangeRequest;
import com.johnmillercoding.cryptonotify.services.NotificationService;
import com.johnmillercoding.cryptonotify.utilities.Config;
import com.johnmillercoding.cryptonotify.utilities.VolleyController;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Tags
    private final String LOG_TAG = MainActivity.this.getClass().getSimpleName();

    // Cryptocurrencies
    private List<String> coins, exchanges;
    private HashMap<String, ArrayAdapter<String>> arrayAdapters;
    private List<ExchangeRequest> ethPrices, ltcPrices, zecPrices;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    /**
     * Initializes the UI and components.
     */
    private void initialize(){

        // Starting notification service
        startService(new Intent(this, NotificationService.class));

        // Initializing lists
        coins = new ArrayList<>();
        /*coins.add("btc");*/ coins.add("eth"); coins.add("ltc"); coins.add("zec"); // WTF BTC ISN'T WORKING
        exchanges = Arrays.asList(getResources().getStringArray(R.array.exchanges));
//        HashMap<String, ListView> listViews = new HashMap<>();
        arrayAdapters = new HashMap<>();
        ethPrices = new ArrayList<>();
        ltcPrices = new ArrayList<>();
        zecPrices = new ArrayList<>();

        // Configuring TabHost
        TabHost tabHost = findViewById(R.id.pricesTabHost);
        tabHost.setup();

        // Looping through coins
        for (String currency : coins) {

            // Configuring TabSpec
            TabHost.TabSpec currencyTab = tabHost.newTabSpec(currency.toUpperCase());
            currencyTab.setIndicator(currency.toUpperCase());

            // Configuring ListView and ArrayAdapter
            final ListView listView = new ListView(this);
            listView.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.MATCH_PARENT));
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);
            listView.setAdapter(arrayAdapter);

            // Set TabSpec content to the new ListView
            currencyTab.setContent(new TabHost.TabContentFactory() {
                @Override
                public View createTabContent(String s) {
                    return listView;
                }
            });

            // Add TabSpec to TabHost
            tabHost.addTab(currencyTab);

//            // Add ListView and ArrayAdapter to HashMaps
//            listViews.put(currency.toUpperCase(), listView);
            arrayAdapters.put(currency.toUpperCase(), arrayAdapter);
        }

        // Show prices
        getPrices();
    }

    /**
     * Gets the pricing information for each coin at each exchange.
     */
    private void getPrices(){

        Toast.makeText(this, "Getting Prices", Toast.LENGTH_LONG).show();
        for (int i = 0; i < coins.size(); i++) {
            for (int j = 0; j < exchanges.size(); j++){

                // Request price
                requestPrice(coins.get(i).toUpperCase(), "BTC,USD", exchanges.get(j));
            }
        }
    }

    /**
     * Requests coin pricing from various exchanges.
     * @param coin the crypto coin.
     * @param conversions the preferred values.
     * @param exchange the exchange to request pricing from.
     */
    private void requestPrice(final String coin, final String conversions, final String exchange){
        final String requestString = "get_price";
        String uri = Config.URL_PRICE + "?fsym=" + coin + "&tsyms=" + conversions + "&e=" + exchange;
        StringRequest strReq = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    // Adding to adapters
                    arrayAdapters.get(coin.toUpperCase()).add("EXCHANGE: " + exchange.toUpperCase() + "\tBTC: " + jsonObject.getString("BTC") + "\tUSD: " + NumberFormat.getCurrencyInstance(new Locale("en", "US"))
                            .format(jsonObject.getDouble("USD")));
                    arrayAdapters.get(coin.toUpperCase()).notifyDataSetChanged();

                    // Adding to exchange list
                    switch (coin.toUpperCase()){
                        case "ETH":
                            ethPrices.add(new ExchangeRequest(coin, exchange.toUpperCase(), jsonObject.getDouble("BTC"), jsonObject.getDouble("USD")));
                            break;
                        case "LTC":
                            ltcPrices.add(new ExchangeRequest(coin, exchange.toUpperCase(), jsonObject.getDouble("BTC"), jsonObject.getDouble("USD")));
                            break;
                        case "ZEC":
                            zecPrices.add(new ExchangeRequest(coin, exchange.toUpperCase(), jsonObject.getDouble("BTC"), jsonObject.getDouble("USD")));
                            break;
                    }
                }
                // JSON error
                catch (JSONException e) {
                    Log.d(LOG_TAG, "JSON ERROR: " + e.getMessage());
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
}
