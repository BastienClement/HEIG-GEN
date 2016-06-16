package ch.heigvd.gen.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.heigvd.gen.R;
import ch.heigvd.gen.communications.RequestGET;
import ch.heigvd.gen.communications.RequestPUT;
import ch.heigvd.gen.interfaces.ICallback;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.models.User;
import ch.heigvd.gen.utilities.Utils;

/**
 * Activity allowing the user to add contact from the server's list of users. Clicking on a username
 * adds the corresponing contact on the user's Discussion list
 */
public class ContactAddActivity extends AppCompatActivity implements IRequests {

    private final static String TAG = ContactAddActivity.class.getSimpleName();

    ArrayAdapter adapter = null;

    /**
     * Called when the activity is first created, uses a ListView and an ArrayAdapter<User> to
     * display the list of every user registered on the server. A search view allows the user to
     * search for a specific user in that list.
     *
     * @param savedInstanceState a potential previous state saved
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_add);

        // enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new ArrayAdapter<User>(this, R.layout.contacts_search_item);

        loadUsers();

        // fill listview
        final ListView listView = (ListView) findViewById(R.id.user_list);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);

        // handle click on contact
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                // get contact
                final User user = (User) parent.getItemAtPosition(position);

                try {
                    new RequestPUT(new ICallback<String>() {
                        @Override
                        public void success(String result) {
                            Log.i(TAG, "Success : " + result);
                            finish();
                        }

                        @Override
                        public void failure(Exception ex) {
                            try {
                                Utils.showAlert(ContactAddActivity.this, new JSONObject(ex.getMessage()).getString("err"));
                            } catch (JSONException e) {
                                Utils.showAlert(ContactAddActivity.this, ex.getMessage());
                                Log.e(TAG, e.getMessage());
                            }
                            Log.e(TAG, ex.getMessage());
                        }
                    }, Utils.getToken(ContactAddActivity.this), BASE_URL + GET_CONTACT + user.getId()).execute();
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
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

    /**
     * Execute a HTTP GET request to retrieve the users list from the server and fill the Adapter
     * with that list
     */
    private void loadUsers() {
        try {
            new RequestGET(new ICallback<String>() {
                @Override
                public void success(String result) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonUser = jsonArray.getJSONObject(i);
                            adapter.add(new User(jsonUser.getInt("id"), jsonUser.getString("name"), jsonUser.getBoolean("admin"), false));
                        }

                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "Success : " + result);
                }

                @Override
                public void failure(Exception ex) {
                    try {
                        Utils.showAlert(ContactAddActivity.this, new JSONObject(ex.getMessage()).getString("err"));
                    } catch (JSONException e) {
                        Utils.showAlert(ContactAddActivity.this, ex.getMessage());
                        Log.e(TAG, e.getMessage());
                    }
                    Log.e(TAG, ex.getMessage());
                }
            }, Utils.getToken(this), BASE_URL + GET_ALL_UNKNOWN_USERS).execute();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    /**
     * Implements the back button behaviour to go back to the discussion activity
     *
     * @param item The menuItem that was clicked
     * @return true if the menuItem was successfully handled
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
}
