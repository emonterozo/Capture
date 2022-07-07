package com.eqmonterozo.capture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlaceDetailsActivity extends AppCompatActivity {
    RecyclerView recViewImages;
    ImageAdapter imageAdapter;

    TextView txtPlaceName, txtDescription, txtAddress, txtAddedBy;
    Button btnLocate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        recViewImages = findViewById(R.id.recViewImages);
        txtPlaceName = findViewById(R.id.txtPlaceName);
        txtDescription = findViewById(R.id.txtDescription);
        txtAddress = findViewById(R.id.txtAddress);
        txtAddedBy = findViewById(R.id.txtAddedBy);
        btnLocate = findViewById(R.id.btnLocate);

        Date date = null;
        try {
            date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(getIntent().getStringExtra("timestamp").replaceAll("Z$", "+0000"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        txtPlaceName.setText(getIntent().getStringExtra("placeName"));
        txtDescription.setText(getIntent().getStringExtra("description"));
        txtAddress.setText(getIntent().getStringExtra("address"));
        txtAddedBy.setText("Added by: " + getIntent().getStringExtra("addedBy") + ", " + (new SimpleDateFormat("yyyy-MM-dd hh:mm aa")).format(date));


        JsonArray outputJsonArray = JsonParser.parseString(getIntent().getStringExtra("images")).getAsJsonArray();
        imageAdapter = new ImageAdapter(outputJsonArray);
        recViewImages.setAdapter(imageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recViewImages.setLayoutManager(linearLayoutManager);

        btnLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q="+getIntent().getStringExtra("latitude") +"," + getIntent().getStringExtra("longitude")));
                startActivity(intent);
            }
        });
    }
}