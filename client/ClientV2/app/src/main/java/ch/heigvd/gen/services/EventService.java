package ch.heigvd.gen.services;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ch.heigvd.gen.communications.RequestGET;
import ch.heigvd.gen.interfaces.ICallback;
import ch.heigvd.gen.interfaces.ICustomCallback;
import ch.heigvd.gen.interfaces.IJSONKeys;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.models.Group;
import ch.heigvd.gen.models.Message;
import ch.heigvd.gen.models.User;
import ch.heigvd.gen.utilities.Utils;

/**
 * Event service Manager
 */
public class EventService implements IRequests, IJSONKeys {

    private final static String TAG = EventService.class.getSimpleName();

    ICustomCallback currentCallbackActivity = null;
    Thread thread;
    private static EventService mInstance;

    private String token;

    private int id;

    private Integer from = null;

    private EventService(){

    }
    public static synchronized EventService getInstance() {
        if(mInstance == null)
            mInstance = new EventService();
        return mInstance;
    }

    public void setActivity(ICustomCallback callbackActivity){
        currentCallbackActivity = callbackActivity;
    }

    public void removeActivity(){
        currentCallbackActivity = null;
    }

    public void start(Activity activity){
        Log.i(TAG, "Starting event service thread !");
        token = Utils.getToken(activity);
        id = Utils.getId(activity);
        thread = new Thread() {
            public void run() {
                while (true) {
                    Log.i(TAG, "Trying to retrieve events !");
                    RequestGET get = null;
                    try {
                        // Event management
                        get = new RequestGET(new ICallback<String>() {
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
                                    ex.printStackTrace();
                                    Log.e(TAG, "Error : " + ex.getMessage());
                                }
                            }

                            @Override
                            public void failure(Exception ex) {
                                Log.e(TAG, ex.getMessage());
                            }
                        }, token, BASE_URL + EVENTS +  (from == null ? "" : "?from=" + from));

                        get.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        get.get();
                        sleep(1000);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                        get.cancel(true);
                        break;
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
                //User.findById(jsonEvent.getInt("contact")).setUnread(false);
                //updateCallbackActivity();
                break;
            case "PRIVATE_MESSAGES_READ":
                //User.findById(jsonEvent.getInt("contact")).setUnread(true);
                //updateCallbackActivity();
                break;
            case "GROUP_USER_KICKED":
                removeGroupMember(jsonEvent);
                break;
            case "GROUPS_UNREAD":
                break;
            case "GROUPS_READ":
                break;
            case "CONTACT_ADDED":
                // Add contact
                addContact(jsonEvent);
                break;
            case "GROUP_USER_INVITED":
                // Add group
                /**
                 * TODO : Only update the new user
                 */
                addGroup(jsonEvent);
                break;
            case "CONTACT_REMOVED":
                // Update contacts
                removeContact(jsonEvent);
                break;
            case "GROUP_DELETED":
                removeGroup(jsonEvent);
                break;
            case "PRIVATE_MESSAGES_UPDATED":
                // Load new messages
                User.findById(jsonEvent.getInt("contact")).setUnread(true);
                loadNewContactMessages(jsonEvent);
                break;
            case "GROUP_MESSAGES_UPDATED":
                Group.findById(jsonEvent.getInt("group")).setUnread(true);
                loadNewGroupMessages(jsonEvent);
                break;
            default:
                Log.e(TAG, "Unhandled event !");
                break;
        }
    }

    private void removeGroupMember(final JSONObject jsonEvent) throws JSONException{
        try {
                new RequestGET(new ICallback<String>() {
                @Override
                public void success(String result) {
                    try{
                        JSONObject json = new JSONObject(result);

                        Group group = Group.findById(jsonEvent.getInt("group"));
                        if(jsonEvent.getInt("user") == json.getInt("id")){
                            Group.groups.remove(group);
                        } else {
                            group.deleteMemberById(jsonEvent.getInt("user"));
                        }
                        updateCallbackActivity();
                        Log.i(TAG, "Id : " + json.getString("id"));
                    } catch (Exception ex){
                        Log.e(TAG, ex.getMessage());
                    }
                    Log.i(TAG, "Success : " + result);
                }

                @Override
                public void failure(Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }, token, BASE_URL + GET_SELF).execute();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    private void removeGroup(JSONObject jsonEvent) throws JSONException {
        Group.deleteById(jsonEvent.getInt("group"));
        updateCallbackActivity();
    }

    private void loadNewGroupMessages(JSONObject jsonEvent) throws JSONException {
        final int id = jsonEvent.getInt("group");
        final List<Message> messages = Group.findById(id).getMessages();
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
                            Group.findById(id).addMessage(new Message(jsonMessage.getInt("user"), jsonMessage.getString("text"), sdf.parse(jsonMessage.getString("date")), jsonMessage.getInt("id")));
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
        }, token, BASE_URL + GET_GROUP + id + GET_MESSAGES +
                (messages.size() > 0 ? ("?from=" + messages.get(messages.size() - 1).getId()) : "")).execute();
    }

    private void addGroup(JSONObject jsonEvent) {
        try {
            new RequestGET(new ICallback<String>() {
                @Override
                public void success(String result) {
                    JSONObject jsonGroup = null;
                    try {
                        jsonGroup = new JSONObject(result);
                        if(Group.findById(jsonGroup.getInt("id")) == null) {
                            final Group group = new Group(jsonGroup.getInt("id"), jsonGroup.getString("title"), false);
                            Group.groups.add(group);
                            updateCallbackActivity();
                        }
                        loadMembers(Group.findById(jsonGroup.getInt("id")));
                        loadGroupMessages(Group.findById(jsonGroup.getInt("id")));
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    Log.i(TAG, "Success : " + result);
                }

                @Override
                public void failure(Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }, token, BASE_URL + GET_GROUP + jsonEvent.getInt("group")).execute();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    private void loadMembers(final Group group) throws ExecutionException, InterruptedException {
        new RequestGET(new ICallback<String>() {
            @Override
            public void success(String result) {
                try {
                    JSONArray jsonMembers = new JSONArray(result);
                    for (int i=0; i<jsonMembers.length(); i++) {
                        final JSONObject member = jsonMembers.getJSONObject(i);
                        if(!group.hasMemberWithId(member.getInt("user"))) {
                            new RequestGET(new ICallback<String>() {
                                @Override
                                public void success(String result) {
                                    JSONObject jsonUser = null;
                                    try {
                                        jsonUser = new JSONObject(result);

                                        // Retrieve members
                                        group.getMembers().add(new User(jsonUser.getInt("id"), jsonUser.getString("name"), member.getBoolean("admin"), false));
                                    } catch (JSONException e) {
                                        Log.e(TAG, e.getMessage());
                                    }
                                    updateCallbackActivity();
                                    Log.i(TAG, "Success : " + result);
                                }

                                @Override
                                public void failure(Exception ex) {
                                    Log.e(TAG, ex.getMessage());
                                }
                            }, token, BASE_URL + GET_USER + member.getInt("user")).execute();
                        }

                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }

                Log.i(TAG, "Success : " + result);
            }

            @Override
            public void failure(Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }, token, BASE_URL + GET_GROUP + group.getId() + GET_MEMBERS).execute();
    }

    /**
     * TODO
     *
     */
    private void loadGroupMessages(final Group group) throws ExecutionException, InterruptedException {
        new RequestGET(new ICallback<String>() {
            @Override
            public void success(String result) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(result);
                    for (int i = jsonArray.length() - 1; i >= 0; i--) {
                        JSONObject jsonMessage = jsonArray.getJSONObject(i);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        group.addMessage(new Message(jsonMessage.getInt("from"), jsonMessage.getString("text"), sdf.parse(jsonMessage.getString("date")), jsonMessage.getInt("id")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "Success : " + result);
            }

            @Override
            public void failure(Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }, token, BASE_URL + GET_GROUP + group.getId() + GET_MESSAGES).execute();
    }

    private void removeContact(JSONObject jsonEvent) throws JSONException {
        User.deleteById(jsonEvent.getInt("contact"));
        updateCallbackActivity();
    }

    private void addContact(JSONObject jsonEvent) throws JSONException {
        try {
            new RequestGET(new ICallback<String>() {
                @Override
                public void success(String result) {
                    JSONObject jsonUser = null;
                    try {
                        jsonUser = new JSONObject(result);
                        User user = new User(jsonUser.getInt("id"), jsonUser.getString("name"), jsonUser.getBoolean("admin"), false);
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

    private void loadNewContactMessages(JSONObject jsonEvent) throws JSONException {
        final int id = jsonEvent.getInt("contact");
        final List<Message> messages = User.findById(id).getMessages();
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
                (messages.size() > 0 ? ("?from=" + messages.get(messages.size() - 1).getId()) : "")).execute();
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
