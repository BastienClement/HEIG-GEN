package ch.heigvd.gen.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

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
 * TODO : Faire des logs un peu mieux
 *
 * TODO : Faire que les dialogues dans une discussion soient joli + afficher l'heure du message et le jour et le nom de l'utilisateur qui a envoyé pour les discussions de groupe
 *
 * TODO : Trier les utilisateurs par de date creation si aucun message
 *
 * TODO : Faire fonctionner les erreurs quand on perd la connection avec le server
 *
 * TODO : Faire fonctionner la recherche dans la liste des groupes/contact ça marche plus avec les fragments
 *
 * TODO : Message read unread faire dans les events
 *
 * TODO : faire les requêtes pour charger les membres et les messages d'un group pour que ça soit fait une seule fois dans les events
 *
 * TODO won't be done : Faire les report/blocage d'utilisateur et report de message dans groupe
 */
public class LoginActivity extends AppCompatActivity implements IJSONKeys, IRequests {

    private final static String TAG = LoginActivity.class.getSimpleName();

    private RadioGroup radioGroup;
    private EditText login;
    private EditText passwordBox;
    private TextInputLayout server;

    /**
     * TODO
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (EditText) findViewById(R.id.login);
        passwordBox = (EditText) findViewById(R.id.password);
    }

    /**
     * TODO
     *
     * @param view
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
                        try{
                            JSONObject json = new JSONObject(result);
                            Utils.setToken(LoginActivity.this, json.getString("token"));
                            Log.i(TAG, "Token : " + json.getString("token"));
                        } catch (Exception ex){
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
     * TODO
     *
     * @param view
     */
    public void register(final View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}

