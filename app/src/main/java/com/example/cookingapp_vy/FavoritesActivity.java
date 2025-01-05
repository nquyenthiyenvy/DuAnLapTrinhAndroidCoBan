package com.example.cookingapp_vy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import com.example.cookingapp_vy.model.Meal;

public class FavoritesActivity extends AppCompatActivity {
    private ListView favoriteListView;
    private DatabaseHelper databaseHelper;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoriteListView = findViewById(R.id.favoriteListView);
        databaseHelper = new DatabaseHelper(this);
        
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        currentUsername = prefs.getString("username", "");

        loadFavorites();
    }

    private void loadFavorites() {
        List<Meal> favoriteMeals = databaseHelper.getFavorites(currentUsername);
        MealAdapter adapter = new MealAdapter(this, favoriteMeals, databaseHelper, currentUsername);
        favoriteListView.setAdapter(adapter);
    }
} 