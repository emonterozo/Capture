package com.eqmonterozo.capture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {
    RecyclerView recViewPlaces;
    PlaceAdapter placeAdapter;

    DatabaseHelper databaseHelper;

    ArrayList images = new ArrayList();
    ArrayList placeName = new ArrayList();
    ArrayList description = new ArrayList();
    ArrayList latitude = new ArrayList();
    ArrayList longitude = new ArrayList();
    ArrayList address = new ArrayList();
    ArrayList timestamp = new ArrayList();
    ArrayList addedBy = new ArrayList();

    FirebaseStorage storage;
    StorageReference storageReference;

    ImageView imgLoading;
    AnimationDrawable loadingAnimation;
    ConstraintLayout layoutLoading, layoutUpload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        recViewPlaces = findViewById(R.id.recViewPlaces);

        databaseHelper = new DatabaseHelper(this);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        imgLoading = findViewById(R.id.imgLoading);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutUpload = findViewById(R.id.layoutUpload);

        Cursor cursor = databaseHelper.getPlaces();
        if (cursor.getCount() == 0) {
            Snackbar.make(layoutUpload, getResources().getString(R.string.empty_saved), Snackbar.LENGTH_LONG).show();
        } else {
            while (cursor.moveToNext()) {
                images.add(cursor.getString(3));
                placeName.add(cursor.getString(1));
                description.add(cursor.getString(2));
                latitude.add(cursor.getString(4));
                longitude.add(cursor.getString(5));
                address.add(cursor.getString(6));
                timestamp.add(cursor.getString(7));
                addedBy.add(cursor.getString(8));
            }
        }

        placeAdapter = new PlaceAdapter(images, placeName, description, latitude, longitude, address, timestamp, addedBy, false);
        recViewPlaces.setAdapter(placeAdapter);
        recViewPlaces.setLayoutManager(new LinearLayoutManager(UploadActivity.this));

        getSupportActionBar().setTitle(getResources().getString(R.string.saved_places));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.submit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submit:
                new MaterialAlertDialogBuilder(UploadActivity.this)
                        .setCancelable(false)
                        .setTitle(getResources().getString(R.string.upload_title))
                        .setMessage(getResources().getString(R.string.upload_message))
                        .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                imgLoading.setBackgroundResource(R.drawable.loading);
                                loadingAnimation = (AnimationDrawable) imgLoading.getBackground();
                                loadingAnimation.start();
                                layoutLoading.setVisibility(View.VISIBLE);

                                ArrayList places = new ArrayList<>();
                                List<Task<Uri>> taskArrayList = new ArrayList<>();

                                for (int counter = 0; counter < images.size(); counter++) {
                                    JsonArray outputJsonArray = JsonParser.parseString(String.valueOf(images.get(counter))).getAsJsonArray();

                                    ArrayList<String> imagesUrl = new ArrayList<>();
                                    for (int ii = 0; ii < outputJsonArray.size(); ii++) {
                                        File image = new File(outputJsonArray.get(ii).toString().replace("\"", ""));

                                        Task<Uri> uploadTask = uploadImage(image);
                                        taskArrayList.add(uploadTask);

                                        int finalCounter = counter;
                                        int finalIi = ii;
                                        uploadTask.addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                imagesUrl.add(task.getResult().toString());

                                                if (finalIi == outputJsonArray.size() - 1) {
                                                    JsonObject object = JsonParser.parseString(String.valueOf(new Place(placeName.get(finalCounter).toString(), description.get(finalCounter).toString(), imagesUrl.toString(), latitude.get(finalCounter).toString(), longitude.get(finalCounter).toString(), address.get(finalCounter).toString(), timestamp.get(finalCounter).toString(), addedBy.get(finalCounter).toString()))).getAsJsonObject();
                                                    places.add(object);
                                                }
                                            }
                                        });

                                    }
                                }


                                Tasks.whenAllSuccess(taskArrayList).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {

                                        SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES_KEY, MODE_PRIVATE);
                                        String json = sharedPreferences.getString(Constant.SHARED_PREFERENCES_USER, Constant.DEFAULT_VALUE);
                                        Gson gson = new Gson();
                                        User obj = gson.fromJson(json, User.class);

                                        RequestQueue queue = Volley.newRequestQueue(UploadActivity.this);
                                        StringRequest request = new StringRequest(Request.Method.POST, Constant.SERVER_URL + Constant.API_UPLOAD, new com.android.volley.Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                loadingAnimation.stop();
                                                layoutLoading.setVisibility(View.INVISIBLE);

                                                databaseHelper.delete();
                                                File folder = new File(getCacheDir(), "/images/");
                                                File[] files = folder.listFiles();
                                                for (File file : files)
                                                    file.delete();

                                                Snackbar.make(layoutUpload, getResources().getString(R.string.upload_success_message), Snackbar.LENGTH_LONG).show();
                                            }
                                        }, new com.android.volley.Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Snackbar.make(layoutUpload, getResources().getString(R.string.upload_error), Snackbar.LENGTH_LONG).show();
                                                loadingAnimation.stop();
                                                layoutLoading.setVisibility(View.INVISIBLE);
                                            }
                                        }) {

                                            @Override
                                            public Map<String, String> getHeaders() throws AuthFailureError {
                                                HashMap<String, String> headers = new HashMap<>();
                                                headers.put("Authorization", "Bearer " + obj.getToken());

                                                return headers;
                                            }

                                            @Override
                                            protected Map<String, String> getParams() {
                                                Map<String, String> params = new HashMap<String, String>();

                                                params.put("data", String.valueOf(places));

                                                return params;
                                            }
                                        };
                                        queue.add(request);
                                    }
                                });



                            }
                        }).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public Task<Uri> uploadImage(File file) {
        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        UploadTask uploadTask = ref.putFile(Uri.fromFile(file));

        return uploadTask.continueWithTask(task -> {
            return ref.getDownloadUrl();
        });
    }
}