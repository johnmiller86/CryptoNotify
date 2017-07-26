package com.johnmillercoding.cryptonotify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.johnmillercoding.cryptonotify.R;
import com.johnmillercoding.cryptonotify.models.ListViewAdapter;
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
import java.util.Map;

import static com.johnmillercoding.cryptonotify.models.ListViewAdapter.BTC_PRICE;
import static com.johnmillercoding.cryptonotify.models.ListViewAdapter.EXCHANGE;
import static com.johnmillercoding.cryptonotify.models.ListViewAdapter.USD_PRICE;

public class MainActivity extends AppCompatActivity {

    // Tags
    private final String LOG_TAG = MainActivity.this.getClass().getSimpleName();

    // Lists
    private List<String> coins, exchanges;
    private HashMap<String, ListView> listViews;
    private ArrayList<HashMap<String, String>> ethPrices, ltcPrices, zecPrices;


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
        listViews = new HashMap<>();
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

            // Set TabSpec content to the new ListView
            currencyTab.setContent(new TabHost.TabContentFactory() {
                @Override
                public View createTabContent(String s) {
                    return listView;
                }
            });

            // Add TabSpec to TabHost
            tabHost.addTab(currencyTab);

            // Add ListView to HashMap
            listViews.put(currency.toUpperCase(), listView);
        }

        // Show prices
        getPrices();
    }

    /**
     * Gets the pricing information for each coin at each exchange.
     */
    private void getPrices(){

        Toast.makeText(this, "Getting Prices", Toast.LENGTH_LONG).show();
        boolean last = false;
        for (int i = 0; i < coins.size(); i++) {
            for (int j = 0; j < exchanges.size(); j++){

                // Check if last request because requests are async
                if (i == coins.size() - 1 && j == exchanges.size() - 1){
                    last = true;
                }

                // Request price
                requestPrice(coins.get(i).toUpperCase(), exchanges.get(j), last);
            }
        }
    }

    /**
     * Requests coin pricing from various exchanges.
     * @param coin the crypto coin.
     * @param exchange the exchange to request pricing from.
     */
    private void requestPrice(final String coin, final String exchange, final boolean last){
        final String requestString = "get_price";
        String uri = Config.URL_PRICE + "?fsym=" + coin + "&tsyms=BTC,USD&e=" + exchange;
        StringRequest strReq = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    // Configure entry and add to list
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(EXCHANGE, String.valueOf(exchange.toUpperCase()));
                    hashMap.put(BTC_PRICE, jsonObject.getString("BTC") + "BTC");
                    hashMap.put(USD_PRICE, NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(jsonObject.getDouble("USD")));
                    switch (coin.toUpperCase()){
                        case "ETH":
                            ethPrices.add(hashMap);
                            break;
                        case "LTC":
                            ltcPrices.add(hashMap);
                            break;
                        case "ZEC":
                            zecPrices.add(hashMap);
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

                        // TODO put in sorting of the HashMaps

                        // Configure each ListViewAdapter
                        for (Map.Entry<String, ListView> entry : listViews.entrySet()){

                            ListViewAdapter adapter;
                            switch (entry.getKey()){
                                case "ETH":
                                    adapter = new ListViewAdapter(getApplicationContext(), ethPrices);
                                    break;
                                case "LTC":
                                    adapter = new ListViewAdapter(getApplicationContext(), ltcPrices);
                                    break;
                                default:
                                    adapter = new ListViewAdapter(getApplicationContext(), zecPrices);
                                    break;
                            }
                            entry.getValue().setAdapter(adapter);
                        }
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
}
