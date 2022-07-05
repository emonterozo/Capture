package com.eqmonterozo.capture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setTitle(getResources().getString(R.string.home));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorites:
                Toast.makeText(getApplicationContext(), "Favorites", Toast.LENGTH_SHORT).show();
                return  true;
            case R.id.logout:
               new MaterialAlertDialogBuilder(HomeActivity.this)
                       .setTitle(getResources().getString(R.string.logout_title))
                       .setMessage(getResources().getString(R.string.logout_message))
                       .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {

                           }
                       })
                       .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES_KEY, MODE_PRIVATE);
                               SharedPreferences.Editor editor = sharedPreferences.edit();
                               editor.clear();
                               editor.apply();
                               Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                               startActivity(intent);
                           }
                       })
                       .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        String user = sharedPreferences.getString(Constant.SHARED_PREFERENCES_USER, Constant.DEFAULT_VALUE);

        if (!user.equals(Constant.DEFAULT_VALUE)) {
            finishAffinity();
            finish();
        }
        super.onBackPressed();
    }

    public void onClickButton(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btnAdd:
                intent = new Intent(HomeActivity.this, AddActivity.class);
                startActivity(intent);
                break;
            case R.id.btnSearch:
                intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.btnUpload:
                intent = new Intent(HomeActivity.this, UploadActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

}