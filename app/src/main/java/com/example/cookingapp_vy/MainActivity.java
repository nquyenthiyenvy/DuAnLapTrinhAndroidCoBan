package com.example.cookingapp_vy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cookingapp_vy.model.MealResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText searchInput;
    private ImageView searchIcon, loginIcon;
    private ListView recipeListView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseHelper = new DatabaseHelper(this);
        AddControls();
        AddEvents();

        loadDefaultMeals();
    }

    private void AddControls() {
        searchInput = findViewById(R.id.searchInput);
        searchIcon = findViewById(R.id.searchIcon);
        loginIcon = findViewById(R.id.loginIcon);
        recipeListView = findViewById(R.id.recipeListView);
    }

    private void AddEvents() {
        searchIcon.setOnClickListener(v -> {
            Log.d("API_DEBUG", "Đã click nút tìm kiếm");  // Thêm dòng này
            String query = searchInput.getText().toString().trim();
            Log.d("API_DEBUG", "Query: " + query);  // Thêm dòng này
            if (!query.isEmpty()) {
                searchMeals(query);
            } else {
                Toast.makeText(MainActivity.this, "Vui lòng nhập từ khóa tìm kiếm!", Toast.LENGTH_SHORT).show();
            }
        });

        loginIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DangNhap.class);
            startActivity(intent);
        });
    }

    //API
    private void searchMeals(String mealName) {
        Log.d("API_DEBUG", "Bắt đầu tìm kiếm với từ khóa: " + mealName);

        MealService apiService = RetrofitInstance.getRetrofitInstance().create(MealService.class);
        Call<MealResponse> call = apiService.searchMeals(mealName);

        Log.d("API_DEBUG", "URL Request: " + call.request().url());


        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                Log.d("API_DEBUG", "Nhận được response code: " + response.code());

                if (response.isSuccessful()) {
                    MealResponse mealResponse = response.body();
                    Log.d("API_DEBUG", "Response body: " + (mealResponse != null ? "not null" : "null"));

                    if (mealResponse != null && mealResponse.getMeals() != null) {
                        Log.d("API_DEBUG", "Số lượng món ăn tìm thấy: " + mealResponse.getMeals().size());
                        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                        String username = prefs.getString("username", "");
                        MealAdapter mealAdapter = new MealAdapter(MainActivity.this, mealResponse.getMeals(), databaseHelper, username);
                        recipeListView.setAdapter(mealAdapter);
                    } else {
                        Toast.makeText(MainActivity.this, "Không tìm thấy món ăn nào!", Toast.LENGTH_SHORT).show();
                        Log.d("API_DEBUG", "Response successful but no meals found");
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("API_DEBUG", "Error response: " + errorBody);
                        Toast.makeText(MainActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e("API_DEBUG", "Lỗi khi đọc error body", e);
                    }
                }
            }


            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                Log.e("API_DEBUG", "API call failed", t);
                t.printStackTrace();
                Toast.makeText(MainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void loadDefaultMeals() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Vui lòng kiểm tra kết nối internet", Toast.LENGTH_LONG).show();
            Log.e("API_DEBUG", "Không có kết nối internet");
            return;
        }

        Log.d("API_DEBUG", "Bắt đầu tải danh sách món ăn mặc định");
        
        try {
            MealService apiService = RetrofitInstance.getRetrofitInstance().create(MealService.class);
            Call<MealResponse> call = apiService.searchMeals("chicken");
            
            // Log URL request
            Log.d("API_DEBUG", "URL Request: " + call.request().url());
            Log.d("API_DEBUG", "Headers: " + call.request().headers());

            call.enqueue(new Callback<MealResponse>() {
                @Override
                public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                    Log.d("API_DEBUG", "Response code: " + response.code());
                    
                    if (response.isSuccessful()) {
                        MealResponse mealResponse = response.body();
                        Log.d("API_DEBUG", "Response body: " + (mealResponse != null ? "not null" : "null"));
                        
                        if (mealResponse != null && mealResponse.getMeals() != null) {
                            SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                            String username = prefs.getString("username", "");
                            MealAdapter mealAdapter = new MealAdapter(MainActivity.this, mealResponse.getMeals(), databaseHelper, username);
                            recipeListView.setAdapter(mealAdapter);
                        } else {
                            Log.e("API_DEBUG", "Response body hoặc meals là null");
                            Toast.makeText(MainActivity.this, 
                                "Không tìm thấy món ăn nào", 
                                Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("API_DEBUG", "Error response: " + errorBody);
                            Log.e("API_DEBUG", "Error code: " + response.code());
                            Log.e("API_DEBUG", "Error message: " + response.message());
                        } catch (IOException e) {
                            Log.e("API_DEBUG", "Error reading error body", e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<MealResponse> call, Throwable t) {
                    Log.e("API_DEBUG", "API call failed", t);
                    Log.e("API_DEBUG", "Error message: " + t.getMessage());
                    Log.e("API_DEBUG", "Stack trace: ", t);
                    Toast.makeText(MainActivity.this,
                            "Lỗi kết nối: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("API_DEBUG", "Exception when creating API call", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorites) {

            SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            String username = prefs.getString("username", "");
            if (username.isEmpty()) {
                Toast.makeText(this, "Vui lòng đăng nhập để xem danh sách yêu thích", Toast.LENGTH_SHORT).show();
                return true;
            }
            
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
