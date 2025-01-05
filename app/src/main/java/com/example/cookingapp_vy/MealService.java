package com.example.cookingapp_vy;

import com.example.cookingapp_vy.model.MealResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealService {
    @GET("search.php")
    Call<MealResponse> searchMeals(@Query("s") String query);

    @GET("lookup.php")
    Call<MealResponse> getMealById(@Query("i") String id);
}