package ch.heigvd.gen.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ch.heigvd.gen.R;
import ch.heigvd.gen.adapters.ContactListViewAdapter;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.models.User;

public class ContactListActivity extends AppCompatActivity implements IRequests {

    private final static String TAG = ContactListActivity.class.getSimpleName();

    private List<User> list;
    private ContactListViewAdapter adapter;
    private EditText filter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        list = new LinkedList<>();
        adapter = new ContactListViewAdapter(this, R.layout.contact_list_item, list);
        filter = (EditText) findViewById(R.id.filter);
        listView = (ListView) findViewById(R.id.listView);

        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /* Nothing to do here ! */
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /* Nothing to do here ! */
            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(filter.getText().toString());
            }
        });

        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.placeholder));
        loadUsers();
    }

    private void loadUsers() {
        String[] contactsArray = {"Toto" /* pour montrer que la liste sera ordonnée */, "Amel", "Antoine", "Bastien", "Guillaume"};
        int i = 0;
        for (String s : contactsArray) {
            list.add(new User(i++, s));
        }
        Collections.sort(list);
        adapter.notifyDataSetChanged();
        /*
        new RequestGET(new ICallback<String>() {
            @Override
            public void success(String result) {
                // TODO: Parse result, create User item, add users to list, and notifyDataSetChanged the adapter.
                Log.i(TAG, "Success : " + result);
            }

            @Override
            public void failure(Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }, Utils.getToken(this), BASE_URL + GET_ALL_USERS).execute();
        */
    }
}
