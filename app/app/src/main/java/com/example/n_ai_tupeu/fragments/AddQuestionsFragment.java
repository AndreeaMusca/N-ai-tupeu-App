package com.example.n_ai_tupeu.fragments;

import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.n_ai_tupeu.R;
import com.example.n_ai_tupeu.RecyclerViewAdapter;
import com.example.n_ai_tupeu.database.Challenge;
import com.example.n_ai_tupeu.database.ChallengeDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddQuestionsFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {
    private Button mButton;
    private EditText t1;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private List<Challenge> challenges;
    private ChallengeDatabase challengeDatabase;
    RadioGroup radioGroup;
    String t2="";

    private int idUser=37;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the AnimalDatabase
        challengeDatabase = Room.databaseBuilder(requireContext(),
                        ChallengeDatabase.class, "challenge-database")
                .build();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_questions, container, false);
        Button backToGameButton = view.findViewById(R.id.backToGameButton);

        backToGameButton.setOnClickListener(v -> navigateToGame());

        mButton = view.findViewById(R.id.addButton);
        t1 = view.findViewById(R.id.text1);
        recyclerView = view.findViewById(R.id.recycler_view);
        radioGroup=(RadioGroup) view.findViewById(R.id.challenge_group);
        radioGroup.setOnCheckedChangeListener(this);

        challenges = new ArrayList<>();
        adapter = new RecyclerViewAdapter(challenges, challengeDatabase, 1);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        displayDatabase(); // Load data from Room

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (t1.getText().toString().trim().isEmpty() || t2=="") {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setMessage("Unul dintre fielduri e gol.");
                    builder.setTitle("Alert !");
                    builder.setCancelable(false);
                    builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    String text1 = t1.getText().toString();
                    Challenge a = new Challenge(text1, t2, idUser);
                    if (existChallenge(a)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setMessage("Chellenge already exist.");
                        builder.setTitle("Alert !");
                        builder.setCancelable(false);
                        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        t1.setText("");
                    } else {
                        challenges.add(a);
                        insertChallenge(a);
                    }

                    adapter.notifyDataSetChanged();
                    t1.setText("");
                    radioGroup.clearCheck();
                 }
            }
        });

        Button deleteAllButton = view.findViewById(R.id.deleteAllButton);
        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeleteAllChallenges();
            }
        });
        return view;
    }

    private void confirmDeleteAllChallenges() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Are you sure you want to delete all challenges?");
        builder.setTitle("Confirm Delete");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAllChallenges();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteAllChallenges() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                challengeDatabase.challengeDao().deleteAllChallenges();
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        challenges.clear();
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }
    private void insertChallenge(Challenge challenge) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                challengeDatabase.challengeDao().insertChallenge(challenge);
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayDatabase(); // Refresh the data after insertion
                    }
                });
            }
        }).start();
    }
    private boolean existChallenge(Challenge challenge) {
        for (Challenge obj : challenges) {
            if (obj.getChallenge().equalsIgnoreCase(challenge.getChallenge())) {
                return true;
            }
        }
        return false;
    }
    private void displayDatabase() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                challenges.clear();
                challenges.addAll(challengeDatabase.challengeDao().getAllChallengesByUser(idUser));
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }
    private void navigateToGame() {
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_addQuestionsFragment_to_gameFragment);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId)
        {
            case R.id.radioButton_dare:
                t2="Dare";
                break;

            case R.id.radioButton_truth:
                t2="Truth";
                break;
        }
    }
}