package com.eqmonterozo.capture;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AddActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int CAMERA_REQUEST_CODE = 1;
    private int image = 0;
    private ImageView imgFirst, imgSecond, imgThird;
    private ViewFlipper flipAdd;
    private Button btnNext;

    private GoogleMap map;

    private FusedLocationProviderClient fusedLocationProviderClient;
    public LatLng latLng;

    FirebaseStorage storage;
    StorageReference storageReference;

    public ArrayList<String> images = new ArrayList<>();

    private TextInputLayout inputPlaceName, inputPlaceDescription;
    private TextInputEditText edtPlaceName, edtPlaceDescription;

    ImageView imgLoading;
    AnimationDrawable loadingAnimation;
    ConstraintLayout layoutLoading, layoutAdd;

    TextView txtConnectionStatus, txtImageHelper;

    DatabaseHelper databaseHelper;

    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        flipAdd = findViewById(R.id.flipAdd);

        imgLoading = findViewById(R.id.imgLoading);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutAdd = findViewById(R.id.layoutAdd);

        txtConnectionStatus = findViewById(R.id.txtConnectionStatus);
        txtImageHelper = findViewById(R.id.txtImageHelper);

        imgFirst = findViewById(R.id.imgFirst);
        imgSecond = findViewById(R.id.imgSecond);
        imgThird = findViewById(R.id.imgThird);
        inputPlaceName = findViewById(R.id.inputPlaceName);
        edtPlaceName = findViewById(R.id.edtPlaceName);
        inputPlaceDescription = findViewById(R.id.inputPlaceDescription);
        edtPlaceDescription = findViewById(R.id.edtPlaceDescription);
        btnNext = findViewById(R.id.btnNext);

        databaseHelper = new DatabaseHelper(this);

        if (!isNetworkAvailable()) {
            txtConnectionStatus.bringToFront();
            txtConnectionStatus.setVisibility(View.VISIBLE);

        }

        getLocation();


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        edtPlaceName.addTextChangedListener(new InputTextWatcher(edtPlaceName, inputPlaceName));
        edtPlaceDescription.addTextChangedListener(new InputTextWatcher(edtPlaceDescription, inputPlaceDescription));



        if (ContextCompat.checkSelfPermission(AddActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(AddActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(AddActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 404);
            ActivityCompat.requestPermissions(AddActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 404);
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isValid = true;

                if (images.size() == 0) {
                    txtImageHelper.setText(getResources().getText(R.string.image_mandatory));
                    isValid = false;
                }
                if (edtPlaceName.getText().toString().isEmpty()) {
                    inputPlaceName.setHelperTextEnabled(false);
                    inputPlaceName.setError(getResources().getText(R.string.name_mandatory));
                    isValid = false;
                }
                if (edtPlaceDescription.getText().toString().isEmpty()) {
                    inputPlaceDescription.setHelperTextEnabled(false);
                    inputPlaceDescription.setError(getResources().getText(R.string.description_mandatory));
                    isValid = false;
                }

                if (isValid) {
                    if (isNetworkAvailable()) {
                        MenuItem item = menu.findItem(R.id.submit);
                        item.setVisible(true);
                        flipAdd.showNext();
                        getSupportActionBar().setTitle(getResources().getString(R.string.confirm_location));
                    } else {
                        Geocoder geocoder = new Geocoder(AddActivity.this, Locale.getDefault());
                        try {
                            SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES_KEY, MODE_PRIVATE);
                            String json = sharedPreferences.getString(Constant.SHARED_PREFERENCES_USER, Constant.DEFAULT_VALUE);
                            Gson gson = new Gson();
                            User obj = gson.fromJson(json, User.class);

                            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            boolean isSuccess = databaseHelper.addPlace(edtPlaceName.getText().toString(),edtPlaceDescription.getText().toString(), images, latLng.latitude, latLng.longitude, addressList.get(0).getAddressLine(0), obj.getId());
                            if (isSuccess) {
                                successDialog(getResources().getString(R.string.success), getResources().getString(R.string.add_success_message_no_connection));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.submit_menu, menu);
        MenuItem item = menu.findItem(R.id.submit);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submit:
                new MaterialAlertDialogBuilder(AddActivity.this)
                        .setCancelable(false)
                        .setTitle(getResources().getString(R.string.submit_title))
                        .setMessage(getResources().getString(R.string.submit_message))
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

                                List<Task<Uri>> taskArrayList = new ArrayList<>();
                                for (int counter = 0; counter < images.size(); counter++) {

                                    File image = new File(images.get(counter).replace("\"", ""));

                                    taskArrayList.add(uploadImage(image));

                                    Tasks.whenAllSuccess(taskArrayList).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {

                                            Geocoder geocoder = new Geocoder(AddActivity.this, Locale.getDefault());
                                            try {
                                                List<Address> markerList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);


                                                SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES_KEY, MODE_PRIVATE);
                                                String json = sharedPreferences.getString(Constant.SHARED_PREFERENCES_USER, Constant.DEFAULT_VALUE);
                                                Gson gson = new Gson();
                                                User obj = gson.fromJson(json, User.class);


                                                RequestQueue queue = Volley.newRequestQueue(AddActivity.this);
                                                StringRequest request = new StringRequest(Request.Method.POST, Constant.SERVER_URL + Constant.API_ADD_PLACE, new com.android.volley.Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        loadingAnimation.stop();
                                                        layoutLoading.setVisibility(View.INVISIBLE);

                                                        for (int counter = 0; counter < images.size(); counter++) {
                                                            image.delete();
                                                        }
                                                        successDialog(getResources().getString(R.string.success), getResources().getString(R.string.add_success_message));
                                                    }
                                                }, new com.android.volley.Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Snackbar.make(layoutAdd, getResources().getString(R.string.add_error), Snackbar.LENGTH_LONG).show();
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

                                                        params.put("place_name", edtPlaceName.getText().toString());
                                                        params.put("description", edtPlaceDescription.getText().toString());
                                                        params.put("images", String.valueOf(task.getResult()));
                                                        params.put("latitude", String.valueOf(markerList.get(0).getLatitude()));
                                                        params.put("longitude", String.valueOf(markerList.get(0).getLongitude()));
                                                        params.put("address", markerList.get(0).getAddressLine(0));
                                                        params.put("added_by", String.valueOf(obj.getId()));

                                                        return params;
                                                    }
                                                };
                                                queue.add(request);

                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                                }

                            }
                        })
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void onClickCamera(View view) {
        switch (view.getId()) {
            case R.id.imgFirst:
                image = 1;
                break;
            case R.id.imgSecond:
                image = 2;
                break;
            case R.id.imgThird:
                image = 3;
                break;
            default:
                break;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (latLng != null) {
            // Add a marker and move the camera
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.draggable(true);
            map.addMarker(markerOptions);

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));


            // Add circle options
            CircleOptions circle = new CircleOptions();
            circle.center(latLng);
            circle.radius(10000);
            circle.fillColor(Color.BLUE);
            circle.strokeWidth(1);
            circle.strokeColor(Color.GRAY);

            // Draw circle
            map.addCircle(circle);


            map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDrag(@NonNull Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(@NonNull Marker marker) {
                    float[] distance = new float[2];

                    Location.distanceBetween(marker.getPosition().latitude, marker.getPosition().longitude, circle.getCenter().latitude, circle.getCenter().longitude, distance);
                    if (distance[0] > circle.getRadius()) {
                        new MaterialAlertDialogBuilder(AddActivity.this)
                                .setCancelable(false)
                                .setTitle(getResources().getString(R.string.location_error_title))
                                .setMessage(getResources().getString(R.string.location_error_message))
                                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        marker.setPosition(latLng);
                                    }
                                })
                                .show();
                    } else {
                        latLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                    }
                }

                @Override
                public void onMarkerDragStart(@NonNull Marker marker) {

                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");

            File folder = new File(getCacheDir(), "/images/");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            File imageFile = new File(folder, UUID.randomUUID().toString() + ".jpg");

            try {
                FileOutputStream outputStream = new FileOutputStream(String.valueOf(imageFile));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
                images.add("\"" + imageFile+ "\"");
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (image == 1) {
                imgFirst.setImageBitmap(bitmap);
            } else if (image == 2) {
                imgSecond.setImageBitmap(bitmap);
            } else {
                imgThird.setImageBitmap(bitmap);
            }
            txtImageHelper.setVisibility(View.GONE);
        }
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public Task<Uri> uploadImage(File file) {
        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        UploadTask uploadTask = ref.putFile(Uri.fromFile(file));

        return uploadTask.continueWithTask(task -> {
            return ref.getDownloadUrl();
        });
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void getLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> currentLocation = this.fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }

            @Override
            public boolean isCancellationRequested() {
                return false;
            }
        });

        currentLocation.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                latLng = new LatLng(location.getLatitude(), location.getLongitude());

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(AddActivity.this);
            }
        });
    }

    private void successDialog(String title, String message) {
        new MaterialAlertDialogBuilder(AddActivity.this)
            .setCancelable(false)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(AddActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            })
            .show();
    }
}