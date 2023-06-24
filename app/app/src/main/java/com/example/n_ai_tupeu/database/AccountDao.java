package com.example.n_ai_tupeu.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface AccountDao {
    @Insert
    void insertAccount(Account account);

    @Query("SELECT * FROM account WHERE username=:username AND password=:password")
    Account getAccountByUsernameAndPassword(String username, String password);
}
