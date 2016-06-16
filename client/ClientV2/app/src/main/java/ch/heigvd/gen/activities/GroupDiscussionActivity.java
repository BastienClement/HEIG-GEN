package ch.heigvd.gen.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import ch.heigvd.gen.adapters.GroupDiscussionAdapter;
import ch.heigvd.gen.communications.RequestGET;
import ch.heigvd.gen.communications.RequestPOST;
import ch.heigvd.gen.communications.RequestPUT;
import ch.heigvd.gen.interfaces.ICallback;
import ch.heigvd.gen.interfaces.ICustomCallback;
import ch.heigvd.gen.interfaces.IJSONKeys;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.models.Group;
import ch.heigvd.gen.models.User;
import ch.heigvd.gen.services.EventService;
import ch.heigvd.gen.utilities.Utils;

/**
 * TODO
 */
public class GroupDiscussionActivity extends AppCompatActivity implements IRequests, IJSONKeys, ICustomCallback {

    private GroupDiscussionAdapter adapter;
    private Bundle b = null;

    private final static String TAG = GroupDiscussionActivity.class.getSimpleName();

    /**
     * TODO
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_discussion);

        // get group
        b = getIntent().getExtras();

        // enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the GroupDiscussionAdapter
        adapter = new GroupDiscussionAdapter(this, R.layout.other_message_list_item, Group.findById(b.getInt("group_id")).getMessages());

        // fill listview
        final ListView listView = (ListView) findViewById(R.id.message_list);
        listView.setAdapter(adapter);

        // Scroll to bottom of listview when keyboard opens up
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);

        // set group name
        TextView title = (TextView) findViewById(R.id.group_name);
        title.setText(b.getString("group_name"));

    }

    /**
     * TODO
     *
     * @param view
     */
    public void editGroup(final View view){
        // start group search
        Intent intent = new Intent(GroupDiscussionActivity.this, GroupEditActivity.class);
        intent.putExtras(b);
        startActivity(intent);;
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
                        Utils.showAlert(GroupDiscussionActivity.this, new JSONObject(ex.getMessage()).getString("err"));
                    } catch (JSONException e) {
                        Utils.showAlert(GroupDiscussionActivity.this, ex.getMessage());
                        Log.e(TAG, e.getMessage());
                    }
                    Log.e(TAG, ex.getMessage());
                }
            }, Utils.getToken(this), BASE_URL + GET_GROUP + b.getInt("group_id") + GET_MESSAGES, content).execute();
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
        Group.findById(b.getInt("group_id")).setUnread(false);
        try {
            new RequestPUT(new ICallback<String>() {
                @Override
                public void success(String result) {
                    Log.i(TAG, "Success : " + result);
                }

                @Override
                public void failure(Exception ex) {
                    try {
                        Utils.showAlert(GroupDiscussionActivity.this, new JSONObject(ex.getMessage()).getString("err"));
                    } catch (JSONException e) {
                        Utils.showAlert(GroupDiscussionActivity.this, ex.getMessage());
                        Log.e(TAG, e.getMessage());
                    }
                    Log.e(TAG, ex.getMessage());
                }
            }, Utils.getToken(GroupDiscussionActivity.this), BASE_URL + GET_GROUP + b.getInt("group_id") + SET_MESSAGES_READ).execute();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    /**
     * TODO
     */
    @Override
    public void update() {
        if(Group.findById(b.getInt("group_id")) == null){
            finish();
        } else{
            setReadMessages();
        }
        this.runOnUiThread(new Runnable(){
            public void run(){
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * After a pause OR at startup check if the grouo is still a group, if not finish the activity
     * TODO : Faire le check sur la liste des groups et pas faire une requête au serveur
     *
     */
    @Override
    public void onResume()
    {
        super.onResume();
        if(Group.findById(b.getInt("group_id")) == null){
            finish();
        } else{
            EventService.getInstance().setActivity(this);
            setReadMessages();
        }
    }
}