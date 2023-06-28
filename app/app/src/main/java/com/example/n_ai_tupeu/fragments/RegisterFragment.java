package com.example.n_ai_tupeu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.Response;


import com.example.n_ai_tupeu.R;
import com.example.n_ai_tupeu.helpers.VolleyConfigSingleton;

import org.json.JSONObject;


public class RegisterFragment extends Fragment {
    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        usernameEditText = view.findViewById(R.id.usernameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        Button registerButton = view.findViewById(R.id.registerButton);
        Button goToLoginButton = view.findViewById(R.id.goToLoginButton);

        registerButton.setOnClickListener(v -> registerUser());

        goToLoginButton.setOnClickListener(v -> navigateToLogin());

        return view;
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        if(username.equals("")||password.equals(""))
            Toast.makeText(requireContext(), "The fields can't be null", Toast.LENGTH_SHORT).show();

        // Create successListener and errorListener for the registration request
        Response.Listener<JSONObject> successListener = response -> {
            Toast.makeText(requireContext(), "Registration successful", Toast.LENGTH_SHORT).show();
            navigateToLogin();
        };

        Response.ErrorListener errorListener = error -> {
            String errorMessage = error.getMessage();
            Toast.makeText(requireContext(), "Registration failed: " + errorMessage, Toast.LENGTH_LONG).show();
        };

        // Use the VolleyConfigSingleton to make the registration request
        VolleyConfigSingleton volleyConfigSingleton = VolleyConfigSingleton.getInstance(requireContext());
        volleyConfigSingleton.registerUser(username, password, successListener, errorListener);
    }



    private void navigateToLogin() {
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_registerFragment_to_loginFragment);
    }
}
