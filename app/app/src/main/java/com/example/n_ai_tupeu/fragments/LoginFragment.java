package com.example.n_ai_tupeu.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.n_ai_tupeu.R;
import com.example.n_ai_tupeu.SecondActivity;
import com.example.n_ai_tupeu.helpers.Constants;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginFragment extends Fragment {
    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button loginButton = view.findViewById(R.id.loginButton);
        Button registerButton = view.findViewById(R.id.goToRegisterButton);
        usernameEditText = view.findViewById(R.id.usernameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRegister();
            }
        });

        return view;
    }

    private void loginUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Create a JSON object to hold the username and password
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put(Constants.USERNAME, username);
            jsonBody.put(Constants.PASSWORD, password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Make the HTTP GET request to the login endpoint
        String url = Constants.BASE_URL + Constants.GET_ENDPOINT;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show();
                try {
                    saveId(response.getString("id"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                saveLogState();
                Intent intent = new Intent(getActivity(), SecondActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = error.getMessage();
                Toast.makeText(requireContext(), "Login failed: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        // Add the request to the request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(request);
    }

    private void saveId(String id){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", id);
        editor.apply();
    }
    private void saveLogState(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("state", true);
        editor.apply();
    }

    private void navigateToRegister() {
        NavHostFragment.findNavController(this).navigate(R.id.action_loginFragment_to_registerFragment);
    }

}