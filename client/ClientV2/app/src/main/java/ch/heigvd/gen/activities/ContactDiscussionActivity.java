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

import org.json.JSONException;
import org.json.JSONObject;

import ch.heigvd.gen.R;
import ch.heigvd.gen.adapters.ContactDiscussionAdapter;
import ch.heigvd.gen.communications.RequestPOST;
import ch.heigvd.gen.communications.RequestPUT;
import ch.heigvd.gen.interfaces.ICallback;
import ch.heigvd.gen.interfaces.ICustomCallback;
import ch.heigvd.gen.interfaces.IJSONKeys;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.models.User;
import ch.heigvd.gen.services.EventService;
import ch.heigvd.gen.utilities.Utils;

/**
 * TODO
 */
public class ContactDiscussionActivity extends AppCompatActivity implements IRequests, IJSONKeys, ICustomCallback {

    private ContactDiscussionAdapter adapter;
    private Bundle b = null;

    private final static String TAG = ContactDiscussionActivity.class.getSimpleName();

    /**
     * TODO
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_discussion);

        // get contact
        b = getIntent().getExtras();

        // enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the ContactDiscussionAdapter
        adapter = new ContactDiscussionAdapter(this, R.layout.other_message_list_item, User.findById(b.getInt("user_id")).getMessages());

        // fill listview
        final ListView listView = (ListView) findViewById(R.id.message_list);
        listView.setAdapter(adapter);

        // Scroll to bottom of listview when keyboard opens up
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);

        // set contact name
        TextView title = (TextView) findViewById(R.id.contact_name);
        title.setText(b.getString("user_name"));

    }

    /**
     * TODO
     *
     * @param view
     */
    public void editContact(final View view){
        // start contact search activity
        Intent intent = new Intent(ContactDiscussionActivity.this, ContactEditActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    /**
     * TODO
     *
     * @param view
     */
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

                    // Empty editText
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
            }, Utils.getToken(this), BASE_URL + GET_CONTACT + b.getInt("user_id") + GET_MESSAGES, content).execute();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }



    /**
     * TODO
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setReadMessages(){
        try {
            Log.i(TAG, "Token : " + Utils.getToken(ContactDiscussionActivity.this));
            System.out.println("MESSAGE REEAWDADDADDDAD");
            new RequestPUT(new ICallback<String>() {
                @Override
                public void success(String result) {
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
            }, Utils.getToken(ContactDiscussionActivity.this), BASE_URL + GET_CONTACT + User.findById(b.getInt("user_id")).getId() + SET_MESSAGES_READ).execute();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    /**
     * TODO
     */
    @Override
    public void update() {
        setReadMessages();
        this.runOnUiThread(new Runnable(){
            public void run(){
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * After a pause OR at startup check if the user is still a contact, if not finish the activity
     * TODO : Faire le check sur la liste des users et pas faire une requête au serveur
     *
     */
    @Override
    public void onResume()
    {
        super.onResume();
        setReadMessages();
        EventService.getInstance().setActivity(this,this);
        if(User.findById(b.getInt("user_id")) == null){
            finish();
        }
    }

    /**
     * ¨TODO
     */
    @Override
    public void onPause(){
        super.onPause();
        //EventService.getInstance().removeActivity();
    }

    /**
     * TODO
     */
    @Override
    public void onStop(){
        super.onStop();
        //EventService.getInstance().removeActivity();
    }
}