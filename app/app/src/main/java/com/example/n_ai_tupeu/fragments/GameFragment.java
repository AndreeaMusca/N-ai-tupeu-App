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
import com.example.n_ai_tupeu.R;
import com.example.n_ai_tupeu.RecyclerViewAdapter;
import com.example.n_ai_tupeu.database.Challenge;
import com.example.n_ai_tupeu.database.ChallengeDatabase;
import java.util.ArrayList;
import java.util.List;

public class GameFragment extends Fragment {
    private EditText t1;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private List<Challenge> lista;
    private List<Challenge> truths;
    private List<Challenge> dares;
    private ChallengeDatabase animalDatabase;
    String t2="";
    private int idUser=37;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animalDatabase = Room.databaseBuilder(requireContext(),
                        ChallengeDatabase.class, "challenge-database")
                .build();

        lista = new ArrayList<>();
        truths = new ArrayList<>();
        dares = new ArrayList<>();

        displayDatabase(); // Load data from Room
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
        recyclerView = view.findViewById(R.id.recycler_view);



        adapter = new RecyclerViewAdapter(lista, animalDatabase,2);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        Button truthButton = view.findViewById(R.id.truthButton);
        Button dareButton = view.findViewById(R.id.dareButton);
        Button randomButton = view.findViewById(R.id.randomButton);

        if(truths.isEmpty())
            truthButton.setEnabled(false);
        if(dares.isEmpty())
            dareButton.setEnabled(false);
        if(lista.isEmpty())
            randomButton.setEnabled(false);

        truthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!truths.isEmpty()) {
                    Challenge randomChallenge = truths.get((int) (Math.random() * truths.size()));

                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Truth")
                            .setMessage(randomChallenge.getChallenge())
                            .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    truths.remove(randomChallenge); // Remove the random challenge from the truths list

                                    if (truths.isEmpty()) {
                                        truthButton.setEnabled(false);
                                    }

                                    lista.remove(randomChallenge);
                                    if (lista.isEmpty()) {
                                        showGameEndedDialog();
                                        dareButton.setEnabled(true);
                                        truthButton.setEnabled(true);
                                        randomButton.setEnabled(true);
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("Skipped", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Close the dialog
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                }
            }
        });

        dareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dares.isEmpty()) {
                    Challenge randomChallenge = dares.get((int) (Math.random() * dares.size()));

                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Dare")
                            .setMessage(randomChallenge.getChallenge())
                            .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dares.remove(randomChallenge); // Remove the random challenge from the truths list

                                    if (dares.isEmpty()) {
                                        dareButton.setEnabled(false);
                                    }

                                    lista.remove(randomChallenge);
                                    if (lista.isEmpty()) {
                                        showGameEndedDialog();
                                        dareButton.setEnabled(true);
                                        truthButton.setEnabled(true);
                                        randomButton.setEnabled(true);
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("Skipped", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Close the dialog
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                }
            }
        });

        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Challenge randomChallenge = lista.get((int) (Math.random() * lista.size()));

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(randomChallenge.getType())
                    .setMessage(randomChallenge.getChallenge())
                    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(randomChallenge.getType().equals("Dare"))
                            {
                                dares.remove(randomChallenge);
                                if (dares.isEmpty()) {
                                    dareButton.setEnabled(false);
                                }
                            }
                            else
                            {
                                truths.remove(randomChallenge);
                                if (truths.isEmpty()) {
                                    truthButton.setEnabled(false);
                                }
                            }

                            lista.remove(randomChallenge);
                            if (lista.isEmpty()) {
                                showGameEndedDialog();
                                dareButton.setEnabled(true);
                                truthButton.setEnabled(true);
                                randomButton.setEnabled(true);
                            }

                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("Skipped", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Close the dialog
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }
        });
        return view;
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

    private void displayDatabase() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                lista.clear();
                truths.clear();
                dares.clear();
                lista.addAll(animalDatabase.challengeDao().getAllChallengesByUser(idUser));
                truths.addAll(animalDatabase.challengeDao().getAllTruthChallengesByUser(idUser));
                dares.addAll(animalDatabase.challengeDao().getAllDareChallengesByUser(idUser));
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }
    private void navigateToAddQuestions() {
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_gameFragment_to_addQuestionsFragment);
    }

}