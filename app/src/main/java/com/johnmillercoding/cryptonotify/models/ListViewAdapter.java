package com.johnmillercoding.cryptonotify.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.johnmillercoding.cryptonotify.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ListViewAdapter extends BaseAdapter {

    // Constants
    public static final String EXCHANGE = "exchange";
    public static final String BTC_PRICE = "btcPrice";
    public static final String USD_PRICE = "usdPrice";

    // Instance vars
    private final ArrayList<HashMap<String, String>> list;
    private final Context context;

    // Constructor
    public ListViewAdapter(Context context, ArrayList<HashMap<String, String>> list) {
        super();
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.list_view_row, parent, false);
        }
        TextView exchangeTextView = view.findViewById(R.id.exchangeTextView);
        TextView btcPriceTextView = view.findViewById(R.id.btcPriceTextView);
        TextView usdPriceTextView = view.findViewById(R.id.usdPriceTextView);

        HashMap<String, String> map = list.get(position);
        exchangeTextView.setText(map.get(EXCHANGE));
        btcPriceTextView.setText(map.get(BTC_PRICE));
        usdPriceTextView.setText(map.get(USD_PRICE));
        usdPriceTextView.setSingleLine(true);
        return view;
    }
}