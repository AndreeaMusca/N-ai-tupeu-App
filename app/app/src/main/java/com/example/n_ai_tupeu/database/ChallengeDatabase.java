package com.example.n_ai_tupeu.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Challenge.class}, version = 3)
public abstract class ChallengeDatabase extends RoomDatabase {
    public abstract ChallengeDao challengeDao();
}
