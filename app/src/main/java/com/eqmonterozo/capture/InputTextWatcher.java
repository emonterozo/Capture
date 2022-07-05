package com.eqmonterozo.capture;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

public class InputTextWatcher implements TextWatcher {

    private EditText editText;
    private TextInputLayout textInputLayout;

    public InputTextWatcher(EditText editText, TextInputLayout textInputLayout) {
        this.editText = editText;
        this.textInputLayout = textInputLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean isEmpty = editText.getText().toString().isEmpty();

        if (isEmpty) {
            textInputLayout.setHelperText("Required*");
        } else {
            textInputLayout.setErrorEnabled(false);
            textInputLayout.setHelperTextEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
