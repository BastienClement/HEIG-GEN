package ch.heigvd.gen.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * TODO
 */
public final class Utils {

    /**
     * TODO
     *
     * @param a
     * @return
     */
    public static String getToken(final Activity a) {
        SharedPreferences prefs = a.getSharedPreferences("Authentication", Context.MODE_PRIVATE);
        return prefs.getString("Token", "");
    }

    /**
     * TODO
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
     * TODO
     *
     * @param a
     * @return
     */
    public static int getId(final Activity a) {
        SharedPreferences prefs = a.getSharedPreferences("Authentication", Context.MODE_PRIVATE);
        return prefs.getInt("Id", 0);
    }

    /**
     * TODO
     *
     * @param a
     * @param id
     */
    public static void setId(final Activity a, final int id) {
        SharedPreferences prefs = a.getSharedPreferences("Authentication", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("Id", id);
        editor.commit();
    }

    /**
     * TODO
     *
     * @param a
     * @return
     */
    public static String getUser(final Activity a) {
        SharedPreferences prefs = a.getSharedPreferences("Authentication", Context.MODE_PRIVATE);
        return prefs.getString("User", "");
    }

    /**
     * TODO
     *
     * @param a
     * @param user
     */
    public static void setUser(final Activity a, final String user) {
        SharedPreferences prefs = a.getSharedPreferences("Authentication", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("User", user);
        editor.commit();
    }

    /**
     * TODO
     *
     * @param keys
     * @param values
     * @return
     * @throws Exception
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
     * TODO
     * 
     * @param context
     * @param message
     */
    public static void showAlert(Context context, String message){
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
