package com.example.n_ai_tupeu.database;

public class ChallengeType {
    public enum Type {
        Dare(1),
        Truth(0);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


}
