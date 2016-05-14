package com.heigvd.gen.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.support.design.widget.TextInputLayout;

import com.heigvd.gen.chat.Network.JsonSender;
import com.heigvd.gen.chat.Network.Query.Register;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioButtons);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                TextInputLayout server = (TextInputLayout) findViewById(R.id.serverLayout);
                if (checkedId == R.id.radioButton_privateServer) {
                    server.setVisibility(View.VISIBLE);
                }
                else {
                    server.setVisibility(View.GONE);
                }
            }
        });

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
                    login.setError(null);
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

        final Button loginButton = (Button)findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonSender jsender = new JsonSender();
                Log.d(SubscribeActivity.class.getSimpleName(), "Registering with username : " + login.getText().toString() + " and password : " + passwordBox.getText().toString());
                jsender.send(new Register(login.getText().toString(),passwordBox.getText().toString()));
            }
        });

        final Button subscribeButton = (Button)findViewById(R.id.subscribe_button);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SubscribeActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateNotEmpty(Editable s) {
        return !TextUtils.isEmpty(s.toString());
    }
}

