package com.johnmillercoding.cryptonotify.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

public class NetworkReceiver extends BroadcastReceiver {

    // Instance vars
    private final List<NetworkStateReceiverListener> listeners;
    private Boolean connected;

    // Constructor
    public NetworkReceiver() {
        listeners = new ArrayList<>();
        connected = null;
    }

    /**
     * Notifies the listeners on network received.
     * @param context the Fragment/Activity's context.
     * @param intent the intent.
     */
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getExtras() == null)
            return;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED || networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTING) {
            connected = true;
        } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
            connected = false;
        }
        notifyStateToAll();
    }

    /**
     * Notifies the listeners of network changes.
     */
    private void notifyStateToAll() {
        for(NetworkStateReceiverListener listener : listeners)
            notifyState(listener);
    }

    /**
     * Calls appropriate interface based on network state.
     * @param listener the network listener.
     */
    private void notifyState(NetworkStateReceiverListener listener) {
        if(connected == null || listener == null)
            return;

        if(connected)
            listener.networkAvailable();
        else
            listener.networkUnavailable();
    }

    /**
     * Adds a listener to the NetworkReceiver instance.
     * @param listener the network listener.
     */
    public void addListener(NetworkStateReceiverListener listener) {
        listeners.add(listener);
        notifyState(listener);
    }

    /**
     * Interfaces to be implemented.
     */
    public interface NetworkStateReceiverListener {
        void networkAvailable();
        void networkUnavailable();
    }
}