package com.example.cookingapp_vy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cookingapp_vy.model.Meal;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MealAdapter extends BaseAdapter {

    private Context context;
    private List<Meal> meals;
    private DatabaseHelper databaseHelper;
    private String currentUsername;

    public MealAdapter(Context context, List<Meal> meals, DatabaseHelper databaseHelper, String currentUsername) {
        this.context = context;
        this.meals = meals;
        this.databaseHelper = databaseHelper;
        this.currentUsername = currentUsername;
    }

    @Override
    public int getCount() {
        return meals.size();
    }

    @Override
    public Object getItem(int position) {
        return meals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_meal, parent, false);
        }

        Meal meal = meals.get(position);

        TextView mealName = convertView.findViewById(R.id.mealName);
        ImageView mealImage = convertView.findViewById(R.id.mealImage);
        ImageView favoriteButton = convertView.findViewById(R.id.favoriteButton);

        mealName.setText(meal.getStrMeal());

        //Picasso
        Picasso.get().load(meal.getStrMealThumb()).into(mealImage);

        if (databaseHelper.isFavorite(currentUsername, meal.getIdMeal())) {
            favoriteButton.setImageResource(R.drawable.ic_favorite);
        } else {
            favoriteButton.setImageResource(R.drawable.ic_favorite_border);
        }

        favoriteButton.setOnClickListener(v -> {
            if (databaseHelper.isFavorite(currentUsername, meal.getIdMeal())) {

                databaseHelper.removeFavorite(currentUsername, meal.getIdMeal());
                favoriteButton.setImageResource(R.drawable.ic_favorite_border);
                Toast.makeText(context, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
            } else {

                databaseHelper.addFavorite(currentUsername, meal.getIdMeal(),
                    meal.getStrMeal(), meal.getStrMealThumb());
                favoriteButton.setImageResource(R.drawable.ic_favorite);
                Toast.makeText(context, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
            }
        });

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MealDetailActivity.class);
            intent.putExtra("MEAL_ID", meal.getIdMeal());
            context.startActivity(intent);
        });

        return convertView;
    }
}
