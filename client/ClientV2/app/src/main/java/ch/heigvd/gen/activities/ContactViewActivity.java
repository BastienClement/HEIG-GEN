package ch.heigvd.gen.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import ch.heigvd.gen.R;
import ch.heigvd.gen.adapters.ChatAdapter;
import ch.heigvd.gen.communications.RequestGET;
import ch.heigvd.gen.interfaces.ICallback;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.models.Message;
import ch.heigvd.gen.utilities.Utils;


public class ContactViewActivity extends AppCompatActivity implements IRequests {

    private List<Message> list;
    ChatAdapter adapter;

    Bundle b = null;

    private final static String TAG = ContactViewActivity.class.getSimpleName();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_contact_view);

            // get contact
            b = getIntent().getExtras();
            String contact = null;
            int id;
            if(b != null) {
                contact = b.getString("contact");
                id = b.getInt("id");
            }

            // enable back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            //Initiate List
            list = new LinkedList<>();

            //Pour tester avant que les messages soient implémentés coté serveur


            // Create the ChatAdapter
            adapter = new ChatAdapter(this, R.layout.other_message_list_item, list);


            // fill listview
            final ListView listView = (ListView) findViewById(R.id.message_list);
            listView.setAdapter(adapter);


            // set contact name
            TextView title = (TextView) findViewById(R.id.contact_name);
            title.setText(contact);




        }

    public void editContact(final View view){
        // start contact search activity
        Intent intent = new Intent(ContactViewActivity.this, ContactEditActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        System.out.println(b.getInt("id"));
        super.onResume();
        // Check if it is still a contact
        new RequestGET(new ICallback<String>() {
            @Override
            public void success(String result) {
                Log.i(TAG, "Success : " + result);
            }

            @Override
            public void failure(Exception ex) {
                //finish();
            }
        }, Utils.getToken(ContactViewActivity.this), BASE_URL + GET_CONTACT + b.getInt("id")).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}