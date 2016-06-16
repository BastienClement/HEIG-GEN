package ch.heigvd.gen.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import ch.heigvd.gen.R;
import ch.heigvd.gen.adapters.GroupAddMemberListAdapter;
import ch.heigvd.gen.communications.RequestPUT;
import ch.heigvd.gen.interfaces.ICallback;
import ch.heigvd.gen.interfaces.IJSONKeys;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.models.Group;
import ch.heigvd.gen.models.User;
import ch.heigvd.gen.utilities.Utils;

/**
 * Activity allowing to add new members to the current group
 */
public class GroupAddMemberActivity extends AppCompatActivity implements IRequests, IJSONKeys {

    GroupAddMemberListAdapter adapter = null;

    private final static String TAG = GroupAddMemberActivity.class.getSimpleName();

    Bundle b = null;

    /**
     * Called when the activity is first created, uses a ListView and a custom GroupAddMemberListAdapter to
     * display the list of the user's contact
     *
     * @param savedInstanceState a potential previous state saved
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_add_member);

        // enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get bundle
        b = getIntent().getExtras();

        // Create adapter
        adapter = new GroupAddMemberListAdapter(this, R.layout.add_members_list_item, Group.findById(b.getInt("group_id")), User.users);

        final ListView listView = (ListView) findViewById(R.id.group_contact_list);

        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);

        // handle click on contact
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                // get contact
                final User user = (User) parent.getItemAtPosition(position);
                inviteContact(b.getInt("group_id"), user.getId());
            }
        });
    }

    /**
     * Execute an HTTP Put request to add a contact to the current group
     *
     * @param groupId the id of the group
     */
    public void inviteContact(int groupId, int userId) {
        try {
            new RequestPUT(new ICallback<String>() {
                @Override
                public void success(String result) {
                    GroupAddMemberActivity.this.finish();
                    Log.i(TAG, "Success : " + result);
                }

                @Override
                public void failure(Exception ex) {
                    try {
                        Utils.showAlert(GroupAddMemberActivity.this, new JSONObject(ex.getMessage()).getString("err"));
                    } catch (JSONException e) {
                        Utils.showAlert(GroupAddMemberActivity.this, ex.getMessage());
                        Log.e(TAG, e.getMessage());
                    }
                    Log.e(TAG, ex.getMessage());
                }
            }, Utils.getToken(GroupAddMemberActivity.this), BASE_URL + GET_GROUP + groupId + GET_MEMBER + userId).execute();
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
