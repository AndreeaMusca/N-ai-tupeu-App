package com.example.n_ai_tupeu.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "challenge")
public class Challenge {
    @PrimaryKey
    @NonNull
    private String challenge;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "idUser")
    private int idUser;

    public Challenge(String challenge, String type, int idUser) {
        this.challenge = challenge;
        this.type = type;
        this.idUser=idUser;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Challenge other = (Challenge) obj;
        // Compare the properties of the challenges that make them equal
        return idUser == other.idUser && challenge.equals(other.challenge) && type.equals(other.type);
    }
    @NonNull
    public String getChallenge() {
        return challenge;
    }
    public void setChallenge(@NonNull String challenge) {
        this.challenge = challenge;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public int getIdUser() {
        return idUser;
    }
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
}
