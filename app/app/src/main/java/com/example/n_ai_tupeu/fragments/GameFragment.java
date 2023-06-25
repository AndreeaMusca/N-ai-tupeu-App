package com.example.n_ai_tupeu.fragments;

import android.content.Context;
import android.content.DialogInterface;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.n_ai_tupeu.R;
import com.example.n_ai_tupeu.RecyclerViewAdapter;
import com.example.n_ai_tupeu.database.Challenge;
import com.example.n_ai_tupeu.database.ChallengeDatabase;
import com.example.n_ai_tupeu.database.ChallengeType;
import com.example.n_ai_tupeu.helpers.Constants;

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

        // Verifică dacă există conexiune la internet în momentul inițial
        if (isConnectedToInternet()) {
            addChallengesInRoom();
        } else {
            showErrorMessage(); // Afișează mesajul de eroare utilizatorului în caz de lipsă de conexiune la internet
            displayDatabase();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_game, container, false);
        Button addQuestionsButton = view.findViewById(R.id.goToAddQuestionsButton);

        addQuestionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAddQuestions();
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        adapter = new RecyclerViewAdapter(challenges, challengeDatabase, 2);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        Button truthButton = view.findViewById(R.id.truthButton);
        Button dareButton = view.findViewById(R.id.dareButton);
        Button randomButton = view.findViewById(R.id.randomButton);

        updateButtonEnabledState(truthButton, truths);
        updateButtonEnabledState(dareButton, dares);
        randomButton.setEnabled(!challenges.isEmpty());

        truthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChallengeDialog("Truth", truths, truthButton);
            }
        });

        dareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChallengeDialog("Dare", dares, dareButton);
            }
        });

        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChallengeDialog("", challenges, randomButton);
            }
        });

        return view;
    }

    private void showChallengeDialog(String type, List<Challenge> challenges, Button button) {
        if (!challenges.isEmpty()) {
            Challenge randomChallenge = challenges.get((int) (Math.random() * challenges.size()));

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(type)
                    .setMessage(randomChallenge.getQuestion())
                    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            challenges.remove(randomChallenge);

                            if (button != null) {
                                updateButtonEnabledState(button, challenges);
                            }

                            GameFragment.this.challenges.remove(randomChallenge);
                            if (GameFragment.this.challenges.isEmpty()) {
                                showGameEndedDialog();
                            }

                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("Skipped", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }
    }

    private void showGameEndedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Game Ended")
                .setMessage("No more challenges available.")
                .setPositiveButton("Start Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        displayDatabase();
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_blue_dark);
        dialog.show();
    }

    private void updateButtonEnabledState(Button button, List<Challenge> challenges) {
        button.setEnabled(!challenges.isEmpty());
    }

    private void displayDatabase() {
        challenges.clear();
        truths.clear();
        dares.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Retrieve challenges from the Room database
                challenges.addAll(challengeDatabase.challengeDao().getAllChallengesByUser(idUser));
                truths.addAll(challengeDatabase.challengeDao().getAllTruthChallengesByUser(idUser));
                dares.addAll(challengeDatabase.challengeDao().getAllDareChallengesByUser(idUser));

                // Update the UI on the main thread
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }


    private String getUserId() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userId", "");
    }

    private void addChallengesInRoom() {
        // Realizează cererea către server pentru a obține datele de la server și actualizează baza de date locală
        String url = Constants.BASE_URL + Constants.CHALLENGES_ENDPOINT + idUser;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            List<Challenge> challengeList = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject challengeJson = response.getJSONObject(i);
                                String text = challengeJson.getString("text");
                                int typeValue = challengeJson.getInt("type"); 
                                String userId = challengeJson.getString("userId");

                                ChallengeType.Type type;
                                if (typeValue == 0) {
                                    type = ChallengeType.Type.Truth;
                                } else if (typeValue == 1) {
                                    type = ChallengeType.Type.Dare;
                                } else {
                                    continue;
                                }

                                Challenge challenge = new Challenge(text, type, userId);
                                challengeList.add(challenge);
                            }

                            // Delete all challenges and insert the new challenge list in a new thread
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    challengeDatabase.challengeDao().deleteAllChallenges();
                                    challengeDatabase.challengeDao().insertAllChallenges(challengeList);

                                }
                            }).start();

                            challenges.clear();
                            challenges.addAll(challengeList);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        showErrorMessage();
                    }
                });

        // Add the request to the request queue
        Volley.newRequestQueue(requireContext()).add(request);
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
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isConnectedToInternet()) {
                            addChallengesInRoom();
                        } else {
                            showErrorMessage();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
