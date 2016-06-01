package ch.heigvd.gen.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ch.heigvd.gen.R;
import ch.heigvd.gen.communications.RequestGET;
import ch.heigvd.gen.interfaces.ICallback;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.models.Message;
import ch.heigvd.gen.models.User;
import ch.heigvd.gen.utilities.Utils;

public class ContactListActivity extends AppCompatActivity implements IRequests{

    ArrayAdapter adapter = null;

    private final static String TAG = ContactListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        // Create adapter
        adapter = new ArrayAdapter<User>(this, R.layout.contacts_list_item, User.users);

        // Load self pref
        loadSelfPref();

        // Load contacts
        loadContacts();

        // Update message from users every 5 seconds
        loadEvents();

        // fill listview
        final ListView listView = (ListView) findViewById(R.id.contact_list);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);

        // handle click on contact
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                // get contact
                final User item = (User) parent.getItemAtPosition(position);

                // start contact edit activity
                Intent intent = new Intent(ContactListActivity.this, ContactDiscussionActivity.class);
                Bundle b = new Bundle();
                b.putString("user_name", item.getUsername());
                b.putInt("user_id", item.getId());
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        // search view
        SearchView search = (SearchView) findViewById(R.id.search);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    listView.clearTextFilter();
                } else {
                    listView.setFilterText(newText.toString());
                }
                return true;
            }
        });
    }

    private void loadEvents() {
    }

    private void loadMessages(final int pos){
        new RequestGET(new ICallback<String>() {
            @Override
            public void success(String result) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(result);
                    for (int i = jsonArray.length() - 1; i >= 0; i--) {
                        JSONObject jsonMessage = jsonArray.getJSONObject(i);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        User.users.get(pos).addMessage(new Message(jsonMessage.getInt("from"), jsonMessage.getString("text"), sdf.parse(jsonMessage.getString("date")), jsonMessage.getInt("id")));
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "Success : " + result);
            }

            @Override
            public void failure(Exception ex) {
                try {
                    Utils.showAlert(ContactListActivity.this, new JSONObject(ex.getMessage()).getString("err"));
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
                Log.e(TAG, ex.getMessage());
            }
        }, Utils.getToken(this), BASE_URL + GET_CONTACT + User.users.get(pos).getId() + GET_MESSAGES).execute();
    }

    private void loadContacts(){
        try {
            Log.i(TAG, "Token : " + Utils.getToken(this));
            new RequestGET(new ICallback<String>() {
                @Override
                public void success(String result) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonUser = jsonArray.getJSONObject(i);
                            User user = new User(jsonUser.getInt("id"), jsonUser.getString("name"), jsonUser.getBoolean("admin"));
                            User.users.add(user);
                            loadMessages(User.users.indexOf(user));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // TODO : Order by last message

                    Log.i(TAG, "Success : " + result);
                }

                @Override
                public void failure(Exception ex) {
                    try {
                        Utils.showAlert(ContactListActivity.this, new JSONObject(ex.getMessage()).getString("err"));
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    Log.e(TAG, ex.getMessage());
                }
            }, Utils.getToken(this), BASE_URL + GET_CONTACTS).execute();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        // Refresh contacts
        adapter.notifyDataSetChanged();
    }

    public void addContact(final View view){
        // start contact search activity
        Intent intent = new Intent(ContactListActivity.this, ContactSearchActivity.class);
        startActivity(intent);
    }

    private void loadSelfPref(){
        try {
            new RequestGET(new ICallback<String>() {
                @Override
                public void success(String result) {
                    try{
                        JSONObject json = new JSONObject(result);
                        Utils.setId(ContactListActivity.this, json.getInt("id"));
                        Log.i(TAG, "Id : " + json.getString("id"));
                    } catch (Exception ex){
                        Log.e(TAG, ex.getMessage());
                    }
                    Log.i(TAG, "Success : " + result);
                }

                @Override
                public void failure(Exception ex) {
                    try {
                        Utils.showAlert(ContactListActivity.this, new JSONObject(ex.getMessage()).getString("err"));
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    Log.e(TAG, ex.getMessage());
                }
            }, Utils.getToken(this), BASE_URL + GET_SELF).execute();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }
}
