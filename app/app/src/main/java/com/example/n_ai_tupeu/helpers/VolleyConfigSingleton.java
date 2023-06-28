package com.example.n_ai_tupeu.helpers;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.n_ai_tupeu.database.Challenge;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class VolleyConfigSingleton {
    private static VolleyConfigSingleton instance;
    private RequestQueue requestQueue;
    private static Context context;

    private VolleyConfigSingleton(Context context) {
        this.context = context;
        this.requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        return requestQueue;
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

    public void loginUser(String username, String password, Response.Listener<JSONObject> successListener, Response.ErrorListener errorListener) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put(Constants.USERNAME, username);
            jsonBody.put(Constants.PASSWORD, password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = Constants.BASE_URL + Constants.GET_ENDPOINT;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody, successListener, errorListener);

        addToRequestQueue(request);
    }

    public void registerUser(String username, String password, Response.Listener<JSONObject> successListener, Response.ErrorListener errorListener) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put(Constants.USERNAME, username);
            jsonBody.put(Constants.PASSWORD, password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = Constants.BASE_URL + Constants.ACCOUNTS_ENDPOINT;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody, successListener, errorListener);

        addToRequestQueue(request);
    }

    public void getChallengesFromServer(String userId, Response.Listener<JSONArray> successListener, Response.ErrorListener errorListener) {
        String url = Constants.BASE_URL + Constants.CHALLENGES_ENDPOINT + userId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, successListener, errorListener);

        addToRequestQueue(request);
    }

    public void syncData(List<Challenge> challengesFromRoom, String userId, Response.Listener<JSONArray> successListener, Response.ErrorListener errorListener) {
        String url = Constants.BASE_URL + Constants.MIGRATE_TO_MONGO_ENDPOINT+userId;

        JSONArray challengesJsonArray = new JSONArray();
        for (Challenge challenge : challengesFromRoom) {
            JSONObject challengeJsonObject = new JSONObject();
            try {
                challengeJsonObject.put(Constants.TEXT, challenge.getQuestion());
                challengeJsonObject.put(Constants.TYPE, challenge.getType().getValue());
                challengeJsonObject.put(Constants.USER_ID, challenge.getIdUser());
                challengesJsonArray.put(challengeJsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, challengesJsonArray, successListener, errorListener);

        addToRequestQueue(jsonArrayRequest);
    }


}
