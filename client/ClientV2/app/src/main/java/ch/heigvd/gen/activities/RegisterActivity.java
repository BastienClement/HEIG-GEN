package ch.heigvd.gen.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
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
 * TODO
 */
public class RegisterActivity extends AppCompatActivity implements IJSONKeys, IRequests {

    private final static String TAG = RegisterActivity.class.getSimpleName();

    private EditText login;
    private EditText passwordBox;
    private EditText passwordConfirmationBox;

    /**
     * TODO
     *
     * @param savedInstanceState
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
     * TODO
     *
     * @param view
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
                        try{
                            JSONObject json = new JSONObject(result);
                            Utils.setToken(RegisterActivity.this, json.getString("token"));
                            Log.i(TAG, "Token : " + json.getString("token"));
                        } catch (Exception ex){
                            Log.e(TAG, ex.getMessage());
                        }
                        Intent intent = new Intent(RegisterActivity.this, ContactListActivity.class);
                        startActivity(intent);
                        Log.i(TAG, "Success : " + result);
                    }

                    @Override
                    public void failure(Exception ex) {
                        try {
                            Utils.showAlert(RegisterActivity.this, new JSONObject(ex.getMessage()).getString("err"));
                        } catch (JSONException e) {
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
     * TODO
     */
    private void init() {
        login = (EditText)findViewById(R.id.login);
        passwordBox = (EditText)findViewById(R.id.password);
        passwordConfirmationBox = (EditText)findViewById(R.id.passwordConfirmation);
    }
}
