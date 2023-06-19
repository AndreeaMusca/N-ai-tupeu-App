package com.example.n_ai_tupeu.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface ChallengeDao {
    @Insert
    void insertChallenge(Challenge challenge);
    @Update
    void updateChallenge(Challenge challenge);
    @Query("SELECT * FROM Challenge")
    List<Challenge> getAllChallenges();
    @Query("SELECT * FROM Challenge WHERE idUser = :userId")
    List<Challenge> getAllChallengesByUser(int userId);
    @Delete
    void deleteChallenge(Challenge challenge);
    @Query("DELETE FROM Challenge")
    void deleteAllChallenges();
    @Query("SELECT * FROM Challenge WHERE type = 'Dare' AND idUser = :userId")
    List<Challenge> getAllDareChallengesByUser(int userId);
    @Query("SELECT * FROM Challenge WHERE type = 'Truth' AND idUser = :userId")
    List<Challenge> getAllTruthChallengesByUser(int userId);
}
