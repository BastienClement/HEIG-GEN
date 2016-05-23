package ch.heigvd.gen.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import ch.heigvd.gen.R;
import ch.heigvd.gen.communications.RequestPOST;
import ch.heigvd.gen.interfaces.ICallback;
import ch.heigvd.gen.interfaces.IJSONKeys;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.utilities.Utils;

public class RegisterActivity extends AppCompatActivity implements IJSONKeys, IRequests {

    private final static String TAG = RegisterActivity.class.getSimpleName();

    private EditText login;
    private EditText passwordBox;
    private EditText passwordConfirmationBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

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
                        finishActivity(0);
                        Log.i(TAG, "Success : " + result);
                    }

                    @Override
                    public void failure(Exception ex) {
                        Log.e(TAG, ex.getMessage());
                    }
                }, Utils.getToken(this), REGISTER, content).execute();
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }
    }

    private void init() {
        login = (EditText)findViewById(R.id.login);
        passwordBox = (EditText)findViewById(R.id.password);
        passwordConfirmationBox = (EditText)findViewById(R.id.passwordConfirmation);
    }
}
