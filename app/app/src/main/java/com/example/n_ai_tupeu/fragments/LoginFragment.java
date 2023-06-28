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

import com.android.volley.Response;
import com.example.n_ai_tupeu.R;
import com.example.n_ai_tupeu.SecondActivity;
import com.example.n_ai_tupeu.helpers.VolleyConfigSingleton;

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

        loginButton.setOnClickListener(v -> loginUser());


        registerButton.setOnClickListener(v -> navigateToRegister());

        return view;
    }

    private void loginUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Create successListener and errorListener for the login request
        Response.Listener<JSONObject> successListener = response -> {
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
        };

        Response.ErrorListener errorListener = error -> {
            String errorMessage = error.getMessage();
            Toast.makeText(requireContext(), "Incorrect username or password " + errorMessage, Toast.LENGTH_LONG).show();
        };

        // Use the VolleyConfigSingleton to make the login request
        VolleyConfigSingleton volleyConfigSingleton = VolleyConfigSingleton.getInstance(requireContext());
        volleyConfigSingleton.loginUser(username, password, successListener, errorListener);
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