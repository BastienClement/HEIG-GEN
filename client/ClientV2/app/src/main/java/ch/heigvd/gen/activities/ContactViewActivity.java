package ch.heigvd.gen.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

import ch.heigvd.gen.R;
import ch.heigvd.gen.adapters.ChatAdapter;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.models.Message;


public class ContactViewActivity extends AppCompatActivity implements IRequests {

    private List<Message> list;
    ChatAdapter adapter;

    private final static String TAG = ContactListActivity.class.getSimpleName();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_contact_view);

            //Initiate List
            list = new LinkedList<>();

            // Create the ChatAdapter
            adapter = new ChatAdapter(this, R.layout.other_message_list_item, list);


            // fill listview
            final ListView listView = (ListView) findViewById(R.id.message_list);
            listView.setAdapter(adapter);


        }
}