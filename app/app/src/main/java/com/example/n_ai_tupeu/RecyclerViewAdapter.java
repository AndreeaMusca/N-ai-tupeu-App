package com.example.n_ai_tupeu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.n_ai_tupeu.database.Challenge;
import com.example.n_ai_tupeu.database.ChallengeDatabase;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Challenge> dataList;
    private ChallengeDatabase challengeDatabase;
    int page;

    public RecyclerViewAdapter(List<Challenge> dataList, ChallengeDatabase challengeDatabase, int page) {
        this.dataList = dataList;
        this.challengeDatabase = challengeDatabase;
        this.page = page;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String data = dataList.get(position).getQuestion();
        holder.type.setText(data);
        data = dataList.get(position).getType();
        holder.challenge.setText(data);

        if (data.equalsIgnoreCase("Dare")) {
            holder.itemView.setBackgroundResource(R.drawable.gradient_background_dare);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.gradient_background_truth);
        }

        if (page == 1) {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = holder.getAdapterPosition();
                    Challenge animalToDelete = dataList.get(adapterPosition);
                    deleteAnimal(animalToDelete);
                    dataList.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);
                }
            });
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
    }

    private void deleteAnimal(Challenge challenge) {
        new Thread(() -> {
            challengeDatabase.challengeDao().deleteChallenge(challenge);
        }).start();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView type;
        TextView challenge;
        ImageButton imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.type);
            challenge = itemView.findViewById(R.id.challenge);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
