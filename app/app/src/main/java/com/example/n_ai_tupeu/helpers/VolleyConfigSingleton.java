package com.example.n_ai_tupeu.helpers;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import androidx.collection.LruCache;

public class VolleyConfigSingleton {
    private static VolleyConfigSingleton instance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static Context context;

    private VolleyConfigSingleton(Context context) {
        this.context = context;
        this.requestQueue = getRequestQueue();
        this.imageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public static synchronized VolleyConfigSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyConfigSingleton(context);
        }
        return instance;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

    public void getUserAccount(String userId, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = Constants.BASE_URL + userId;

        StringRequest request = new StringRequest(Request.Method.GET, url, listener, errorListener);

        addToRequestQueue(request);
    }

    public void createUserAccount(String username, String password, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = Constants.BASE_URL + Constants.ACCOUNTS_ENDPOINT;

        StringRequest request = new StringRequest(Request.Method.POST, url, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        addToRequestQueue(request);
    }
}
