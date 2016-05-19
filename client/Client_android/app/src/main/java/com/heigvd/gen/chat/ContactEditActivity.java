package com.heigvd.gen.chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ContactEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edit);

        // get contact
        Bundle b = getIntent().getExtras();
        String contact = null;
        if(b != null)
            contact = b.getString("contact");

        // enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // set contact name
        TextView title = (TextView) findViewById(R.id.contact_name);
        title.setText(contact);
    }
}
