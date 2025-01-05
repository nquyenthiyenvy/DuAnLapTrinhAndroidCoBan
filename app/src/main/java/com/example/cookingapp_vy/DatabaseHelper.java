package com.example.cookingapp_vy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cookingapp_vy.model.Meal;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CookingApp.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    private static final String TABLE_FAVORITES = "favorites";
    private static final String COLUMN_MEAL_ID = "meal_id";
    private static final String COLUMN_MEAL_NAME = "meal_name";
    private static final String COLUMN_MEAL_IMAGE = "meal_image";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USERNAME + " TEXT PRIMARY KEY,"
                + COLUMN_PASSWORD + " TEXT"
                + ")";
        db.execSQL(createUsersTable);

        String createFavoritesTable = "CREATE TABLE " + TABLE_FAVORITES + "("
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_MEAL_ID + " TEXT,"
                + COLUMN_MEAL_NAME + " TEXT,"
                + COLUMN_MEAL_IMAGE + " TEXT,"
                + "PRIMARY KEY (" + COLUMN_USERNAME + ", " + COLUMN_MEAL_ID + ")"
                + ")";
        db.execSQL(createFavoritesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        onCreate(db);
    }

    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USERNAME};
        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        
        return count > 0;
    }

    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USERNAME};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};
        
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        
        return count > 0;
    }

    // yêu thích
    public List<Meal> getFavorites(String username) {
        List<Meal> favorites = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + TABLE_FAVORITES + 
                      " WHERE " + COLUMN_USERNAME + " = ?";
                      
        Cursor cursor = db.rawQuery(query, new String[]{username});
        
        if (cursor.moveToFirst()) {
            do {
                Meal meal = new Meal();
                meal.setIdMeal(cursor.getString(cursor.getColumnIndex(COLUMN_MEAL_ID)));
                meal.setStrMeal(cursor.getString(cursor.getColumnIndex(COLUMN_MEAL_NAME)));
                meal.setStrMealThumb(cursor.getString(cursor.getColumnIndex(COLUMN_MEAL_IMAGE)));
                favorites.add(meal);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return favorites;
    }

    public boolean isFavorite(String username, String mealId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_FAVORITES + 
                      " WHERE " + COLUMN_USERNAME + " = ? AND " + 
                      COLUMN_MEAL_ID + " = ?";
                      
        Cursor cursor = db.rawQuery(query, new String[]{username, mealId});
        boolean isFavorite = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isFavorite;
    }

    public void addFavorite(String username, String mealId, String mealName, String mealImage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_MEAL_ID, mealId);
        values.put(COLUMN_MEAL_NAME, mealName);
        values.put(COLUMN_MEAL_IMAGE, mealImage);
        
        db.insert(TABLE_FAVORITES, null, values);
        db.close();
    }

    public void removeFavorite(String username, String mealId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITES, 
                 COLUMN_USERNAME + " = ? AND " + COLUMN_MEAL_ID + " = ?",
                 new String[]{username, mealId});
        db.close();
    }
} 