package com.example.n_ai_tupeu.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.n_ai_tupeu.R;
public class AddQuestionsFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_questions, container, false);
        Button backToGameButton = view.findViewById(R.id.backToGameButton);

        backToGameButton.setOnClickListener(v -> navigateToGame());

        return view;
    }

    private void navigateToGame() {
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_addQuestionsFragment_to_gameFragment);
    }

}