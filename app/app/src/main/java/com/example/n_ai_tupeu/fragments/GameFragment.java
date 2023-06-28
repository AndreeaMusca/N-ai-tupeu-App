package com.example.n_ai_tupeu.fragments;

import android.content.Context;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Response;

import com.example.n_ai_tupeu.R;
import com.example.n_ai_tupeu.RecyclerViewAdapter;
import com.example.n_ai_tupeu.database.Challenge;
import com.example.n_ai_tupeu.database.ChallengeDatabase;
import com.example.n_ai_tupeu.database.ChallengeType;

import com.example.n_ai_tupeu.helpers.VolleyConfigSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GameFragment extends Fragment {
    private RecyclerViewAdapter adapter;
    private List<Challenge> challenges;
    private List<Challenge> truths;
    private List<Challenge> dares;
    private Button truthButton;
    private Button dareButton;
    private Button randomButton;
    private ChallengeDatabase challengeDatabase;
    private String idUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        challengeDatabase = Room.databaseBuilder(requireContext(),
                        ChallengeDatabase.class, "challenge-database")
                .build();

        challenges = new ArrayList<>();
        truths = new ArrayList<>();
        dares = new ArrayList<>();

        idUser = getUserId(); // Get Id from shared preferences

        // Check if there is internet connection initially
        if (isConnectedToInternet()) {
            addChallengesInRoom();
        } else {
            showErrorMessage(); // Show error message to user in case of no internet connection
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_game, container, false);
        Button addQuestionsButton = view.findViewById(R.id.goToAddQuestionsButton);

        addQuestionsButton.setOnClickListener(v -> navigateToAddQuestions());
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        adapter = new RecyclerViewAdapter(challenges, challengeDatabase, 2);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        truthButton = view.findViewById(R.id.truthButton);
        dareButton = view.findViewById(R.id.dareButton);
        randomButton = view.findViewById(R.id.randomButton);

        truthButton.setOnClickListener(v -> showChallengeDialog(truths, truthButton));

        dareButton.setOnClickListener(v -> showChallengeDialog(dares, dareButton));

        randomButton.setOnClickListener(v -> showChallengeDialog(challenges, randomButton));
        displayDatabase();
        return view;
    }

    private void setButtonsState(){
        updateButtonEnabledState(truthButton, truths);
        updateButtonEnabledState(dareButton, dares);
        updateButtonEnabledState(randomButton,challenges);
    }


    private void showChallengeDialog(List<Challenge> challenges, Button button) {
        if (!challenges.isEmpty()) {
            Challenge randomChallenge = challenges.get((int) (Math.random() * challenges.size()));
            String type=randomChallenge.getType().toString();

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(type)
                    .setMessage(randomChallenge.getQuestion())
                    .setPositiveButton("Done", (dialog, which) -> {
                        challenges.remove(randomChallenge);

                        if (button != null) {
                            updateButtonEnabledState(button, challenges);
                        }

                        GameFragment.this.challenges.remove(randomChallenge);
                        if (GameFragment.this.challenges.isEmpty()) {
                            showGameEndedDialog();
                        }

                        adapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("Skipped", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        }
    }

    private void showGameEndedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Game Ended")
                .setMessage("No more challenges available.")
                .setPositiveButton("Start Again", (dialog, which) -> {
                    displayDatabase();
                    dialog.dismiss();
                });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_blue_dark);
        dialog.show();
    }

    private void updateButtonEnabledState(Button button, List<Challenge> challenges) {
        requireActivity().runOnUiThread(() -> {
            button.setEnabled(!challenges.isEmpty());
        });
    }


    private void displayDatabase() {
        challenges.clear();
        truths.clear();
        dares.clear();

        new Thread(() -> {
            // Retrieve challenges from the Room database
            challenges.addAll(challengeDatabase.challengeDao().getAllChallengesByUser(idUser));
            truths.addAll(challengeDatabase.challengeDao().getAllTruthChallengesByUser(idUser));
            dares.addAll(challengeDatabase.challengeDao().getAllDareChallengesByUser(idUser));

            // Update the UI on the main thread
            requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
            setButtonsState();
        }).start();

    }


    private String getUserId() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userId", "");
    }

    private void addChallengesInRoom() {
        String userId = getUserId(); // Get user ID from shared preferences

        // Create successListener and errorListener for the request
        Response.Listener<JSONArray> successListener = response -> {
            try {
                List<Challenge> challengeList = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    JSONObject challengeJson = response.getJSONObject(i);
                    String text = challengeJson.getString("text");
                    String typeValue = challengeJson.getString("type");
                    String userId1 = challengeJson.getString("userId");

                    ChallengeType.Type type;
                    if (typeValue.equals("Truth")) {
                        type = ChallengeType.Type.Truth;
                    } else if (typeValue.equals("Dare")) {
                        type = ChallengeType.Type.Dare;
                    } else {
                        continue;
                    }

                    Challenge challenge = new Challenge(text, type, userId1);
                    challengeList.add(challenge);
                }

                // Delete all challenges and insert the new challenge list in a new thread
                new Thread(() -> {
                    challengeDatabase.challengeDao().deleteAllChallenges();
                    challengeDatabase.challengeDao().insertAllChallenges(challengeList);

                }).start();

                challenges.clear();
                challenges.addAll(challengeList);
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        Response.ErrorListener errorListener = error -> {
            // Handle the error
            showErrorMessage();
        };

        // Use the VolleyConfigSingleton to retrieve challenges from the server
        VolleyConfigSingleton volleyConfigSingleton = VolleyConfigSingleton.getInstance(requireContext());
        volleyConfigSingleton.getChallengesFromServer(userId, successListener, errorListener);
    }



    private void navigateToAddQuestions() {
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_gameFragment_to_addQuestionsFragment);
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void showErrorMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Error")
                .setMessage("Unable to connect to the internet for backup. Please check your network connection and try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    if (isConnectedToInternet()) {
                        addChallengesInRoom();
                    } else {
                        showErrorMessage();
                    }
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
