package com.example.cookingapp_vy;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cookingapp_vy.model.MealResponse;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealDetailActivity extends AppCompatActivity {
    private ImageView mealImage;
    private TextView mealName, mealInstructions, mealIngredients;
    private Button btnSpeak;
    private TextToSpeech textToSpeech;
    private boolean isSpeaking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_detail);

        // TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(new Locale("vi"));
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });

        mealImage = findViewById(R.id.detailMealImage);
        mealName = findViewById(R.id.detailMealName);
        mealInstructions = findViewById(R.id.detailMealInstructions);
        mealIngredients = findViewById(R.id.detailMealIngredients);
        btnSpeak = findViewById(R.id.btnSpeak);

        btnSpeak.setOnClickListener(v -> {
            if (!isSpeaking) {
                speak();
                btnSpeak.setText("Dừng đọc");
            } else {
                stopSpeaking();
                btnSpeak.setText("Đọc công thức");
            }
            isSpeaking = !isSpeaking;
        });

        String mealId = getIntent().getStringExtra("MEAL_ID");
        if (mealId != null) {
            loadMealDetails(mealId);
        }
    }

    private void loadMealDetails(String mealId) {
        Log.d("API_DEBUG", "Loading meal details for ID: " + mealId);
        
        MealService apiService = RetrofitInstance.getRetrofitInstance().create(MealService.class);
        Call<MealResponse> call = apiService.getMealById(mealId);

        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                Log.d("API_DEBUG", "Got response code: " + response.code());
                if (response.isSuccessful() && response.body() != null
                        && response.body().getMeals() != null
                        && !response.body().getMeals().isEmpty()) {

                    com.example.cookingapp_vy.model.Meal meal = response.body().getMeals().get(0);

                    mealName.setText(meal.getStrMeal());

                    String instructions = meal.getStrInstructions();
                    instructions = instructions.replace(". ", ".\n\n");
                    mealInstructions.setText(instructions);

                    Picasso.get().load(meal.getStrMealThumb()).into(mealImage);

                    StringBuilder ingredients = new StringBuilder();
                    ingredients.append("Nguyên liệu cần có:\n\n");

                    for (int i = 1; i <= 20; i++) {
                        String ingredient = null;
                        String measure = null;
                        
                        switch(i) {
                            case 1:
                                ingredient = meal.getStrIngredient1();
                                measure = meal.getStrMeasure1();
                                break;
                            case 2:
                                ingredient = meal.getStrIngredient2();
                                measure = meal.getStrMeasure2();
                                break;
                            case 3:
                                ingredient = meal.getStrIngredient3();
                                measure = meal.getStrMeasure3();
                                break;
                        }
                        
                        if (ingredient != null && !ingredient.isEmpty()) {
                            ingredients.append("• ").append(ingredient)
                                    .append(": ").append(measure).append("\n");
                        }
                    }

                    mealIngredients.setText(ingredients.toString());

                } else {
                    Toast.makeText(MealDetailActivity.this,
                            "Không thể tải thông tin món ăn",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                Log.e("API_DEBUG", "API call failed", t);
                Toast.makeText(MealDetailActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void speak() {
        if (textToSpeech != null) {
            String textToRead = mealName.getText().toString() + ". " +
                    mealIngredients.getText().toString() + ". " +
                    mealInstructions.getText().toString();

            int result = textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null);
            if (result == TextToSpeech.ERROR) {
                Toast.makeText(this, "Lỗi khi đọc văn bản", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "TextToSpeech chưa sẵn sàng", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopSpeaking() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    private boolean isTextToSpeechReady() {
        return textToSpeech != null && !isSpeaking;
    }
} 