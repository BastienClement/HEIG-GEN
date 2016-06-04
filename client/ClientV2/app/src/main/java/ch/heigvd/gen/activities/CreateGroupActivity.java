package ch.heigvd.gen.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.heigvd.gen.R;
import ch.heigvd.gen.communications.RequestGET;
import ch.heigvd.gen.interfaces.ICallback;
import ch.heigvd.gen.interfaces.ICustomCallback;
import ch.heigvd.gen.models.User;
import ch.heigvd.gen.services.EventService;
import ch.heigvd.gen.utilities.Utils;

public class CreateGroupActivity extends AppCompatActivity  {

    ArrayAdapter adapter = null;

    private final static String TAG = CreateGroupActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);



        // enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create adapter
        adapter = new ArrayAdapter<User>(this, R.layout.group_contacts_list_item, User.users);

        final ListView listView = (ListView) findViewById(R.id.group_contact_list);

        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setItemsCanFocus(false);


    }




    /**
     * TODO Méthode de création du groupe
     *
     * @param view
     */
    public void createNewGroup(final View view){
        /**
         * TODO
         */
        // start contact search activity
        //Intent intent = new Intent(MainActivity.this, CreateGroupActivity.class);
        //startActivity(intent);
    }



    /**
     *


    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        // Refresh contacts
        //EventService.getInstance().setActivity(this, this);
        adapter.notifyDataSetChanged();
    }
     */

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

}
