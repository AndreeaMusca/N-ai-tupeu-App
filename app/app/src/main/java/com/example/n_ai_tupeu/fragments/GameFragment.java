package com.example.n_ai_tupeu.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.n_ai_tupeu.R;
public class GameFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_game, container, false);
        Button addQuestionsButton = view.findViewById(R.id.goToAddQuestionsButton);

        addQuestionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAddQuestions();
            }
        });

        return view;
    }

    private void navigateToAddQuestions() {
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_gameFragment_to_addQuestionsFragment);
    }

}