package ch.heigvd.gen.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import ch.heigvd.gen.R;
import ch.heigvd.gen.communications.RequestPOST;
import ch.heigvd.gen.interfaces.ICallback;
import ch.heigvd.gen.interfaces.IJSONKeys;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.utilities.Utils;

/**
 * This activity provides the account creation functionnality, allowing users to register for the
 * messaging application
 */
public class RegisterActivity extends AppCompatActivity implements IJSONKeys, IRequests {

    private final static String TAG = RegisterActivity.class.getSimpleName();

    private EditText login;
    private EditText passwordBox;
    private EditText passwordConfirmationBox;

    /**
     * Called when the activity is first created, sets up three EditText fields allowing the user to
     * provide the username he wants to use and his new password, with a confirmation field.
     * A register button is also displayed to validate the account creation.
     *
     * @param savedInstanceState a potential previous state saved
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

        // enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Thid method is called to try and validate the account creation towards the server. It
     * executes an HTTP POST request to transmit the registering query.
     *
     * @param view the current view
     */
    public void createAccount(final View view) {
        if (login.getText().toString().isEmpty()) {
            login.setError(getString(R.string.error_required));
        } else if (passwordBox.getText().toString().isEmpty()) {
            passwordBox.setError(getString(R.string.error_required));
        } else if (passwordConfirmationBox.getText().toString().isEmpty()) {
            passwordConfirmationBox.setError(getString(R.string.error_required));
        } else if (!passwordBox.getText().toString().equals(passwordConfirmationBox.getText().toString())) {
            passwordConfirmationBox.setError(getString(R.string.error_passwords_dont_match));
        } else {
            try {
                String[] keys = new String[]{KEY_LOGIN, KEY_PASSWORD};
                String[] values = new String[]{login.getText().toString(), passwordBox.getText().toString()};
                String content = Utils.createJSONObject(keys, values);
                new RequestPOST(new ICallback<String>() {
                    @Override
                    public void success(String result) {
                        try {
                            JSONObject json = new JSONObject(result);
                            Utils.setToken(RegisterActivity.this, json.getString("token"));
                            Log.i(TAG, "Token : " + json.getString("token"));
                        } catch (Exception ex) {
                            Log.e(TAG, ex.getMessage());
                        }
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        Log.i(TAG, "Success : " + result);
                    }

                    @Override
                    public void failure(Exception ex) {
                        try {
                            Utils.showAlert(RegisterActivity.this, new JSONObject(ex.getMessage()).getString("err"));
                        } catch (JSONException e) {
                            Utils.showAlert(RegisterActivity.this, ex.getMessage());
                            Log.e(TAG, e.getMessage());
                        }
                        Log.e(TAG, ex.getMessage());
                    }
                }, Utils.getToken(this), BASE_URL + REGISTER, content).execute();
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }
    }

    /**
     * Initializes EditText fields
     */
    private void init() {
        login = (EditText) findViewById(R.id.login);
        passwordBox = (EditText) findViewById(R.id.password);
        passwordConfirmationBox = (EditText) findViewById(R.id.passwordConfirmation);
    }
}
