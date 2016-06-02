package ch.heigvd.gen.services;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

import ch.heigvd.gen.communications.RequestGET;
import ch.heigvd.gen.interfaces.ICallback;
import ch.heigvd.gen.interfaces.ICustomCallback;
import ch.heigvd.gen.interfaces.IJSONKeys;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.models.Message;
import ch.heigvd.gen.models.User;
import ch.heigvd.gen.utilities.Utils;

/**
 * Created by antoi on 01.06.2016.
 */
public class EventService implements IRequests, IJSONKeys {

    private final static String TAG = EventService.class.getSimpleName();

    ICustomCallback currentCallbackActivity = null;
    Activity currentActivity = null;
    Thread thread;
    private static EventService mInstance;

    private String token;

    private EventService(){

    }
    public static synchronized EventService getInstance() {
        if(mInstance == null)
            mInstance = new EventService();
        return mInstance;
    }

    public void setActivity(ICustomCallback callbackActivity, Activity activity){
        currentCallbackActivity = callbackActivity;
        currentActivity = activity;
        token = Utils.getToken(activity);
    }

    public void removeActivity(){
        currentActivity = null;
        currentCallbackActivity = null;
    }

    public void start(){
        thread = new Thread() {
            public void run() {
                while (true) {
                    Log.i(TAG, "Trying to retrieve events !");
                    try {
                        Log.i(TAG, "Activity is set !");
                        /**
                         * Gestion des events
                         */
                        RequestGET get = new RequestGET(new ICallback<String>() {
                            @Override
                            public void success(String result) {
                                Log.i(TAG, "Success : " + result);
                                try {
                                    JSONObject json = new JSONObject(result);
                                    JSONArray jsonEvents = json.getJSONArray("events");
                                    for (int i = 0; i < jsonEvents.length(); i++) {
                                        JSONObject jsonEvent = jsonEvents.getJSONObject(i);
                                        String type = jsonEvent.getString("type");
                                        switch (type) {
                                            case "CONTACT_ADDED":
                                                // Update contacts
                                                addContact(jsonEvent);
                                            case "CONTACT_REMOVED":
                                                // Update contacts
                                                removeContact(jsonEvent);
                                                break;
                                            case "PRIVATE_MESSAGE_RECEIVED":
                                                // Load new messages
                                                loadNewMessages(jsonEvent);
                                                break;
                                            default:
                                                Log.i(TAG, "No event ! : " + result);
                                                break;
                                        }
                                    }
                                    /**
                                     * TODO : Gérer si next vaut quelque chose
                                     */
                                    Log.i(TAG, "Success : " + result);
                                } catch (Exception ex) {
                                    Log.e(TAG, ex.getMessage());
                                }
                            }

                            @Override
                            public void failure(Exception ex) {
                                Log.e(TAG, ex.getMessage());
                            }
                        }, token, BASE_URL + EVENTS);

                        get.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        get.get();
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }catch(ExecutionException e){
                        e.printStackTrace();
                    }
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    private void removeContact(JSONObject jsonEvent) throws JSONException {
        User.deleteById(jsonEvent.getInt("contact"));
        updateCallbackActivity();
    }

    private void addContact(JSONObject jsonEvent) throws JSONException {
        try {
            Log.i(TAG, "Token : " + token);
            new RequestGET(new ICallback<String>() {
                @Override
                public void success(String result) {
                    JSONObject jsonUser = null;
                    try {
                        jsonUser = new JSONObject(result);
                        User user = new User(jsonUser.getInt("id"), jsonUser.getString("name"), jsonUser.getBoolean("admin"));
                        User.users.add(user);
                        updateCallbackActivity();
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }

                    Log.i(TAG, "Success : " + result);
                }

                @Override
                public void failure(Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }, token, BASE_URL + GET_CONTACT + jsonEvent.getInt("contact")).execute();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    private void loadNewMessages(JSONObject jsonEvent) throws JSONException {
        final int id = jsonEvent.getInt("from");
        new RequestGET(new ICallback<String>() {
            @Override
            public void success(String result) {
                try {
                    // Load new messages
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(result);
                        for (int i = jsonArray.length() - 1; i >= 0; i--) {
                            JSONObject jsonMessage = jsonArray.getJSONObject(i);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            User.findById(id).addMessage(new Message(jsonMessage.getInt("from"), jsonMessage.getString("text"), sdf.parse(jsonMessage.getString("date")), jsonMessage.getInt("id")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    updateCallbackActivity();
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
                Log.i(TAG, "Success : " + result);
            }

            @Override
            public void failure(Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }, token, BASE_URL + id + GET_MESSAGES +
                (User.findById(id).getMessages().size() > 0 ? "?from=" + User.findById(id).getMessages().get(User.findById(id).getMessages().size() - 1) : "")).execute();
    }

    private void updateCallbackActivity() {
        if (currentCallbackActivity != null) {
            currentCallbackActivity.update();
        }
    }

    public void stop(){
        thread.interrupt();
    }
}
