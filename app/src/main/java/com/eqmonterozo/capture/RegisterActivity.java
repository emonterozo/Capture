package com.eqmonterozo.capture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    TextInputLayout inputFirstname, inputLastname, inputUsername, inputPassword;
    TextInputEditText edtFirstname, edtLastname, edtUsername, edtPassword;

    TextView txtError;

    Button btnRegister, btnLogin;

    ImageView imgLoading;
    AnimationDrawable loadingAnimation;
    ConstraintLayout layoutLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        imgLoading = findViewById(R.id.imgLoading);
        layoutLoading = findViewById(R.id.layoutLoading);

        txtError = findViewById(R.id.txtError);

        inputFirstname = findViewById(R.id.inputFirstname);
        edtFirstname = findViewById(R.id.edtFirstname);

        inputLastname = findViewById(R.id.inputLastname);
        edtLastname = findViewById(R.id.edtLastname);

        inputUsername = findViewById(R.id.inputUsername);
        edtUsername = findViewById(R.id.edtUsername);

        inputPassword = findViewById(R.id.inputPassword);
        edtPassword = findViewById(R.id.edtPassword);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);

        edtFirstname.addTextChangedListener(new InputTextWatcher(edtFirstname, inputFirstname));
        edtLastname.addTextChangedListener(new InputTextWatcher(edtLastname, inputLastname));
        edtUsername.addTextChangedListener(new InputTextWatcher(edtUsername, inputUsername));
        edtPassword.addTextChangedListener(new InputTextWatcher(edtPassword, inputPassword));

        getSupportActionBar().setTitle(getResources().getString(R.string.register));

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("isValid");
                boolean isValid = true;

                if (edtFirstname.getText().toString().isEmpty()) {
                    inputFirstname.setHelperTextEnabled(false);
                    inputFirstname.setError(getResources().getText(R.string.firstname_mandatory));
                    isValid = false;
                }

                if (edtLastname.getText().toString().isEmpty()) {
                    inputLastname.setHelperTextEnabled(false);
                    inputLastname.setError(getResources().getText(R.string.lastname_mandatory));
                    isValid = false;
                }

                if (edtUsername.getText().toString().isEmpty()) {
                    inputUsername.setHelperTextEnabled(false);
                    inputUsername.setError(getResources().getText(R.string.username_mandatory));
                    isValid = false;
                }

                if (edtPassword.getText().toString().isEmpty()) {
                    inputPassword.setHelperTextEnabled(false);
                    inputPassword.setError(getResources().getText(R.string.password_mandatory));
                    isValid = false;
                }


                if (isValid) {
                    imgLoading.setBackgroundResource(R.drawable.loading);
                    loadingAnimation = (AnimationDrawable) imgLoading.getBackground();
                    loadingAnimation.start();
                    layoutLoading.setVisibility(View.VISIBLE);
                    RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);

                    StringRequest request = new StringRequest(Request.Method.POST, Constant.SERVER_URL + Constant.API_REGISTER, new com.android.volley.Response.Listener<String>() {
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
                                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
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

                            params.put("first_name", edtFirstname.getText().toString());
                            params.put("last_name", edtLastname.getText().toString());
                            params.put("username", edtUsername.getText().toString());
                            params.put("password", edtPassword.getText().toString());

                            return params;
                        }
                    };
                    queue.add(request);
                }

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}