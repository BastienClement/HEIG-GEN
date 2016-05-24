package ch.heigvd.gen.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import ch.heigvd.gen.R;
import ch.heigvd.gen.models.User;

public class ContactListActivity extends AppCompatActivity {

    // simple test contacts
    private User[] contactsArray = { new User(1, "Amel"), new User(2, "Antoine"), new User(3, "Bastien"), new User(4, "Guillaume")};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        // enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // todo : load contacts (local storage)

        // create adapter
        // TODO : Order by last message
        ArrayAdapter adapter = new ArrayAdapter<User>(this, R.layout.contacts_list_item, contactsArray);

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
                Intent intent = new Intent(ContactListActivity.this, ContactViewActivity.class);
                Bundle b = new Bundle();
                b.putString("contact", item.getUsername());
                b.putInt("id", item.getId());
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
}
