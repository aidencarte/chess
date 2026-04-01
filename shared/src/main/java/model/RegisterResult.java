package model;

import com.google.gson.Gson;

public record RegisterResult(String username, String authToken) {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}