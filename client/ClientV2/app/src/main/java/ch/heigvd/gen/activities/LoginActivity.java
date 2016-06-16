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
 * TODO
 * TODO : Mettre toutes les String dans les fichiers de ressources fait pour
 * TODO : Commenter/indenter/Trier imports, compléter la javadoc
 * TODO : Mettre tous les éléments json et les requêtes dans IJSONKEYS et IREQUESTS
 * <p/>
 * TODO : Trier les utilisateurs par de date creation si aucun message
 * <p/>
 * TODO : Modifier les requêtes pour charger les membres et les messages d'un group pour que ça soit fait une seule fois dans les events
 * <p/>
 * TODO : Faire les report/blocage d'utilisateur et report de message dans groupe
 */

/**
 * First activity called at startup of our application, provides the login and account creation
 * functionnalities
 *
 */
public class LoginActivity extends AppCompatActivity implements IJSONKeys, IRequests {

    private final static String TAG = LoginActivity.class.getSimpleName();

    private EditText login;
    private EditText passwordBox;

    /**
     * Called when the activity is first created, sets up two EditText fields allowing the user to
     * provide his username and password. Two buttons are also displayed, the first one to validate
     * the login, the second one to launch the RegisterActivity in order to create an account.
     *
     * @param savedInstanceState a potential previous state saved
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (EditText) findViewById(R.id.login);
        passwordBox = (EditText) findViewById(R.id.password);
    }

    /**
     * Executes an HTTP POST request to attempt the login with the provided credentials
     *
     * @param view the current view
     */
    public void login(final View view) {
        if (login.getText().toString().isEmpty()) {
            login.setError(getString(R.string.error_required));
        } else if (passwordBox.getText().toString().isEmpty()) {
            passwordBox.setError(getString(R.string.error_required));
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
                            Utils.setToken(LoginActivity.this, json.getString("token"));
                            Log.i(TAG, "Token : " + json.getString("token"));
                        } catch (Exception ex) {
                            Log.e(TAG, ex.getMessage());
                        }
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        Log.i(TAG, "Success : " + result);
                    }

                    @Override
                    public void failure(Exception ex) {
                        try {
                            Utils.showAlert(LoginActivity.this, new JSONObject(ex.getMessage()).getString("err"));
                        } catch (JSONException e) {
                            Utils.showAlert(LoginActivity.this, ex.getMessage());
                            Log.e(TAG, e.getMessage());
                        }
                        Log.e(TAG, ex.getMessage());
                    }
                }, Utils.getToken(this), BASE_URL + LOGIN, content).execute();
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }
    }

    /**
     * Start the RegisterActivity in order to create an account.
     *
     * @param view the current view
     */
    public void register(final View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}

