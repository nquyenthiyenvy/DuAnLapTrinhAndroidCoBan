package com.example.cookingapp_vy.model;

import com.google.gson.annotations.SerializedName;

public class Meal {

    @SerializedName("strMeal")
    private String strMeal;

    @SerializedName("strMealThumb")
    private String strMealThumb;

    @SerializedName("idMeal")
    private String idMeal;

    @SerializedName("strInstructions")
    private String strInstructions;

    @SerializedName("strIngredient1")
    private String strIngredient1;

    @SerializedName("strIngredient2")
    private String strIngredient2;

    @SerializedName("strMeasure1")
    private String strMeasure1;

    @SerializedName("strMeasure2")
    private String strMeasure2;

    @SerializedName("strIngredient3")
    private String strIngredient3;

    @SerializedName("strMeasure3")
    private String strMeasure3;

    public String getStrMeal() {
        return strMeal;
    }

    public String getStrMealThumb() {
        return strMealThumb;
    }

    public String getIdMeal() {
        return idMeal;
    }

    public String getStrInstructions() {
        return strInstructions;
    }

    public String getStrIngredient1() {
        return strIngredient1;
    }

    public String getStrMeasure1() {
        return strMeasure1;
    }

    public String getStrIngredient2() {
        return strIngredient2;
    }

    public void setStrIngredient2(String strIngredient2) {
        this.strIngredient2 = strIngredient2;
    }

    public String getStrMeasure2() {
        return strMeasure2;
    }

    public void setStrMeasure2(String strMeasure2) {
        this.strMeasure2 = strMeasure2;
    }

    public void setStrMeal(String strMeal) {
        this.strMeal = strMeal;
    }

    public void setStrMealThumb(String strMealThumb) {
        this.strMealThumb = strMealThumb;
    }

    public void setIdMeal(String idMeal) {
        this.idMeal = idMeal;
    }

    public void setStrInstructions(String strInstructions) {
        this.strInstructions = strInstructions;
    }

    public void setStrIngredient1(String strIngredient1) {
        this.strIngredient1 = strIngredient1;
    }

    public void setStrMeasure1(String strMeasure1) {
        this.strMeasure1 = strMeasure1;
    }

    public String getStrIngredient3() {
        return strIngredient3;
    }

    public String getStrMeasure3() {
        return strMeasure3;
    }
}
