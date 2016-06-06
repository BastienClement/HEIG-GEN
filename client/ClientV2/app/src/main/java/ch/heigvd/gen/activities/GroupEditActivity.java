package ch.heigvd.gen.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ch.heigvd.gen.R;
import ch.heigvd.gen.adapters.ContactDiscussionAdapter;
import ch.heigvd.gen.adapters.GroupMemberListAdapter;
import ch.heigvd.gen.communications.RequestDELETE;
import ch.heigvd.gen.interfaces.ICallback;
import ch.heigvd.gen.interfaces.ICustomCallback;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.models.Group;
import ch.heigvd.gen.models.User;
import ch.heigvd.gen.services.EventService;
import ch.heigvd.gen.utilities.Utils;

/**
 * TODO
 */
public class GroupEditActivity extends AppCompatActivity implements IRequests, ICustomCallback {


    private GroupMemberListAdapter adapter;
    private final static String TAG = GroupEditActivity.class.getSimpleName();

    Bundle b = null;

    /**
     * TODO
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);

        // get bundle
        b = getIntent().getExtras();

        // enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EventService.getInstance().setActivity(this);

        // Create the ContactDiscussionAdapter
        adapter = new GroupMemberListAdapter(this, R.layout.contacts_list_item,  Group.findById(b.getInt("group_id")), Group.findById(b.getInt("group_id")).getMembers());

        // fill listview
        final ListView listView = (ListView) findViewById(R.id.list_members);
        listView.setAdapter(adapter);

        // set group name
        TextView title = (TextView) findViewById(R.id.group_name);
        title.setText(b.getString("group_name"));
    }

    /**
     * TODO
     */
    public void removeGroup() {
        try {
            RequestDELETE delete = new RequestDELETE(new ICallback<String>() {
                @Override
                public void success(String result) {
                    Log.i(TAG, "Success : " + result);
                    finish();
                }

                @Override
                public void failure(Exception ex) {
                    try {
                        Utils.showAlert(GroupEditActivity.this, new JSONObject(ex.getMessage()).getString("err"));
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    Log.e(TAG, ex.getMessage());
                }
            }, Utils.getToken(GroupEditActivity.this), BASE_URL + GET_GROUP + b.getInt("group_id"));
            delete.execute();
            delete.get();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    /**
     * TODO
     */
    @Override
    public void update() {
        this.runOnUiThread(new Runnable(){
            public void run(){
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_group_edit, menu);
        if (!isAdmin()){
            MenuItem item = menu.findItem(R.id.add_member);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean admin = isAdmin();

        if(id == android.R.id.home){
            finish();
        }

        if (id == R.id.add_member) {
            // start contact create group activity
            Intent intent = new Intent(this, GroupAddMemberActivity.class);
            intent.putExtras(b);
            startActivity(intent);
            return true;
        }

        if (id == R.id.exit_group) {
            if(admin) {
                removeGroup();
            }
            else{
                exitGroup();
            }
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * TODO
     */
    private void exitGroup() {
        try {
            new RequestDELETE(new ICallback<String>() {
                @Override
                public void success(String result) {
                    Log.i(TAG, "Success : " + result);
                }

                @Override
                public void failure(Exception ex) {
                    try {
                        Utils.showAlert(GroupEditActivity.this, new JSONObject(ex.getMessage()).getString("err"));
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    Log.e(TAG, ex.getMessage());
                }
            }, Utils.getToken(this), BASE_URL + GET_GROUP + b.getInt("group_id") + GET_MEMBER + Utils.getId(this)).execute();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    private boolean isAdmin(){
        int id = Utils.getId(this);
        for(User user : Group.findById(b.getInt("group_id")).getMembers()){
            if(user.getId() == id){
                return user.isAdmin();
            }
        }
        return false;
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
        EventService.getInstance().setActivity(this);
    }
}
