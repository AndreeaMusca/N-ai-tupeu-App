package com.example.n_ai_tupeu.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.example.n_ai_tupeu.R;
import com.example.n_ai_tupeu.RecyclerViewAdapter;
import com.example.n_ai_tupeu.database.Challenge;
import com.example.n_ai_tupeu.database.ChallengeDatabase;
import com.example.n_ai_tupeu.database.ChallengeType;
import com.example.n_ai_tupeu.helpers.VolleyConfigSingleton;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AddQuestionsFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {
    private EditText challengeText;
    private RecyclerViewAdapter adapter;
    private List<Challenge> challenges;
    private ChallengeDatabase challengeDatabase;
    RadioGroup radioGroup;
    ChallengeType.Type challengeType;

    private String idUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the ChallengesDatabase
        challengeDatabase = Room.databaseBuilder(requireContext(),
                        ChallengeDatabase.class, "challenge-database")
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_questions, container, false);
        ImageButton backToGameButton = view.findViewById(R.id.backToGameButton);
        Button addButton = view.findViewById(R.id.addButton);
        ImageButton syncButton = view.findViewById(R.id.syncButton);
        Button deleteAllButton = view.findViewById(R.id.deleteAllButton);
        ImageButton logoutButton=view.findViewById(R.id.logoutButton);


        challengeText = view.findViewById(R.id.challenge_text);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        radioGroup = (RadioGroup) view.findViewById(R.id.challenge_group);
        radioGroup.setOnCheckedChangeListener(this);

        challenges = new ArrayList<>();
        adapter = new RecyclerViewAdapter(challenges, challengeDatabase, 1);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        idUser=getUserId();
        displayDatabase(); // Load data from Room

        backToGameButton.setOnClickListener(v -> navigateToGame());
        addButton.setOnClickListener(v -> insertChallenge());
        deleteAllButton.setOnClickListener(v -> confirmDeleteAllChallenges());
        syncButton.setOnClickListener(v -> syncData());
        logoutButton.setOnClickListener(v->logout());
        return view;

    }

    private void logout() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", "");
        editor.putBoolean("state", false);
        editor.apply();

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.loginFragment);
    }

    private void syncData() {
        // Get the challenges from Room database
        new Thread(() -> requireActivity().runOnUiThread(() -> {
            // Use the VolleyConfigSingleton to sync the data
            VolleyConfigSingleton volleyConfigSingleton = VolleyConfigSingleton.getInstance(requireContext());
            volleyConfigSingleton.syncData(challenges, idUser,successListener, errorListener);
        })).start();
    }

    // Define successListener and errorListener for the Volley request
    Response.Listener<JSONArray> successListener = response -> Toast.makeText(requireContext(), "Succes: ", Toast.LENGTH_LONG).show();

    Response.ErrorListener errorListener = error -> Toast.makeText(requireContext(), "Failed " , Toast.LENGTH_LONG).show();

    private void insertChallenge(){
            if (challengeText.getText().toString().trim().isEmpty() || challengeType == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setMessage("One of the fields is empty.");
                builder.setTitle("Alert !");
                builder.setCancelable(false);
                builder.setNegativeButton("Back", (dialog, which) -> dialog.cancel());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                String text1 = challengeText.getText().toString();
                Challenge a = new Challenge(text1, challengeType, idUser);
                if (existChallenge(a)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setMessage("Challenge already exist.");
                    builder.setTitle("Alert !");
                    builder.setCancelable(false);
                    builder.setNegativeButton("Back", (dialog, which) -> dialog.cancel());
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    challengeText.setText("");
                } else {
                    challenges.add(a);
                    insertChallenge(a);
                }

                adapter.notifyDataSetChanged();
                challengeText.setText("");
                radioGroup.clearCheck();
            }
        }

    private void confirmDeleteAllChallenges() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Are you sure you want to delete all challenges?");
        builder.setTitle("Confirm Delete");
        builder.setPositiveButton("Delete", (dialog, which) -> deleteAllChallenges());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deleteAllChallenges() {
        new Thread(() -> {
            challengeDatabase.challengeDao().deleteAllChallenges();
            requireActivity().runOnUiThread(() -> {
                challenges.clear();
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void insertChallenge(Challenge challenge) {
        new Thread(() -> {
            challengeDatabase.challengeDao().insertChallenge(challenge);
            // Refresh the data after insertion
            requireActivity().runOnUiThread(this::displayDatabase);
        }).start();
    }

    private boolean existChallenge(Challenge challenge) {
        for (Challenge obj : challenges) {
            if (obj.getQuestion().equalsIgnoreCase(challenge.getQuestion())) {
                return true;
            }
        }
        return false;
    }

    private void displayDatabase() {
        new Thread(() -> {
            challenges.clear();
            challenges.addAll(challengeDatabase.challengeDao().getAllChallengesByUser(idUser));
            requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
        }).start();
    }

    private void navigateToGame() {
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_addQuestionsFragment_to_gameFragment);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radioButton_dare:
                challengeType = ChallengeType.Type.Dare;
                break;

            case R.id.radioButton_truth:
                challengeType = ChallengeType.Type.Truth;
                break;
        }
    }


    private String getUserId() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userId", "");
    }

}