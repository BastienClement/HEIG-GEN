package ch.heigvd.gen.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import ch.heigvd.gen.R;
import ch.heigvd.gen.models.User;


public class ContactViewActivity extends AppCompatActivity {

    // simple test message
    private String[] messageArray = {"a", "b", "c"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_view);

        // get contact
        final Bundle b = getIntent().getExtras();
        String contact = null;
        int id;
        if(b != null) {
            contact = b.getString("contact");
            id = b.getInt("id");
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.message_list_item, messageArray);

        // fill listview
        final ListView listView = (ListView) findViewById(R.id.message_list);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);

        // enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // set contact name
        TextView title = (TextView) findViewById(R.id.contact_name);
        title.setText(contact);

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactViewActivity.this, ContactEditActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

    }
}
