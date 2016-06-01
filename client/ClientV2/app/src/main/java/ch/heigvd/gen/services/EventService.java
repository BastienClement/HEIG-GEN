package ch.heigvd.gen.services;

import android.app.Activity;
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
    }
    public void removeActivity(){
        currentActivity = null;
    }
    public void start(){
        thread = new Thread() {
            public void run() {
                while (true) {
                    Log.i(TAG, "Trying to retrieve events !");
                    try {
                        if(currentActivity != null) {
                            Log.i(TAG, "Activity is set !");
                            /**
                             * Gestion des events
                             */
                            RequestGET get = new RequestGET(new ICallback<String>() {
                                @Override
                                public void success(String result) {
                                    Log.i(TAG, "Success : " + result);
                                    try{
                                        JSONObject json = new JSONObject(result);
                                        JSONObject jsonEvent = json.getJSONObject("events");
                                        String type = jsonEvent.getString("type");
                                        final int id = jsonEvent.getInt("from");
                                        switch(type){
                                            case "private_chat" :
                                                new RequestGET(new ICallback<String>() {
                                                    @Override
                                                    public void success(String result) {
                                                        try{
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
                                                            currentCallbackActivity.update();
                                                        } catch (Exception ex){
                                                            Log.e(TAG, ex.getMessage());
                                                        }
                                                        Log.i(TAG, "Success : " + result);
                                                    }

                                                    @Override
                                                    public void failure(Exception ex) {
                                                        Log.e(TAG, ex.getMessage());
                                                    }
                                                }, Utils.getToken(currentActivity), BASE_URL + id + GET_MESSAGES +
                                                        (User.findById(id).getMessages().size() > 0 ? "?from=" + User.findById(id).getMessages().get(User.findById(id).getMessages().size() - 1) : "")).execute();
                                                break;
                                            default :
                                                Log.i(TAG, "No event ! : " + result);
                                                break;
                                        }
                                        /**
                                         * TODO : Gérer si next vaut quelque chose
                                         */
                                        currentCallbackActivity.update();
                                        Log.i(TAG, "Success : " + result);
                                    } catch (Exception ex){
                                        Log.e(TAG, ex.getMessage());
                                    }
                                }

                                @Override
                                public void failure(Exception ex) {
                                    Log.e(TAG, ex.getMessage());
                                }
                            }, Utils.getToken(currentActivity), BASE_URL + GET_EVENTS);

                            get.execute();
                            get.get();
                        }
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }
    public void stop(){
        thread.interrupt();
    }
}
