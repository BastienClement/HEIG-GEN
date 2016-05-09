package com.example.guillaume.client_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.support.design.widget.TextInputLayout;


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
                    server.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}

