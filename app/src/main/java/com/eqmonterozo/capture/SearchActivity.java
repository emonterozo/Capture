package com.eqmonterozo.capture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    RecyclerView recViewPlaces;
    PlaceAdapter placeAdapter;

    ImageView imgLoading;
    AnimationDrawable loadingAnimation;
    ConstraintLayout layoutLoading, layoutAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        imgLoading = findViewById(R.id.imgLoading);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutAdd = findViewById(R.id.layoutAdd);

        recViewPlaces = findViewById(R.id.recViewPlaces);
        getPlaces();

        getSupportActionBar().setTitle(getResources().getString(R.string.places));

    }

    public void getPlaces() {
        imgLoading.setBackgroundResource(R.drawable.loading);
        loadingAnimation = (AnimationDrawable) imgLoading.getBackground();
        loadingAnimation.start();
        layoutLoading.setVisibility(View.VISIBLE);

        SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        String json = sharedPreferences.getString(Constant.SHARED_PREFERENCES_USER, Constant.DEFAULT_VALUE);
        Gson gson = new Gson();
        User obj = gson.fromJson(json, User.class);

        RequestQueue queue = Volley.newRequestQueue(SearchActivity.this);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.SERVER_URL + Constant.API_GET_PLACES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JsonArray outputJsonArray = JsonParser.parseString(response).getAsJsonArray();
                ArrayList images = new ArrayList();
                ArrayList placeName = new ArrayList();
                ArrayList description = new ArrayList();
                ArrayList latitude = new ArrayList();
                ArrayList longitude = new ArrayList();
                ArrayList address = new ArrayList();
                ArrayList timestamp = new ArrayList();
                ArrayList addedBy = new ArrayList();


                for (int counter = 0; counter < outputJsonArray.size(); counter++) {
                    JsonObject jsonObject = outputJsonArray.get(counter).getAsJsonObject();

                    images.add(jsonObject.get("images"));
                    placeName.add(jsonObject.get("place_name").getAsString());
                    description.add(jsonObject.get("description").getAsString());
                    latitude.add(jsonObject.get("latitude").getAsString());
                    longitude.add(jsonObject.get("longitude").getAsString());
                    address.add(jsonObject.get("address").getAsString());
                    timestamp.add(jsonObject.get("timestamp").getAsString());
                    addedBy.add(jsonObject.get("added_by").getAsString());
                }

                placeAdapter = new PlaceAdapter(images, placeName, description, latitude, longitude, address, timestamp, addedBy, true);
                recViewPlaces.setAdapter(placeAdapter);
                recViewPlaces.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

                loadingAnimation.stop();
                layoutLoading.setVisibility(View.INVISIBLE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + obj.getToken());

                return headers;
            }
        };
        queue.add(request);
    }
}