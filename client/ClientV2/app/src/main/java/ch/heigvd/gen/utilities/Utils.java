package ch.heigvd.gen.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utilitaries class for the messaging application
 */
public final class Utils {

    /**
     * Get the authentication token for the current user
     *
     * @param a the current activity
     * @return the Token
     */
    public static String getToken(final Activity a) {
        SharedPreferences prefs = a.getSharedPreferences("Authentication", Context.MODE_PRIVATE);
        return prefs.getString("Token", "");
    }

    /**
     * Set the authentication token for the current user
     *
     * @param a
     * @param token
     */
    public static void setToken(final Activity a, final String token) {
        SharedPreferences prefs = a.getSharedPreferences("Authentication", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Token", token);
        editor.commit();
    }

    /**
     * Get the id of the current user
     *
     * @param a the current activity
     * @return the id
     */
    public static int getId(final Activity a) {
        SharedPreferences prefs = a.getSharedPreferences("Authentication", Context.MODE_PRIVATE);
        return prefs.getInt("Id", 0);
    }

    /**
     * Set the id of the current user
     *
     * @param a  the current activity
     * @param id the id
     */
    public static void setId(final Activity a, final int id) {
        SharedPreferences prefs = a.getSharedPreferences("Authentication", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("Id", id);
        editor.commit();
    }

    /**
     * Get the user's current shared preferencies
     *
     * @param a the current activity
     * @return the User's preferencies as a String
     */
    public static String getUser(final Activity a) {
        SharedPreferences prefs = a.getSharedPreferences("Authentication", Context.MODE_PRIVATE);
        return prefs.getString("User", "");
    }

    /**
     * Set the user's current shared preferencies
     *
     * @param a    the current activity
     * @param user the User's preferencies as a String
     */
    public static void setUser(final Activity a, final String user) {
        SharedPreferences prefs = a.getSharedPreferences("Authentication", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("User", user);
        editor.commit();
    }

    /**
     * Generates a JSON object
     *
     * @param keys   an array of the keys
     * @param values an array of the values
     * @return The JSON Object as a String
     * @throws Exception a JSON exception
     */
    public static String createJSONObject(String[] keys, String[] values) throws Exception {
        if (keys.length != values.length) {
            throw new Exception("Keys list and values list do not have the same number of elements !");
        }
        String result = "";
        JSONObject obj = new JSONObject();
        try {
            for (int i = 0; i < keys.length; ++i) {
                obj.put(keys[i], values[i]);
            }
            result = obj.toString();
        } catch (JSONException e) {
            throw e;
        }
        return result;
    }

    /**
     * Display an error message
     *
     * @param context the current context
     * @param message the message to display
     */
    public static void showAlert(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
