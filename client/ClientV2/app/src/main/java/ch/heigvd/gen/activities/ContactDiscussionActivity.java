package ch.heigvd.gen.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import ch.heigvd.gen.R;
import ch.heigvd.gen.adapters.ChatAdapter;
import ch.heigvd.gen.communications.RequestGET;
import ch.heigvd.gen.communications.RequestPOST;
import ch.heigvd.gen.interfaces.ICallback;
import ch.heigvd.gen.interfaces.IJSONKeys;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.models.Message;
import ch.heigvd.gen.utilities.Utils;


public class ContactDiscussionActivity extends AppCompatActivity implements IRequests, IJSONKeys {

    private List<Message> list = new LinkedList<>();
    private ChatAdapter adapter;
    private Bundle b = null;

    private final static String TAG = ContactDiscussionActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_discussion);

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

        // Get the list of messages
        loadMessages();

        // Create the ChatAdapter
        adapter = new ChatAdapter(this, R.layout.other_message_list_item, list);

        // fill listview
        final ListView listView = (ListView) findViewById(R.id.message_list);
        listView.setAdapter(adapter);

        // Scroll to bottom of listview when keyboard opens up
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);

        // set contact name
        TextView title = (TextView) findViewById(R.id.contact_name);
        title.setText(contact);

    }

    private void loadMessages(){
        // fill listview
        final ListView listView = (ListView) findViewById(R.id.message_list);
        new RequestGET(new ICallback<String>() {
            @Override
            public void success(String result) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(result);
                    adapter.clear();
                    for (int i = jsonArray.length() - 1; i >= 0; i--) {
                        JSONObject jsonMessage = jsonArray.getJSONObject(i);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        adapter.add(new Message(jsonMessage.getInt("from"), jsonMessage.getString("text"), sdf.parse(jsonMessage.getString("date")), jsonMessage.getInt("id")));
                    }
                    adapter.notifyDataSetChanged();
                    listView.setSelection(adapter.getCount() - 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // TODO : Order by last message

                Log.i(TAG, "Success : " + result);
            }

            @Override
            public void failure(Exception ex) {
                try {
                    Utils.showAlert(ContactDiscussionActivity.this, new JSONObject(ex.getMessage()).getString("err"));
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
                Log.e(TAG, ex.getMessage());
            }
        }, Utils.getToken(this), BASE_URL + GET_CONTACT + b.getInt("id") + GET_MESSAGES).execute();
    }

    public void editContact(final View view){
        // start contact search activity
        Intent intent = new Intent(ContactDiscussionActivity.this, ContactEditActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void sendMessage(final View view){
        final EditText text = (EditText) findViewById(R.id.write_message);
        if(TextUtils.isEmpty(text.getText())) {
            text.setError("Message is empty !");
            return;
        }
        try {
            String[] keys = new String[]{KEY_MESSAGE};
            String[] values = new String[]{text.getText().toString()};
            String content = Utils.createJSONObject(keys, values);
            new RequestPOST(new ICallback<String>() {
                @Override
                public void success(String result) {
                    loadMessages();
                    text.setText("");
                    Log.i(TAG, "Success : " + result);
                }

                @Override
                public void failure(Exception ex) {
                    try {
                        Utils.showAlert(ContactDiscussionActivity.this, new JSONObject(ex.getMessage()).getString("err"));
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    Log.e(TAG, ex.getMessage());
                }
            }, Utils.getToken(this), BASE_URL + GET_CONTACT + b.getInt("id") + GET_MESSAGES, content).execute();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    /**
     * TODO : BACK BUTTON MUST WORK WITH THE MANIFEST (SETPARENT)
     */
    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        try {
            Log.i(TAG, "Token : " + Utils.getToken(this));
            new RequestGET(new ICallback<String>() {
                @Override
                public void success(String result) {

                    Log.i(TAG, "Success : " + result);
                }

                @Override
                public void failure(Exception ex) {
                    finish();
                    Log.e(TAG, ex.getMessage());
                }
            }, Utils.getToken(this), BASE_URL + GET_CONTACT + b.getInt("id")).execute();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }

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