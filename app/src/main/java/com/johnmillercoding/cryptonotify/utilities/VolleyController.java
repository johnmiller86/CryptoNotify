package com.johnmillercoding.cryptonotify.utilities;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Class to handle network request queues.
 */
public class VolleyController extends Application {

    // Request tag
    private static final String TAG = VolleyController.class.getSimpleName();

    // The RequestQueue and self instance
    private RequestQueue mRequestQueue;
    private static VolleyController instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    /**
     * Returns the instance of the VolleyController.
     * @return the instance.
     */
    public static synchronized VolleyController getInstance() {
        return instance;
    }

    /**
     * Gets the RequestQueue.
     * @return the RequestQueue.
     */
    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    /**
     * Adds an HTTP request to the RequestQueue.
     * @param req the String queue.
     * @param tag the http request tag.
     * @param <T> the template.
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }
}
