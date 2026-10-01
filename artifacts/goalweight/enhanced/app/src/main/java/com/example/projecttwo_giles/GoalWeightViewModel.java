package com.example.projecttwo_giles;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import android.database.Cursor;

public class GoalWeightViewModel extends AndroidViewModel {

    private final GoalWeightRepository repository;

    public GoalWeightViewModel(@NonNull Application application) {
        super(application);
        repository = new GoalWeightRepository(application);
    }

    public boolean usernameExists(String username) {
        return repository.usernameExists(username);
    }

    public long createUser(String username, String password) {
        return repository.createUser(username, password);
    }

    public int authenticateUser(String username, String password) {
        return repository.authenticateUser(username, password);
    }

    public Cursor getWeightsForUser(int userId) {
        return repository.getWeightsForUser(userId);
    }

    public boolean addWeight(int userId, String date, float weight) {
        return repository.addWeight(userId, date, weight);
    }

    public boolean deleteWeight(int id) {
        return repository.deleteWeight(id);
    }

    public Cursor getUserSmsSettings(int userId) {
        return repository.getUserSmsSettings(userId);
    }

    public boolean updateUserSmsSettings(int userId, String phone, Float goalWeight) {
        return repository.updateUserSmsSettings(userId, phone, goalWeight);
    }
}