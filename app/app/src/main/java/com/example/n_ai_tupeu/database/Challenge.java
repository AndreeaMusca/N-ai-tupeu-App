package com.example.n_ai_tupeu.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.UUID;

import java.util.Objects;

@Entity(tableName = "challenge")
public class Challenge {

    @PrimaryKey
    @NonNull
    private UUID id;
    @NonNull
    private String question;

    @ColumnInfo(name = "type")
    private ChallengeType.Type type;

    @ColumnInfo(name = "idUser")
    private String idUser;

    public Challenge(@NonNull String question, ChallengeType.Type type, String idUser) {
        id=UUID.randomUUID();
        this.question = question;
        this.type = type;
        this.idUser = idUser;
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
        return Objects.equals(idUser, other.idUser) && question.equals(other.question) && type.equals(other.type);
    }


    @NonNull
    public String getQuestion() {
        return question;
    }

    public void setQuestion(@NonNull String question) {
        this.question = question;
    }

    public ChallengeType.Type getType() {
        return type;
    }

    public void setType(ChallengeType.Type type) {
        this.type = type;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    @NonNull
    public UUID getId() {return id;}

    public void setId(@NonNull UUID id) {
        this.id = id;
    }
}
