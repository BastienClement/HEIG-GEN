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

    private Integer from = null;

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
        token = Utils.getToken(currentActivity);
    }

    public void removeActivity(){
        Log.i(TAG, "Activity removed");
        currentActivity = null;
        currentCallbackActivity = null;
    }

    public void start(){
        Log.i(TAG, "Starting event service thread !");
        thread = new Thread() {
            public void run() {
                while (true) {
                    Log.i(TAG, "Trying to retrieve events !");
                    try {
                        Log.i(TAG, "Activity is set !");
                        // Event management
                        RequestGET get = new RequestGET(new ICallback<String>() {
                            @Override
                            public void success(String result) {
                                Log.i(TAG, "Success : " + result);
                                try {
                                    JSONObject json = new JSONObject(result);
                                    JSONArray jsonEvents = json.getJSONArray("events");
                                    for (int i = 0; i < jsonEvents.length(); i++) {
                                        JSONObject jsonEvent = jsonEvents.getJSONObject(i);
                                        handleEvent(jsonEvent);
                                    }
                                    from = json.getInt("next");
                                } catch (Exception ex) {
                                    Log.e(TAG, ex.getMessage());
                                }
                            }

                            @Override
                            public void failure(Exception ex) {
                                Log.e(TAG, ex.getMessage());
                            }
                        }, token, BASE_URL + EVENTS +  (from == null ? "" : "?from=" + from));
                        Log.i(TAG, "Get events on url : " + BASE_URL + EVENTS +  (from == null ? "" : "?from=" + from));

                        get.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        get.get();


                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }catch(ExecutionException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    private void handleEvent(JSONObject jsonEvent) throws JSONException {
        String type = jsonEvent.getString("type");
        switch (type) {
            case "PRIVATE_MESSAGES_UNREAD":
                User.findById(jsonEvent.getInt("contact")).setUnread(false);
                break;
            case "PRIVATE_MESSAGES_READ":
                User.findById(jsonEvent.getInt("contact")).setUnread(true);
                break;
            case "CONTACT_ADDED":
                // Add contact
                addContact(jsonEvent);
                break;
            case "CONTACT_REMOVED":
                // Update contacts
                removeContact(jsonEvent);
                break;
            case "PRIVATE_MESSAGES_UPDATED":
                // Load new messages
                User.findById(jsonEvent.getInt("contact")).setUnread(true);
                loadNewMessages(jsonEvent);
                break;
            default:
                Log.e(TAG, "UNHANDLED EVENT !");
                break;
        }
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
                        User user = new User(jsonUser.getInt("id"), jsonUser.getString("name"), jsonUser.getBoolean("admin"), jsonUser.getBoolean("unread"));
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
        final int id = jsonEvent.getInt("contact");
        new RequestGET(new ICallback<String>() {
            @Override
            public void success(String result) {
                try {
                    System.out.println("LOAD MESSAGES DSDS D");
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
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
                updateCallbackActivity();
                Log.i(TAG, "Success : " + result);
            }

            @Override
            public void failure(Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }, token, BASE_URL + GET_CONTACT + id + GET_MESSAGES +
                (User.findById(id).getMessages().size() > 0 ? ("?from=" + User.findById(id).getMessages().get(0)) : "")).execute();
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
