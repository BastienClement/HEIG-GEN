package ch.heigvd.gen.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ch.heigvd.gen.R;
import ch.heigvd.gen.communications.RequestDELETE;
import ch.heigvd.gen.communications.RequestPOST;
import ch.heigvd.gen.interfaces.ICallback;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.utilities.Utils;

public class ContactEditActivity extends AppCompatActivity implements IRequests {

    private final static String TAG = ContactEditActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edit);

        // get contact
        final Bundle b = getIntent().getExtras();
        String contact = null;
        int id;
        if(b != null) {
            contact = b.getString("contact");
            id = b.getInt("id");
        }

        // enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // set contact name
        TextView title = (TextView) findViewById(R.id.contact_name);
        title.setText(contact);

        Button button = (Button) findViewById(R.id.remove_button);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                try {
                    String[] keys = new String[]{};
                    String[] values = new String[]{};
                    String content = Utils.createJSONObject(keys, values);
                    new RequestDELETE(new ICallback<String>() {
                        @Override
                        public void success(String result) {
                            //Utils.setToken(result.toString());
                            Intent intent = new Intent(ContactEditActivity.this, ContactViewActivity.class);
                            intent.putExtras(b);
                            startActivity(intent);
                            Log.i(TAG, "Success : " + result);
                        }

                        @Override
                        public void failure(Exception ex) {
                            Log.e(TAG, ex.getMessage());
                        }
                    }, Utils.getToken(ContactEditActivity.this), BASE_URL + LOGIN + b.getInt("id"), content).execute();
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
        });
    }
}
