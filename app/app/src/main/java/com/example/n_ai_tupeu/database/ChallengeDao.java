package com.example.n_ai_tupeu.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.UUID;

@Dao
public interface ChallengeDao {
    @Insert
    void insertChallenge(Challenge challenge);
    @Insert
    void insertAllChallenges(List<Challenge> challenges);

    @Update
    void updateChallenge(Challenge challenge);

    @Query("SELECT * FROM Challenge")
    List<Challenge> getAllChallenges();

    @Query("SELECT * FROM Challenge WHERE idUser = :userId")
    List<Challenge> getAllChallengesByUser(String userId);

    @Delete
    void deleteChallenge(Challenge challenge);

    @Query("DELETE FROM Challenge")
    void deleteAllChallenges();

    @Query("SELECT * FROM Challenge WHERE type ='2' AND idUser = :userId")
    List<Challenge> getAllDareChallengesByUser(String userId);

    @Query("SELECT * FROM Challenge WHERE type = '1' AND idUser = :userId")
    List<Challenge> getAllTruthChallengesByUser(String userId);
}
