package ch.heigvd.gen.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ch.heigvd.gen.R;
import ch.heigvd.gen.communications.RequestDELETE;
import ch.heigvd.gen.interfaces.ICallback;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.utilities.Utils;

/**
 * The Activity allowing to remove a Contact
 */
public class ContactEditActivity extends AppCompatActivity implements IRequests {

    private final static String TAG = ContactEditActivity.class.getSimpleName();

    Bundle b = null;

    /**
     * Called when the activity is first created, sets the TextVuew displaying the contact name
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edit);

        // get bundle
        b = getIntent().getExtras();

        // enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // set contact name
        TextView title = (TextView) findViewById(R.id.contact_name);
        title.setText(b.getString("user_name"));
    }

    /**
     * Execute and HTTP in order to remove a contact from the user's contact list, then go back to
     * the ContactDiscussionActivity
     *
     * @param view the current view
     */
    public void removeContact(final View view) {
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
                        Utils.showAlert(ContactEditActivity.this, new JSONObject(ex.getMessage()).getString("err"));
                    } catch (JSONException e) {
                        Utils.showAlert(ContactEditActivity.this, ex.getMessage());
                        Log.e(TAG, e.getMessage());
                    }
                    Log.e(TAG, ex.getMessage());
                }
            }, Utils.getToken(ContactEditActivity.this), BASE_URL + GET_CONTACT + b.getInt("user_id"));
            delete.execute();
            delete.get();
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
