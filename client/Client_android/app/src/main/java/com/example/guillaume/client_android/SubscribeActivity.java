package com.example.guillaume.client_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SubscribeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        final EditText login = (EditText)findViewById(R.id.login);
        login.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (validateNotEmpty(login.getText())) {
                    if (validateLogin(login.getText())) {
                        login.setError(null);
                    } else {
                        login.setError("Invalid username");
                    }
                } else {
                    login.setError("Required");
                }
            }
        });

        final EditText passwordBox = (EditText)findViewById(R.id.password);
        passwordBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(validateNotEmpty(passwordBox.getText())) {
                    passwordBox.setError(null);
                }
                else {
                    passwordBox.setError("Required");
                }
            }
        });

        final EditText passwordConfirmationBox = (EditText)findViewById(R.id.passwordConfirmation);
        passwordConfirmationBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(validateNotEmpty(passwordConfirmationBox.getText())) {
                    passwordConfirmationBox.setError(null);
                }
                else {
                    passwordConfirmationBox.setError("Required");
                }
            }
        });

        final Button subscribeButton = (Button)findViewById(R.id.subscribe_button);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if passwords are the same
                if (passwordBox.getText().equals(passwordConfirmationBox.getText())) {
                    // todo : call server to create account
                } else{
                    passwordConfirmationBox.setError("The passwords don't match");
                }
            }
        });
    }

    private boolean validateLogin(Editable login) {
        return login.toString().equals("correct");
    }

    private boolean validateNotEmpty(Editable s) {
        return !TextUtils.isEmpty(s.toString());
    }
}