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

import ch.heigvd.gen.R;
import ch.heigvd.gen.adapters.ContactDiscussionAdapter;
import ch.heigvd.gen.adapters.ContactListAdapter;
import ch.heigvd.gen.communications.RequestGET;
import ch.heigvd.gen.interfaces.ICallback;
import ch.heigvd.gen.interfaces.ICustomCallback;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.models.Message;
import ch.heigvd.gen.models.User;
import ch.heigvd.gen.services.EventService;
import ch.heigvd.gen.utilities.Utils;

/**
 * TODO
 * TODO : Mettre toutes les String dans les fichiers de ressources fait pour
 * TODO : Commenter/indenter/Trier imports, compléter la javadoc
 * TODO : Trier les utilisateurs par liste de derniers message
 * TODO : Faire un bouton de déconnexion
 * TODO : Faire les report/blocage d'utilisateur et report de message dans groupe
 * TODO : Faire que les dialogues dans une discussion soient joli + afficher l'heure du message et le jour et le nom de l'utilisateur qui a envoyé pour les discussions de groupe
 * TODO : Afficher le nombre de messages non lu sur contactlist
 * TODO : Faire les discussion de groupe
 * TODO : Mettre tous les éléments json et les requêtes dans IJSONKEYS et IREQUESTS
 * TODO : ARRETER EventService quand on quitte l'application ou qu'on se déconnecte
 */
public class ContactListActivity extends AppCompatActivity implements IRequests, ICustomCallback{

    ArrayAdapter adapter = null;

    private final static String TAG = ContactListActivity.class.getSimpleName();

    /**
     * TODO
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        // Start event handler service
        EventService.getInstance().setActivity(this, this);
        EventService.getInstance().start();

        // Load self pref
        loadSelfPref();

        // Load contacts
        loadContacts();

        // Create adapter
        adapter = new ContactListAdapter(this, R.layout.contacts_list_item, User.users);

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

    /**
     * TODO
     *
     */
    private void loadMessages(){
        for(final User user : User.users) {
            new RequestGET(new ICallback<String>() {
                @Override
                public void success(String result) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(result);
                        for (int i = jsonArray.length() - 1; i >= 0; i--) {
                            JSONObject jsonMessage = jsonArray.getJSONObject(i);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            user.addMessage(new Message(jsonMessage.getInt("from"), jsonMessage.getString("text"), sdf.parse(jsonMessage.getString("date")), jsonMessage.getInt("id")));
                        }
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
            }, Utils.getToken(this), BASE_URL + GET_CONTACT + user.getId() + GET_MESSAGES).execute();
        }
    }

    /**
     * TODO
     */
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
                            User user = new User(jsonUser.getInt("id"), jsonUser.getString("name"), jsonUser.getBoolean("admin"), jsonUser.getBoolean("unread"));
                            User.users.add(user);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // TODO : Order by last message
                    loadMessages();
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

    /**
     * TODO
     *
     * @param view
     */
    public void addContact(final View view){
        // start contact search activity
        Intent intent = new Intent(ContactListActivity.this, ContactSearchActivity.class);
        startActivity(intent);
    }

    /**
     * TODO
     */
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

    /**
     * Do nothing so the user can't go back to the login activity with the back button after the login
     */
    @Override
    public void onBackPressed() {
        // Do nothing
    }

    /**
     * TODO
     */
    @Override
    public void update() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * TODO
     */
    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        // Refresh contacts
        EventService.getInstance().setActivity(this, this);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause(){
        super.onPause();
        //EventService.getInstance().removeActivity();
    }

    @Override
    public void onStop(){
        super.onStop();
        /**
         * TODO : Stop on application exit or log off
         */
        //EventService.getInstance().stop();
        //EventService.getInstance().removeActivity();
    }
}
