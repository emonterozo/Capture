package com.eqmonterozo.capture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    TextInputLayout textFieldUsername, textFieldPassword;
    TextView txtError;
    ImageView imgLoading;
    AnimationDrawable loadingAnimation;
    ConstraintLayout layoutLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textFieldUsername = findViewById(R.id.textFieldUsername);
        textFieldPassword = findViewById(R.id.textFieldPassword);
        txtError = findViewById(R.id.txtError);
        imgLoading = findViewById(R.id.imgLoading);
        layoutLoading = findViewById(R.id.layoutLoading);

        getSupportActionBar().setTitle(getResources().getString(R.string.login));


        checkIfUserLogin();

    }

    public void checkIfUserLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        String user = sharedPreferences.getString(Constant.SHARED_PREFERENCES_USER, Constant.DEFAULT_VALUE);

        if (!user.equals(Constant.DEFAULT_VALUE)) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }

    public void onClickRegister(View view) {
        Intent intent = new Intent(view.getContext(), RegisterActivity.class);
        startActivity(intent);
    }

    public void onClickLogin(View view) {
        imgLoading.setBackgroundResource(R.drawable.loading);
        loadingAnimation = (AnimationDrawable) imgLoading.getBackground();
        loadingAnimation.start();
        layoutLoading.setVisibility(View.VISIBLE);


        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constant.SERVER_URL + Constant.API_LOGIN, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingAnimation.stop();
                layoutLoading.setVisibility(View.INVISIBLE);
                try {
                    JSONObject respObj = new JSONObject(response);
                    String error = respObj.getString("error");
                    if (TextUtils.isEmpty(error)) {
                        JSONObject user = respObj.getJSONObject("data");
                        SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES_KEY, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constant.SHARED_PREFERENCES_USER, String.valueOf(user));
                        editor.apply();
                        Intent intent = new Intent(view.getContext(), HomeActivity.class);
                        startActivity(intent);
                    } else {
                        txtError.setText(error);
                        txtError.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingAnimation.stop();
                layoutLoading.setVisibility(View.INVISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("username", textFieldUsername.getEditText().getText().toString());
                params.put("password", textFieldPassword.getEditText().getText().toString());

                return params;
            }
        };
        queue.add(request);
    }
}