package com.example.n_ai_tupeu.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.n_ai_tupeu.R;
import com.example.n_ai_tupeu.RecyclerViewAdapter;
import com.example.n_ai_tupeu.SecondActivity;
import com.example.n_ai_tupeu.database.Challenge;
import com.example.n_ai_tupeu.database.ChallengeDatabase;

import java.util.List;


public class LoginFragment extends Fragment {
    private Button mButton;
    private EditText t1;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private List<Challenge> lista;
    private ChallengeDatabase animalDatabase;
    private int idUser=37;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button loginButton = view.findViewById(R.id.loginButton);
        Button registerButton = view.findViewById(R.id.goToRegisterButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SecondActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle register button click
                navigateToRegister();
            }
        });

        return view;
    }

    private void navigateToRegister() {
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_loginFragment_to_registerFragment);
    }

}