package ch.heigvd.gen.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ch.heigvd.gen.R;
import ch.heigvd.gen.activities.ContactDiscussionActivity;
import ch.heigvd.gen.activities.ContactAddActivity;
import ch.heigvd.gen.adapters.ContactListAdapter;
import ch.heigvd.gen.communications.RequestGET;
import ch.heigvd.gen.interfaces.ICallback;
import ch.heigvd.gen.interfaces.ICustomCallback;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.models.Group;
import ch.heigvd.gen.models.Message;
import ch.heigvd.gen.models.User;
import ch.heigvd.gen.services.EventService;
import ch.heigvd.gen.utilities.Utils;

/**
 * A simple {@link Fragment} subclass.
 * This fragment implements the Contact discussion tab
 */
public class ContactFragment extends Fragment implements IRequests, ICustomCallback {
    ListView listView;
    SearchView searchView;
    ContactListAdapter adapter;

    private final static String TAG = ContactFragment.class.getSimpleName();


    /**
     * When the Fragment is created
     *
     * @param inflater           inflater for the view
     * @param container          view's container
     * @param savedInstanceState a potentially saved instance of the fragment
     * @return the created view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact, container, false);
        listView = (ListView) v.findViewById(R.id.contact_list);
        searchView = (SearchView) v.findViewById(R.id.search_frag_contact);
        setHasOptionsMenu(true);

        // Load contacts
        loadContacts();
        return v;
    }

    /**
     * Called right after the creation of the fragment
     */
    @Override
    public void onStart() {
        super.onStart();

        // Create adapter
        adapter = new ContactListAdapter(getActivity(), R.layout.contacts_list_item, User.users);

        // handle click on contact
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                // get contact
                final User item = (User) parent.getItemAtPosition(position);

                // start contact edit activity
                Intent intent = new Intent(getActivity(), ContactDiscussionActivity.class);
                Bundle b = new Bundle();
                b.putString("user_name", item.getUsername());
                b.putInt("user_id", item.getId());
                intent.putExtras(b);
                startActivity(intent);
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        // fill listview
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);

    }


    /**
     * Loads the contacts using an HTTP GET request
     */
    private void loadContacts() {
        try {
            new RequestGET(new ICallback<String>() {
                @Override
                public void success(String result) {
                    adapter.clear();
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonUser = jsonArray.getJSONObject(i);
                            User user = new User(jsonUser.getInt("id"), jsonUser.getString("name"), jsonUser.getBoolean("admin"), jsonUser.getBoolean("unread"));
                            User.users.add(user);
                        }
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
                        Utils.showAlert(getActivity(), new JSONObject(ex.getMessage()).getString("err"));
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    Log.e(TAG, ex.getMessage());
                }
            }, Utils.getToken(getActivity()), BASE_URL + GET_CONTACTS).execute();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    /**
     * Loads the messages using an HTTP GET request
     */
    private void loadMessages() {
        for (final User user : User.users) {
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
                    User.sortUsers();
                    adapter.notifyDataSetChanged();
                    Log.i(TAG, "Success : " + result);
                }

                @Override
                public void failure(Exception ex) {
                    try {
                        Utils.showAlert(getActivity(), new JSONObject(ex.getMessage()).getString("err"));
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    Log.e(TAG, ex.getMessage());
                }
            }, Utils.getToken(getActivity()), BASE_URL + GET_CONTACT + user.getId() + GET_MESSAGES).execute();
        }
    }

    /**
     * To update the contact list
     */
    @Override
    public void update() {
        User.sortUsers();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Called when the menu is created
     *
     * @param menu     the Menu element
     * @param inflater the menu's inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_contacts, menu);
    }

    /**
     * Implements the different behaviours for every menu item, the add contact button and the
     * logoff button
     *
     * @param item the item that was clicked
     * @return true if the MenuItem was correctly handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add) {
            // start contact search activity
            Intent intent = new Intent(getActivity(), ContactAddActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.logoff) {
            User.users = new ArrayList<>();
            Group.groups = new ArrayList<>();
            getActivity().finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called on resume of the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        User.sortUsers();
        adapter.notifyDataSetChanged();
    }
}
